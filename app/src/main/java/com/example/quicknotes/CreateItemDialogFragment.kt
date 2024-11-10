package com.example.quicknotes

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateItemDialogFragment : DialogFragment() {

    interface CreateItemDialogListener {
        fun onCreateFolder(name: String, parentFolderId: Int?)
        fun onCreateNote(title: String, folderId: Int?)
    }

    private lateinit var listener: CreateItemDialogListener

    fun setListener(listener: CreateItemDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val parentFolderId =
            if (arguments?.getInt("parentFolderId") == 0) null else arguments?.getInt("parentFolderId")
        val input = EditText(context)
        return MaterialAlertDialogBuilder(requireContext()).setTitle("Create Item").setView(input)
            .setPositiveButton("Create") { _, _ ->
                val itemName = input.text.toString()
                if (itemName.isNotBlank()) {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Choose Item Type")
                        .setItems(
                            arrayOf("Folder", "Note")
                        ) { _, which ->
                            when (which) {
                                0 -> listener.onCreateFolder(itemName, parentFolderId)
                                1 -> listener.onCreateNote(itemName, parentFolderId)
                            }
                        }.show()
                }
            }.setNegativeButton("Cancel", null).create()
    }
}
