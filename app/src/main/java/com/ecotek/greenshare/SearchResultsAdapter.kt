package com.ecotek.greenshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchResultsAdapter(private val onItemClick: (Article) -> Unit) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    private var searchResults: List<Article> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val article = searchResults[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = searchResults.size

    fun setSearchResults(results: List<Article>) {
        searchResults = results
        notifyDataSetChanged()
    }

    inner class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.resultImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.resultTitleTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val article = searchResults[position]
                    onItemClick(article)
                }
            }
        }

        fun bind(article: Article) {
            titleTextView.text = article.title

            Glide.with(itemView)
                .load("ameen")
                .into(imageView)
        }
    }
}
