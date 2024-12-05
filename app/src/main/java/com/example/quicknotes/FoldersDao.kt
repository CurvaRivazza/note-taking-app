package com.example.quicknotes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FoldersDao {
    @Query("SELECT * FROM folders ORDER BY name ASC")
    suspend fun getAllFolders(): List<Folder>

    @Query("SELECT * FROM folders WHERE parentId IS :parentId ORDER BY name ASC")
    suspend fun getFoldersByParentId(parentId: Int?): List<Folder>

    @Query("SELECT * FROM folders WHERE id IS :folderId")
    fun getFolderById(folderId: Int): LiveData<Folder>

    @Insert
    suspend fun insertFolder(folder: Folder)

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)
}