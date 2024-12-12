package com.example.quicknotes

import org.junit.Assert.*
import org.junit.Test
import java.io.Serializable

class NoteTest {

    @Test
    fun testNoteCreation() {
        val note = Note(title = "Test Note", content = "Test Content", createdAt = 1L, updatedAt = 1L)
        assertEquals("Test Note", note.title)
        assertEquals("Test Content", note.content)
        assertEquals(1L, note.createdAt)
        assertEquals(1L, note.updatedAt)
    }

    @Test
    fun testNoteUpdateTitle() {
        val note = Note(title = "Test note", content = "Test content", createdAt = 1L, updatedAt = 1L)
        note.title = "New note"
        assertEquals("New note", note.title)
    }

    @Test
    fun testNoteUpdateContent() {
        val note = Note(title = "Test Note", content = "Content", createdAt = 1L, updatedAt = 1L)
        note.content = "New content"
        assertEquals("New content", note.content)
    }

    @Test
    fun testNoteUpdateCreatedAt() {
        val note = Note(title = "Test Note", content = "Test Content", createdAt = 1L, updatedAt = 1L)
        note.createdAt = 2L
        assertEquals(2L, note.createdAt)
    }

    @Test
    fun testNoteUpdateUpdatedAt() {
        val note = Note(title = "Test Note", content = "Test Content", createdAt = 1L, updatedAt = 1L)
        note.updatedAt = 2L
        assertEquals(2L, note.updatedAt)
    }

    @Test
    fun testNoteEquality() {
        val note1 = Note(title = "Test Note", content = "Test Content", createdAt = 1L, updatedAt = 1L)
        val note2 = Note(title = "Test Note", content = "Test Content", createdAt = 1L, updatedAt = 1L)
        assertEquals(note1, note2)
    }

    @Test
    fun testNoteInequality() {
        val note1 = Note(title = "Test Note", content = "Test Content", createdAt = 1L, updatedAt = 1L)
        val note2 = Note(title = "Another Note", content = "Another Content", createdAt = 2L, updatedAt = 2L)
        assertNotEquals(note1, note2)
    }

    @Test
    fun testNoteSerialization() {
        val note = Note(title = "Test Note", content = "Test Content", createdAt = 1L, updatedAt = 1L)
        assertTrue(note is Serializable)
    }
}
