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
        return MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.create_item)).setView(input)
            .setPositiveButton(getString(R.string.create)) { _, _ ->
                val itemName = input.text.toString()
                if (itemName.isNotBlank()) {
                    MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.choose_item_type))
                        .setItems(
                            arrayOf(getString(R.string.folder), getString(R.string.note))
                        ) { _, which ->
                            when (which) {
                                0 -> listener.onCreateFolder(itemName, parentFolderId)
                                1 -> listener.onCreateNote(itemName, parentFolderId)
                            }
                        }.show()
                }
            }.setNegativeButton(getString(R.string.cancel), null).create()
    }
}
