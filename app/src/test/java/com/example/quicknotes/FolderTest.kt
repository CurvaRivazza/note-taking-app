package com.example.quicknotes

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.io.Serializable

class FolderTest {
    @Test
    fun testFolderCreation() {
        val folder = Folder(name = "Test Folder", parentId = null)
        assertEquals("Test Folder", folder.name)
        assertEquals(null, folder.parentId)
    }

    @Test
    fun testFolderWithParentId() {
        val folder = Folder(name = "Test Folder", parentId = 1)
        assertEquals("Test Folder", folder.name)
        assertEquals(1, folder.parentId)
    }

    @Test
    fun testFolderUpdateName() {
        val folder = Folder(name = "Test Folder", parentId = null)
        folder.name = "New test folder"
        assertEquals("New test folder", folder.name)
    }

    @Test
    fun testFolderUpdateParentId() {
        val folder = Folder(name = "Test Folder", parentId = null)
        folder.parentId = 2
        assertEquals(2, folder.parentId)
    }

    @Test
    fun testFolderEquality() {
        val folder1 = Folder(name = "Test Folder", parentId = null)
        val folder2 = Folder(name = "Test Folder", parentId = null)
        assertEquals(folder1, folder2)
    }

    @Test
    fun testFolderInequality() {
        val folder1 = Folder(name = "Test Folder", parentId = null)
        val folder2 = Folder(name = "Another Folder", parentId = null)
        assertNotEquals(folder1, folder2)
    }

    @Test
    fun testFolderSerialization() {
        val folder = Folder(name = "Test Folder", parentId = null)
        assertTrue(folder is Serializable)
    }
}