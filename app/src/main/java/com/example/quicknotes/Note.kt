package com.example.quicknotes

data class Note(
    val id: Long,
    val title: String,
    val Content: String,
    val createdAt: Long,
    val updatedAt: Long
)
