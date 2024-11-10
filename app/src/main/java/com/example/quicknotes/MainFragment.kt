package com.example.quicknotes

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {

    lateinit var noteViewModel: NoteViewModel
    private lateinit var combinedAdapter: CombinedAdapter
    private lateinit var currentPathTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    var folderStack = mutableListOf<Int?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        currentPathTextView = view.findViewById(R.id.currentPathTextView)
        backButton = view.findViewById(R.id.backButton)
        recyclerView = view.findViewById(R.id.recyclerView)
        fab = view.findViewById(R.id.fab)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        combinedAdapter = CombinedAdapter { item ->
            when (item) {
                is Folder -> {
                    folderStack.add(item.id)
                    noteViewModel.loadItems(item.id)
                    updateCurrentPath()
                }
                is Note -> {
                    val fragment = NoteDetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt("noteId", item.id.toInt())
                        }
                    }
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        recyclerView.adapter = combinedAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        noteViewModel.allItems.observe(viewLifecycleOwner) { items ->
            combinedAdapter.setItems(items)
        }

        backButton.setOnClickListener {
            if (folderStack.isNotEmpty()) {
                folderStack.removeAt(folderStack.size - 1)
                val lastFolderId = folderStack.lastOrNull()
                noteViewModel.loadItems(lastFolderId)
                updateCurrentPath()
            }
        }

        fab.setOnClickListener {
            showCreateItemDialog()
        }

        return view
    }

    fun updateCurrentPath() {
        currentPathTextView.text = folderStack.joinToString(separator = " > ") { folderId ->
            if (folderId == null) "Root" else "Folder $folderId"
        }
    }

    private fun showCreateItemDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_item, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        val itemTypeRadioGroup = dialogView.findViewById<RadioGroup>(R.id.itemTypeRadioGroup)

        AlertDialog.Builder(context)
            .setTitle("Create New Item")
            .setView(dialogView)
            .setPositiveButton("Create") { dialog, which ->
                val title = titleEditText.text.toString()
                if (title.isNotEmpty()) {
                    val currentFolderId = folderStack.lastOrNull()
                    val newItem = when (itemTypeRadioGroup.checkedRadioButtonId) {
                        R.id.noteRadioButton -> {
                            Note(title = title, content = "", folderId = currentFolderId, createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
                        }
                        R.id.folderRadioButton -> {
                            Folder(name = title, parentId = currentFolderId)
                        }
                        else -> null
                    }
                    newItem?.let {
                        when (it) {
                            is Note -> noteViewModel.insertNote(it)
                            is Folder -> noteViewModel.insertFolder(it)
                            else -> {}
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
