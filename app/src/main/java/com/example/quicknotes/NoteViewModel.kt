package com.example.quicknotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        val foldersDao = NoteDatabase.getDatabase(application).foldersDao()
        repository = NoteRepository(noteDao, foldersDao)
    }

    fun getAllNotesByFolder(folderId: Int?): LiveData<List<Note>> {
        return repository.getNotesByFoldersLiveData(folderId ?: 0)
    }

    fun getRootNotes(): LiveData<List<Note>> {
        return repository.getRootNotesLiveData()
    }

    fun insertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun getNoteById(id: Int): LiveData<Note> {
        return repository.getNoteById(id)
    }

    fun getFolderById(id: Int): LiveData<Folder> {
        return repository.getFolderById(id)
    }

    fun getRootFolders(): LiveData<List<Folder>> {
        return repository.getRootFoldersLiveData()
    }

    fun getChildFolders(parentId: Int?): LiveData<List<Folder>> {
        return repository.getChildFoldersLiveData(parentId)
    }

    fun insertFolder(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFolder(folder)
        }
    }

    fun updateFolder(folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFolder(folder)
        }
    }

    fun deleteFolder(folderId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFolder(folderId)
        }
    }
}
