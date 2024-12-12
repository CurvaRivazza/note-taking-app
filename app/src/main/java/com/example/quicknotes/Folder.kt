package com.example.quicknotes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var parentId: Int? = null
) : Serializable
