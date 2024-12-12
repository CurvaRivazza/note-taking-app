package com.example.quicknotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serial
import java.io.Serializable

@Entity(tableName = "notes", foreignKeys = [ForeignKey(entity = Folder::class, parentColumns = ["id"], childColumns = ["folderId"], onDelete = ForeignKey.CASCADE)])
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String,
    var content: String,
    var folderId: Int? = null,
    var createdAt: Long,
    var updatedAt: Long
) : Serializable
