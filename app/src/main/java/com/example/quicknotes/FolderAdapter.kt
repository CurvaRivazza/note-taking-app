package com.example.quicknotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(
    private val folders: List<FolderItem>,
    private val onFolderSelected: (Folder) -> Unit
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folderItem = folders[position]
        holder.bind(folderItem)
    }

    override fun getItemCount(): Int = folders.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.folderNameTextView)
        private val folderItemContainer: LinearLayout = itemView.findViewById(R.id.folderItemContainer)

        fun bind(folderItem: FolderItem) {
            nameTextView.text = folderItem.folder.name

            val paddingStart = folderItem.level * 60
            folderItemContainer.setPadding(paddingStart, 8, 8, 8)
            itemView.setOnClickListener { onFolderSelected(folderItem.folder) }
        }
    }
}