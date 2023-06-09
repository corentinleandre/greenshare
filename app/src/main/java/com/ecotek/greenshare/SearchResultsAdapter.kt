package com.ecotek.greenshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchResultsAdapter(
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {


    private val searchResults: MutableList<Post> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = searchResults[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    fun setSearchResults(results: List<Post>) {
        searchResults.clear()
        searchResults.addAll(results)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)

        init {
            itemView.setOnClickListener {
                val post = searchResults[adapterPosition]
                onItemClick.invoke(post)
            }
        }

        fun bind(post: Post) {
            titleTextView.text = post.title
            authorTextView.text = post.authorID
            contentTextView.text = post.content
        }
    }
}
