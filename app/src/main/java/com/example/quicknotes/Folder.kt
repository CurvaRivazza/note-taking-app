package com.example.quicknotes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    val parentId: Int? = null
) : Serializable
