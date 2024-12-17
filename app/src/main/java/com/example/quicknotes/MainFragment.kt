package com.example.quicknotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var combinedAdapter: CombinedAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var emptyView: TextView
    private lateinit var searchView: SearchView
    private var folderStack = mutableListOf<Int?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        (activity as MainActivity).setLocale(requireContext(), (activity as MainActivity).getLocalePreferences())
        recyclerView = view.findViewById(R.id.recyclerView)
        fab = view.findViewById(R.id.fab)
        emptyView = view.findViewById(R.id.emptyView)
        searchView = view.findViewById(R.id.searchView)
        (activity as MainActivity).updateCurrentPathTextView("")
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        combinedAdapter = CombinedAdapter(onItemClick = { item ->
            when (item) {
                is Folder -> {
                    if (searchView.query.isNotEmpty()) {
                        searchView.setQuery("", false)
                    }

                    folderStack.add(item.id)
                    noteViewModel.loadItems(item.id)
                    updateBackButtonVisibility()
                    (activity as? MainActivity)?.updateCurrentPathTextView(item.name)
                }

                is Note -> {
                    val activity = activity as? MainActivity
                    activity?.let {
                        val fragment = NoteDetailFragment().apply {
                            arguments = Bundle().apply {
                                putInt("noteId", item.id.toInt())
                                putIntegerArrayList("folderStack", ArrayList(folderStack))
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack(null)
                            .commit()
                        if (searchView.query.isNotEmpty()) {
                            searchView.setQuery("", false)
                        }
                        activity.findViewById<ImageButton>(R.id.backButton).visibility =
                            View.VISIBLE
                        noteViewModel.getFolderById(item.folderId)
                    }
                }
            }
        }, onDeleteButtonClick = { item ->
            showDeleteConfirmationDialog(item)
        }, onEditButtonClick = { item ->
            when (item) {
                is Folder -> {
                    showRenameFolderDialog(item)
                }
            }
        })

        recyclerView.adapter = combinedAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        noteViewModel.allItems.observe(viewLifecycleOwner) { items ->
            combinedAdapter.setItems(items)
            emptyView.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        noteViewModel.folderName.observe(viewLifecycleOwner) { folderName ->
            (activity as? MainActivity)?.updateCurrentPathTextView(folderName)
        }

        fab.setOnClickListener {
            showCreateItemTitleDialog()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                combinedAdapter.filter(newText ?: "")
                return true
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }

        return view
    }

    private fun handleBackPress() {
        if (folderStack.isNotEmpty()) {
            folderStack.removeAt(folderStack.size - 1)
            val lastFolderId = folderStack.lastOrNull()
            noteViewModel.loadItems(lastFolderId)
            noteViewModel.getFolderById(lastFolderId)
            updateBackButtonVisibility()
        }
    }

    fun updateBackButtonVisibility() {
        requireActivity().findViewById<ImageButton>(R.id.backButton).visibility =
            if (folderStack.isEmpty() || folderStack.lastOrNull() == null) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    private fun showCreateItemTitleDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_item, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)

        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.enter_title_dialog))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.next)) { dialog, which ->
                val title = titleEditText.text.toString().trim()
                if (title.isNotEmpty()) {
                    showCreateItemTypeDialog(title)
                }
            }.setNegativeButton(getString(R.string.cancel), null).show()
    }

    private fun showCreateItemTypeDialog(title: String) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_create_item_type, null)
        val noteCard = dialogView.findViewById<MaterialCardView>(R.id.noteCard)
        val folderCard = dialogView.findViewById<MaterialCardView>(R.id.folderCard)

        val dialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.select_item_type))
                .setView(dialogView).setNegativeButton(getString(R.string.cancel), null).create()

        noteCard.setOnClickListener {
            createItem(title, true)
            dialog.dismiss()
        }

        folderCard.setOnClickListener {
            createItem(title, false)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun createItem(title: String, isNote: Boolean) {
        val currentFolderId = folderStack?.lastOrNull()
        val newItem = if (isNote) {
            Note(
                title = title,
                content = "",
                folderId = currentFolderId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        } else {
            Folder(name = title, parentId = currentFolderId)
        }
        when (newItem) {
            is Note -> noteViewModel.insertNote(newItem)
            is Folder -> noteViewModel.insertFolder(newItem)
        }
    }

    private fun showDeleteConfirmationDialog(item: Any) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_title))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                when (item) {
                    is Note -> noteViewModel.deleteNote(item)
                    is Folder -> noteViewModel.deleteFolder(item)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showRenameFolderDialog(folder: Folder) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rename_folder, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.nameEditText)
        nameEditText.setText(folder.name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.rename_folder))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { dialog, which ->
                val newName = nameEditText.text.toString()
                if (newName.isNotEmpty()) {
                    val updatedFolder = folder.copy(name = newName)
                    noteViewModel.updateFolder(updatedFolder)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if(folderStack.isEmpty()){
            val backButton = (activity as MainActivity).findViewById<ImageButton>(R.id.backButton)
            if(backButton?.visibility == View.VISIBLE){
                backButton.visibility = View.GONE
            }
        }
        noteViewModel.loadItems(folderStack.lastOrNull())
    }
}
