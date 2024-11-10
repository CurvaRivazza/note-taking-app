package com.example.quicknotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    private val _allItems = MutableLiveData<List<Any>>()
    val allItems: LiveData<List<Any>> = _allItems

    private var currentFolderId: Int? = null

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        val foldersDao = NoteDatabase.getDatabase(application).foldersDao()
        repository = NoteRepository(NoteDatabase.getDatabase(application))

        loadItems()
    }

    fun loadItems(folderId: Int? = null) {
        currentFolderId = folderId
        viewModelScope.launch {
            val notes =
                if (folderId == null) repository.getAllNotes() else repository.getNotesByFolderId(
                    folderId
                )
            val folders =
                if (folderId == null) repository.getAllFolders() else repository.getFoldersByParentId(
                    folderId
                )
            val items = mutableListOf<Any>()
            items.addAll(folders)
            items.addAll(notes)
            _allItems.value = items
        }
    }

    fun getNoteById(noteId: Int): LiveData<Note> {
        return repository.getNoteById(noteId)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        repository.updateNote(note)
    }

    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
        loadItems(currentFolderId)
    }

    fun insertFolder(folder: Folder) = viewModelScope.launch {
        repository.insertFolder(folder)
        loadItems(currentFolderId)
    }
}
