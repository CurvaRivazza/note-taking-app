package com.example.quicknotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CombinedAdapter(private val onItemClick: (Any) -> Unit) : RecyclerView.Adapter<CombinedAdapter.ViewHolder>() {

    private val items = mutableListOf<Any>()

    fun setItems(newItems: List<Any>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
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

            when (item) {
                is Note -> {
                    itemIconImageView.setImageResource(R.drawable.ic_note)
                    itemNameTextView.text = item.title
                }
                is Folder -> {
                    itemIconImageView.setImageResource(R.drawable.ic_folder)
                    itemNameTextView.text = item.name
                }
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
