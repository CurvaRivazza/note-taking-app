package com.example.quicknotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CombinedAdapter(
    private val onItemClick: (Any) -> Unit,
    private val onDeleteButtonClick: (Any) -> Unit,
    private val onEditButtonClick: (Folder) -> Unit
) : RecyclerView.Adapter<CombinedAdapter.ViewHolder>() {

    private val items = mutableListOf<Any>()

    fun setItems(newItems: List<Any>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Any) {
            val itemIconImageView = itemView.findViewById<ImageView>(R.id.itemIconImageView)
            val itemNameTextView = itemView.findViewById<TextView>(R.id.itemNameTextView)
            val editImageButton = itemView.findViewById<ImageView>(R.id.editImageButton)
            val deleteImageView = itemView.findViewById<ImageView>(R.id.deleteImageButton)

            editImageButton.visibility = View.GONE
            deleteImageView.clearColorFilter()

            when (item) {
                is Note -> {
                    itemIconImageView.setImageResource(R.drawable.ic_note)
                    itemNameTextView.text = item.title
                    deleteImageView.setColorFilter(ContextCompat.getColor(itemView.context, R.color.secondary))
                }

                is Folder -> {
                    itemIconImageView.setImageResource(R.drawable.ic_folder)
                    itemNameTextView.text = item.name
                    editImageButton.visibility = View.VISIBLE
                }
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }

            deleteImageView.setOnClickListener { onDeleteButtonClick(item) }
        }
    }
}
