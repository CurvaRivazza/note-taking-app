package com.example.quicknotes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Insert
    fun insertNote(note: Note): Long

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Query("SELECT * FROM notes WHERE folderId is :folderId ORDER BY updatedAt DESC")
    fun getAllNotesByFolderLiveData(folderId: Int?): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE folderId IS NULL")
    fun getRootNotesLiveData(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE :query OR content LIKE :query")
    fun searchNotes(query: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): LiveData<Note>
}
