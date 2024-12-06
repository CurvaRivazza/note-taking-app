package com.example.quicknotes

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    private val _allItems = MutableLiveData<List<Any>>()
    val allItems: LiveData<List<Any>> = _allItems
    private val _allFolders = MutableLiveData<List<Folder>>()
    val allFolders: LiveData<List<Folder>> = _allFolders

    private var currentFolderId: Int? = null

    private val _folderName = MutableLiveData<String>()
    val folderName: LiveData<String> = _folderName

    init {
        repository = NoteRepository(NoteDatabase.getDatabase(application))
        loadItems()
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch {
            _allFolders.value = repository.getAllFolders()
        }
    }

    fun loadItems(folderId: Int? = null) {
        currentFolderId = folderId
        viewModelScope.launch {
            val notes = if (folderId == null) {
                repository.getNotesByFolderId(null)
            } else {
                repository.getNotesByFolderId(folderId)
            }
            val folders = if (folderId == null) {
                repository.getFoldersByParentId(null)
            } else {
                repository.getFoldersByParentId(folderId)
            }
            val items = mutableListOf<Any>()
            items.addAll(folders)
            items.addAll(notes)
            _allItems.value = items
        }
    }

    fun getFolderById(folderId: Int?, context: Context) {
        if(folderId == null){
            _folderName.value = ""
        } else {
            viewModelScope.launch {
                val folder = repository.getFolderById(folderId)
                _folderName.value = folder.name
            }
        }
    }

    fun getAllFolders(): List<Folder> {
        var liveData: List<Folder> = mutableListOf()
        viewModelScope.launch {
            liveData = repository.getAllFolders()
        }
        return liveData
    }

    fun getNoteById(noteId: Int): LiveData<Note> {
        return repository.getNoteById(noteId)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        repository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
        loadItems(currentFolderId)
    }

    fun insertFolder(folder: Folder) = viewModelScope.launch {
        repository.insertFolder(folder)
        loadItems(currentFolderId)
    }

    fun deleteFolder(folder: Folder) = viewModelScope.launch {
        repository.deleteFolder(folder)
        loadItems(currentFolderId)
    }

    fun updateFolder(folder: Folder) = viewModelScope.launch {
        repository.updateFolder(folder)
        loadItems(currentFolderId)
    }
}
