package com.ecotek.greenshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchResultsAdapter(private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    private var searchResults: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val title = searchResults[position]
        holder.bind(title)
    }

    override fun getItemCount(): Int = searchResults.size

    fun setSearchResults(results: List<String>) {
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
                    val title = searchResults[position]
                    onItemClick(title)
                }
            }
        }

        fun bind(title: String) {
            titleTextView.text = title

            // Utilisez Glide pour charger l'image correspondante ici
            Glide.with(itemView)
                .load("ameen")  // Replace "image_url_here" with the actual URL of the image
                .into(imageView)
        }
    }
}

