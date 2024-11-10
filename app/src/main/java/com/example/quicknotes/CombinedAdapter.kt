package com.example.quicknotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

sealed class Item {
    data class FolderItem(val folder: Folder) : Item()
    data class NoteItem(val note: Note) : Item()
}

class CombinedAdapter(
    private val onFolderClick: (Folder) -> Unit,
    private val onNoteClick: (Note) -> Unit
) : ListAdapter<Item, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        val itemIcon: ImageView = itemView.findViewById(R.id.itemIconImageView)
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return when {
                oldItem is Item.FolderItem && newItem is Item.FolderItem -> oldItem.folder.id == newItem.folder.id
                oldItem is Item.NoteItem && newItem is Item.NoteItem -> oldItem.note.id == newItem.note.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.FolderItem -> 0
            is Item.NoteItem -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Item.FolderItem -> {
                val folderHolder = holder as ItemViewHolder
                folderHolder.itemNameTextView.text = item.folder.name
                folderHolder.itemIcon.setImageResource(R.drawable.ic_folder)
                folderHolder.itemView.setOnClickListener { onFolderClick(item.folder) }
            }
            is Item.NoteItem -> {
                val noteHolder = holder as ItemViewHolder
                noteHolder.itemNameTextView.text = item.note.title
                noteHolder.itemIcon.setImageResource(R.drawable.ic_note)
                noteHolder.itemView.setOnClickListener { onNoteClick(item.note) }
            }
        }
    }
}
