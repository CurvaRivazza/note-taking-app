package com.example.quicknotes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FoldersDao {
    @Query("SELECT * FROM folders WHERE parentId IS NULL")
    fun getRootFoldersLiveData(): LiveData<List<Folder>>

    @Query("SELECT * FROM folders WHERE parentId = :parentId")
    fun getChildFoldersLiveData(parentId: Int?): LiveData<List<Folder>>

    @Insert
    fun insertFolder(folder: Folder)

    @Update
    fun updateFolder(folder: Folder)

    @Query("DELETE FROM folders WHERE id = :id")
    fun deleteFolder(id: Int)

    @Query("SELECT * FROM folders WHERE id = :id")
    fun getFolderById(id: Int): LiveData<Folder>
}