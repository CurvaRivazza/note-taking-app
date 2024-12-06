package com.example.quicknotes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import jp.wasabeef.richeditor.RichEditor
import java.io.File
import java.io.FileOutputStream

class NoteDetailFragment : Fragment() {
    private var currentNote: Note? = null
    private var isBackPressedCallbackAdded = false
    private val REQUEST_PERMISSIONS = 1001
    private val REQUEST_IMAGE_PICK = 1
    private var isViewMode: Boolean = false
    lateinit var viewModel: NoteViewModel
    private lateinit var titleEditText: EditText
    private lateinit var contentRichEditor: RichEditor
    private lateinit var saveButton: ImageButton
    private lateinit var hintTextView: TextView
    private lateinit var formatToolbar: HorizontalScrollView
    private lateinit var actionBar: ConstraintLayout
    private lateinit var uploadImageButton: ImageButton
    private lateinit var alignLeftFormatImageButton: ImageButton
    private lateinit var alignCenterFormatImageButton: ImageButton
    private lateinit var alignRightFormatImageButton: ImageButton
    private lateinit var boldFormatImageButton: ImageButton
    private lateinit var italicFormatImageButton: ImageButton
    private lateinit var strikethroughFormatImageButton: ImageButton
    private lateinit var underlinedFormatImageButton: ImageButton
    private lateinit var colorFillFormatImageButton: ImageButton
    private lateinit var colorTextFormatImageButton: ImageButton
    private lateinit var indentIncreaseImageButton: ImageButton
    private lateinit var indentDecreaseImageButton: ImageButton
    private lateinit var listNumberedImageButton: ImageButton
    private lateinit var listBulletedImageButton: ImageButton
    private lateinit var clearFormatImageButton: ImageButton
    private lateinit var moreOptionsButton: ImageButton
    private lateinit var undoImageButton: ImageButton
    private lateinit var redoImageButton: ImageButton

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_detail, container, false)
        titleEditText = view.findViewById(R.id.titleEditText)
        contentRichEditor = view.findViewById(R.id.contentRichEditor)
        saveButton = view.findViewById(R.id.saveButton)
        formatToolbar = view.findViewById(R.id.formatToolbar)
        hintTextView = view.findViewById(R.id.hintTextView)
        actionBar = view.findViewById(R.id.actionBar)
        uploadImageButton = view.findViewById(R.id.uploadImageButton)
        alignLeftFormatImageButton = view.findViewById(R.id.alignLeftFormatImageButton)
        alignCenterFormatImageButton = view.findViewById(R.id.alignCenterFormatImageButton)
        alignRightFormatImageButton = view.findViewById(R.id.alignRightFormatImageButton)
        boldFormatImageButton = view.findViewById(R.id.boldFormatImageButton)
        italicFormatImageButton = view.findViewById(R.id.italicFormatImageButton)
        strikethroughFormatImageButton = view.findViewById(R.id.strikethroughFormatImageButton)
        underlinedFormatImageButton = view.findViewById(R.id.underlinedFormatImageButton)
        colorFillFormatImageButton = view.findViewById(R.id.colorFillFormatImageButton)
        colorTextFormatImageButton = view.findViewById(R.id.colorTextFormatImageButton)
        indentIncreaseImageButton = view.findViewById(R.id.indentIncreaseImageButton)
        indentDecreaseImageButton = view.findViewById(R.id.indentDecreaseImageButton)
        listNumberedImageButton = view.findViewById(R.id.listNumberedImageButton)
        listBulletedImageButton = view.findViewById(R.id.listBulletedImageButton)
        clearFormatImageButton = view.findViewById(R.id.clearFormatImageButton)
        moreOptionsButton = view.findViewById(R.id.moreOptionsButton)
        undoImageButton = view.findViewById(R.id.undoButton)
        redoImageButton = view.findViewById(R.id.redoButton)
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        contentRichEditor.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.background
            )
        )
        contentRichEditor.setEditorFontSize(18)
        contentRichEditor.setEditorFontColor(resources.getColor(R.color.text))
        contentRichEditor.setPadding(8, 0, 8, 0)

        uploadImageButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestMediaPermission()
            } else {
                openImagePicker()
            }
        }
        alignLeftFormatImageButton.setOnClickListener { contentRichEditor.setAlignLeft() }
        alignCenterFormatImageButton.setOnClickListener { contentRichEditor.setAlignCenter() }
        alignRightFormatImageButton.setOnClickListener { contentRichEditor.setAlignRight() }
        boldFormatImageButton.setOnClickListener { applyFormat(RichEditor.Type.BOLD) }
        italicFormatImageButton.setOnClickListener { applyFormat(RichEditor.Type.ITALIC) }
        strikethroughFormatImageButton.setOnClickListener { applyFormat(RichEditor.Type.STRIKETHROUGH) }
        underlinedFormatImageButton.setOnClickListener { applyFormat(RichEditor.Type.UNDERLINE) }
        indentIncreaseImageButton.setOnClickListener { contentRichEditor.setIndent() }
        indentDecreaseImageButton.setOnClickListener { contentRichEditor.setOutdent() }
        listNumberedImageButton.setOnClickListener { contentRichEditor.setNumbers() }
        listBulletedImageButton.setOnClickListener { contentRichEditor.setBullets() }
        colorTextFormatImageButton.setOnClickListener { showColorPickerDialog(true) }
        colorFillFormatImageButton.setOnClickListener { showColorPickerDialog(false) }
        clearFormatImageButton.setOnClickListener { contentRichEditor.removeFormat() }
        moreOptionsButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
        undoImageButton.setOnClickListener { contentRichEditor.undo() }
        redoImageButton.setOnClickListener { contentRichEditor.redo() }

        val noteId = arguments?.getInt("noteId")
        if (noteId != null) {
            viewModel.getNoteById(noteId).observe(viewLifecycleOwner) { note ->
                currentNote = note
                titleEditText.setText(note.title)
                contentRichEditor.html = note.content
                hintTextView.visibility = if (note.content.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        contentRichEditor.setOnTextChangeListener { text ->
            hintTextView.visibility =
                if (text.isEmpty() || text == "<br>") View.VISIBLE else View.GONE
        }

        saveButton.setOnClickListener {
            saveNote()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }

        viewModel.folderName.observe(viewLifecycleOwner) { folderName ->
            (activity as? MainActivity)?.updateCurrentPathTextView(folderName)
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (imeVisible) {
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                formatToolbar.visibility = View.VISIBLE
                actionBar.visibility = View.GONE
                formatToolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = imeHeight
                }
            } else {
                formatToolbar.visibility = View.GONE
                actionBar.visibility = View.VISIBLE
            }
            insets
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isBackPressedCallbackAdded) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                handleBackPress()
            }
            isBackPressedCallbackAdded = true
        }
    }

    private fun handleBackPress() {
        val title = titleEditText.text.toString()
        val content = contentRichEditor.html

        currentNote?.let { note ->
            if (note.title != title || note.content != content) {
                showUnsavedChangesDialog()
            } else {
                parentFragmentManager.popBackStack()
            }
        } ?: run {
            parentFragmentManager.popBackStack()
        }
    }

    private fun requestMediaPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permission),
            REQUEST_PERMISSIONS
        )
    }

    private fun handleOldAndroidUri(uri: Uri): String {
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val imageFile = File(requireContext().getExternalFilesDir(null), fileName)

        inputStream.use { input ->
            val outputStream = FileOutputStream(imageFile)
            input?.copyTo(outputStream)
            outputStream.close()
        }

        return imageFile.absolutePath
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val localImagePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    copyImageToAppStorage(uri)
                } else {
                    handleOldAndroidUri(uri)
                }
                val html =
                    "<img src=\"$localImagePath\" style=\"width: 100%; height: auto;\"/> <br><br>"
                contentRichEditor.evaluateJavascript(
                    "javascript:document.execCommand('insertHTML', false, '$html');",
                    null
                )
            }
        }
    }

    private fun copyImageToAppStorage(imageUri: Uri): String {
        val inputStream = context?.contentResolver?.openInputStream(imageUri)
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val imageFile = File(requireContext().getExternalFilesDir(null), fileName)

        inputStream.use { input ->
            val outputStream = FileOutputStream(imageFile)
            input?.copyTo(outputStream)
            outputStream.close()
        }

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )

        return uri.toString()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.context_menu, popupMenu.menu)
        popupMenu.setForceShowIcon(true)
        val viewModeItem = popupMenu.menu.findItem(R.id.viewModeButton)
        if (isViewMode) {
            viewModeItem.setIcon(R.drawable.ic_edit)
        } else {
            viewModeItem.setIcon(R.drawable.ic_read)
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.viewModeButton -> toggleViewMode(popupMenu)
                R.id.findButton -> showFindDialog()
                R.id.replaceButton -> showReplaceDialog()
                R.id.shareButton -> showShareDialog()
                R.id.moveFileButton -> showMoveNoteDialog()
                R.id.deleteButton -> deleteNote()
                else -> false
            }
            true
        }
        popupMenu.show()
    }

    private fun deleteNote() {
        currentNote?.let { note ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить заметку")
                .setMessage("Вы уверены, что хотите удалить эту заметку?")
                .setPositiveButton("Да") { _, _ ->
                    viewModel.deleteNote(note)
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                .show()
        } ?: run {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Ошибка")
                .setMessage("Не удалось найти заметку для удаления.")
                .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun toggleViewMode(popupMenu: PopupMenu) {
        isViewMode = !isViewMode
        if (isViewMode) {
            titleEditText.isEnabled = false
            contentRichEditor.isEnabled = false
            popupMenu.menu.findItem(R.id.viewModeButton).setIcon(R.drawable.ic_edit)
        } else {
            titleEditText.isEnabled = true
            contentRichEditor.isEnabled = true
            popupMenu.menu.findItem(R.id.viewModeButton).setIcon(R.drawable.ic_read)
        }
    }

    private fun showFindDialog() {
        val findDialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.find))
        val input = EditText(requireContext())
        findDialog.setView(input)
        findDialog.setPositiveButton(getString(R.string.search1)) { dialog, _ ->
            val searchTerm = input.text.toString()
            findInEditor(searchTerm)
            dialog.dismiss()
        }

        findDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        findDialog.show()
    }

    private fun findInEditor(searchItem: String) {
        if (searchItem.isEmpty()) return
        val highlightStart = "<span style='background-color: yellow;'>"
        val highlightEnd = "</span>"
        val currentContent = contentRichEditor.html
        val escapedSearchItem = escapeHtml(searchItem)
        val highlightedContent = currentContent.replace(
            escapedSearchItem,
            "$highlightStart$escapedSearchItem$highlightEnd",
            ignoreCase = true
        )
        contentRichEditor.html = highlightedContent
    }

    private fun escapeHtml(input: String): String {
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&apos;")
    }

    private fun showReplaceDialog() {
        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }
        val inputOld = EditText(requireContext())
        inputOld.hint = getString(R.string.what_to_replace)
        val inputNew = EditText(requireContext())
        inputNew.hint = getString(R.string.what_to_replace_it_with)
        linearLayout.addView(inputOld)
        linearLayout.addView(inputNew)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.replacing_the_text))
            .setView(linearLayout)
            .setPositiveButton(getString(R.string.replace)) { dialog, _ ->
                val oldText = inputOld.text.toString().trim()
                val newText = inputNew.text.toString().trim()
                replaceInEditor(oldText, newText)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun replaceInEditor(oldText: String, newText: String) {
        if (oldText.isEmpty()) return

        val currentContent = contentRichEditor.html
        val escapedOldText = escapeHtml(oldText)
        val updatedContent = currentContent.replace(escapedOldText, newText, ignoreCase = true)
        contentRichEditor.html = updatedContent
    }

    private fun showUnsavedChangesDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Несохраненные изменения")
            .setMessage("У вас есть несохраненные изменения. Вы хотите их сохранить?")
            .setPositiveButton("Сохранить") { _, _ ->
                saveNote()
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("Отменить") { dialog, _ ->
                dialog.dismiss()
                parentFragmentManager.popBackStack()
            }
            .setNeutralButton("Отмена", null)
            .show()
    }

    private fun saveNote() {
        val title = titleEditText.text.toString()
        val content = contentRichEditor.html
        val updatedAt = System.currentTimeMillis()
        val updatedNote = Note(
            currentNote!!.id,
            title = title,
            content = content,
            currentNote!!.folderId,
            currentNote!!.createdAt,
            updatedAt = updatedAt
        )
        viewModel.updateNote(updatedNote)
    }

    private fun applyFormat(format: RichEditor.Type) = when (format) {
        RichEditor.Type.BOLD -> {
            contentRichEditor.setBold()
        }

        RichEditor.Type.ITALIC -> {
            contentRichEditor.setItalic()
        }

        RichEditor.Type.STRIKETHROUGH -> {
            contentRichEditor.setStrikeThrough()
        }

        RichEditor.Type.UNDERLINE -> {
            contentRichEditor.setUnderline()
        }

        else -> {}
    }

    private fun showColorPickerDialog(isTextColor: Boolean) {
        val defaultColor = if (isTextColor) resources.getColor(R.color.text) else Color.TRANSPARENT
        ColorPickerDialog.Builder(requireContext()).setTitle(getString(R.string.pick_a_color))
            .setPreferenceName("ColorPickerDialog")
            .setPositiveButton(getString(R.string.ok), ColorEnvelopeListener { envelope, fromUser ->
                if (isTextColor) {
                    contentRichEditor.setTextColor(envelope.color)
                } else {
                    contentRichEditor.setTextBackgroundColor(envelope.color)
                }
            }).setNegativeButton(getString(R.string.cancel)) { dialog, i -> dialog.dismiss() }.attachAlphaSlideBar(true)
            .attachAlphaSlideBar(true).attachBrightnessSlideBar(true).setBottomSpace(12).show()
    }

    private fun showShareDialog() {
        val options = arrayOf("TXT", "HTML")
        val dialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.select_the_format_to_export))
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> exportAsTXT()
                        1 -> exportAsHTML()
                    }
                    dialog.dismiss()
                }
        dialog.show()
    }

    private fun exportAsTXT() {
        val content =
            titleEditText.text.toString() + "\n\n" + contentRichEditor.html.replace("<br>", "\n")
                .replace(
                    Regex("<[^>]*>"),
                    ""
                ).replace("&nbsp;", " ").trim()
        val fileName = "${titleEditText.text}.txt"
        saveToFile(fileName, content, "text/plain")
    }

    private fun exportAsHTML() {
        val content = "<h1>${titleEditText.text}</h1>\n${contentRichEditor.html}"
        val fileName = "${titleEditText.text}.html"
        saveToFile(fileName, content, "text/html")
    }

    private fun saveToFile(fileName: String, content: String, mimeType: String) {
        val file = File(requireContext().getExternalFilesDir(null), fileName)
        file.writeText(content, charset("windows-1251"))
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.quicknotes.fileprovider",
            file
        )
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = mimeType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_a_note)))
    }

    private fun showMoveNoteDialog() {
        viewModel.allFolders.observe(viewLifecycleOwner) { folders ->
            val folderTree = buildFolderHierarchy(folders)

            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_folder, null)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(context)

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.select_folder))
                .setView(dialogView)
                .setNegativeButton(getString(R.string.cancel), null).create()

            val adapter = FolderAdapter(folderTree) { selectedFolder ->
                moveNoteToFolder(selectedFolder.id)
                dialog.dismiss()
            }

            recyclerView.adapter = adapter
            dialog.show()
        }
    }

    private fun moveNoteToFolder(folderId: Int) {
        currentNote?.let {
            val updatedNote = it.copy(folderId = folderId)
            viewModel.updateNote(updatedNote)
            viewModel.getFolderById(folderId, requireContext())
            Toast.makeText(requireContext(), "Заметка перемещена в папку", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun buildFolderHierarchy(folders: List<Folder>): List<FolderItem> {
        val folderMap = folders.associateBy { it.id }
        val rootFolders = folders.filter { it.parentId == null }
        val folderItems = mutableListOf<FolderItem>()

        rootFolders.forEach { rootFolder ->
            folderItems.add(FolderItem(rootFolder, 0))
            addChildFolders(folderItems, rootFolder.id, folderMap, 1)
        }

        return folderItems
    }

    private fun addChildFolders(
        folderItems: MutableList<FolderItem>,
        parentId: Int?,
        folderMap: Map<Int, Folder>,
        level: Int
    ) {
        folderMap.values.filter { it.parentId == parentId }.forEach { folder ->
            folderItems.add(FolderItem(folder, level))
            addChildFolders(folderItems, folder.id, folderMap, level + 1)
        }
    }

}