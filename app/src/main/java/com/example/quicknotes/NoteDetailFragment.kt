package com.example.quicknotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import jp.wasabeef.richeditor.RichEditor

class NoteDetailFragment : Fragment() {
    private lateinit var viewModel: NoteViewModel
    private lateinit var titleEditText: EditText
    private lateinit var contentRichEditor: RichEditor
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_detail, container, false)
        titleEditText = view.findViewById(R.id.titleEditText)
        contentRichEditor = view.findViewById(R.id.contentRichEditor)
        saveButton = view.findViewById(R.id.saveButton)
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        val noteId = arguments?.getInt("noteId")
        if (noteId != null) {
            viewModel.getNoteById(noteId).observe(viewLifecycleOwner) { note ->
                titleEditText.setText(note.title)
                contentRichEditor.html = note.content
            }
        }

        saveButton.setOnClickListener {
            saveNote()
        }

//        // Auto-save on text change
//        titleEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                saveNote()
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })
//
//        contentEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                saveNote()
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })

        return view
    }

    private fun saveNote() {
        val noteId = arguments?.getInt("noteId")
        val title = titleEditText.text.toString()
        val content = contentRichEditor.html
        val updatedAt = System.currentTimeMillis()

        if (noteId != null) {
            viewModel.getNoteById(noteId).observe(viewLifecycleOwner) { note ->
                val updatedNote = note.copy(
                    title = title,
                    content = content,
                    updatedAt = updatedAt
                )
                viewModel.updateNote(updatedNote)
            }
        } else {
            val newNote = Note(
                title = title,
                content = content,
                createdAt = updatedAt,
                updatedAt = updatedAt
            )
            viewModel.insertNote(newNote)
        }
    }
}
