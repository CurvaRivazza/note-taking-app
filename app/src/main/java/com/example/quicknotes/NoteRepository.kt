package com.example.quicknotes

class NoteRepository(private val noteDatabase: NoteDatabase) {

    suspend fun getAllNotes() = noteDatabase.noteDao().getAllNotes()
    suspend fun getNotesByFolderId(folderId: Int) = noteDatabase.noteDao().getNotesByFolderId(folderId)
    fun getNoteById(noteId: Int) = noteDatabase.noteDao().getNoteById(noteId)
    suspend fun insertNote(note: Note) = noteDatabase.noteDao().insertNote(note)
    suspend fun updateNote(note: Note) = noteDatabase.noteDao().updateNote(note)
    suspend fun deleteNote(note: Note) = noteDatabase.noteDao().deleteNote(note)

    suspend fun getAllFolders() = noteDatabase.foldersDao().getAllFolders()
    suspend fun getFoldersByParentId(parentId: Int) = noteDatabase.foldersDao().getFoldersByParentId(parentId)
    suspend fun insertFolder(folder: Folder) = noteDatabase.foldersDao().insertFolder(folder)
    suspend fun updateFolder(folder: Folder) = noteDatabase.foldersDao().updateFolder(folder)
    suspend fun deleteFolder(folder: Folder) = noteDatabase.foldersDao().deleteFolder(folder)
}
