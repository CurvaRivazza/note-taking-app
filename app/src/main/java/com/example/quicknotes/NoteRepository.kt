package com.example.quicknotes

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao, private val folderDao: FoldersDao) {
    fun getNotesByFoldersLiveData(folderId: Int?): LiveData<List<Note>> {
        return noteDao.getAllNotesByFolderLiveData(folderId)
    }

    fun getRootNotesLiveData(): LiveData<List<Note>> {
        return noteDao.getRootNotesLiveData()
    }

    fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    fun getNoteById(id: Int): LiveData<Note> {
        return noteDao.getNoteById(id)
    }

    fun getFolderById(id: Int): LiveData<Folder> {
        return folderDao.getFolderById(id)
    }

    fun getRootFoldersLiveData(): LiveData<List<Folder>> {
        return folderDao.getRootFoldersLiveData()
    }

    fun getChildFoldersLiveData(parentId: Int?): LiveData<List<Folder>> {
        return folderDao.getChildFoldersLiveData(parentId)
    }

    fun insertFolder(folder: Folder) {
        folderDao.insertFolder(folder)
    }

    fun updateFolder(folder: Folder) {
        folderDao.updateFolder(folder)
    }

    fun deleteFolder(id: Int) {
        folderDao.deleteFolder(id)
    }
}

