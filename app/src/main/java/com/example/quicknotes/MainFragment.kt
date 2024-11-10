package com.example.quicknotes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Stack
class MainFragment : Fragment(), CreateItemDialogFragment.CreateItemDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var combinedAdapter: CombinedAdapter
    private lateinit var viewModel: NoteViewModel
    private var currentFolderId: Int? = null
    private val folderStack = Stack<Int>()
    private lateinit var currentPathTextView: TextView
    private lateinit var backButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        recyclerView = view.findViewById(R.id.recyclerView)
        currentPathTextView = view.findViewById(R.id.currentPathTextView)
        backButton = view.findViewById(R.id.backButton)

        combinedAdapter = CombinedAdapter(
            onFolderClick = { folder ->
                folderStack.push(currentFolderId ?: 0)
                currentFolderId = folder.id
                updateList()
                updatePath()
            },
            onNoteClick = { note ->
                val fragment = NoteDetailFragment().apply {
                    arguments = Bundle().apply {
                        putInt("noteId", note.id.toInt())
                    }
                }
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = combinedAdapter

        getRootItems()

        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            val dialog = CreateItemDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt("parentFolderId", currentFolderId ?: 0)
                }
                setListener(this@MainFragment)
            }
            dialog.show(parentFragmentManager, "CreateItemDialogFragment")
        }

        backButton.setOnClickListener {
            if (folderStack.isNotEmpty()) {
                currentFolderId = folderStack.pop()
                updateList()
                updatePath()
            }
        }

        return view
    }

    private fun updatePath() {
        buildPath(currentFolderId) { path ->
            currentPathTextView.text = path
        }
    }

    private fun buildPath(folderId: Int?, callback: (String) -> Unit) {
        if (folderId == null) {
            callback("")
            return
        }

        viewModel.getFolderById(folderId).observe(viewLifecycleOwner) { folder ->
            if (folder == null) {
                callback("")
                return@observe
            }
            if (folder.parentId != null) {
                buildPath(folder.parentId) { parentPath ->
                    callback(if (parentPath.isEmpty()) folder.name else "$parentPath > ${folder.name}")
                }
            } else {
                callback(folder.name)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (folderStack.isNotEmpty()) {
                        currentFolderId = folderStack.pop()
                        updateList()
                        updatePath()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun onCreateFolder(name: String, parentFolderId: Int?) {
        val folder = Folder(name = name, parentId = parentFolderId)
        viewModel.insertFolder(folder)
        //updateList()
    }

    override fun onCreateNote(title: String, folderId: Int?) {
        val note = Note(
            id = 0,
            title = title,
            content = "",
            folderId = folderId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        viewModel.insertNote(note)
        updateList()
    }

    private fun updateList() {
        viewModel.getChildFolders(currentFolderId).observe(viewLifecycleOwner) { folders ->
            viewModel.getAllNotesByFolder(currentFolderId).observe(viewLifecycleOwner) { notes ->
                val items = folders.map {
                    Item.FolderItem(it)
                } + notes.map {
                    Item.NoteItem(it)
                }
                combinedAdapter.submitList(items)
            }
        }
    }

    private fun getRootItems() {
        viewModel.getRootFolders().observe(viewLifecycleOwner) { folders ->
            viewModel.getRootNotes().observe(viewLifecycleOwner) { notes ->
                val items = folders.map { Item.FolderItem(it) } + notes.map { Item.NoteItem(it) }
                combinedAdapter.submitList(items)
            }
        }
    }
}
