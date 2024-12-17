package com.example.quicknotes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE folderId IS :folderId ORDER BY title ASC")
    suspend fun getNotesByFolderId(folderId: Int?): List<Note>

    @Query("SELECT * FROM notes WHERE id IS :noteId")
    fun getNoteById(noteId: Int):LiveData<Note>

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}
