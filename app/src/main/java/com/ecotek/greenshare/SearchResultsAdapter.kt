import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ecotek.greenshare.R
class SearchResultsAdapter(private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder>() {

    private var searchResults: MutableList<Pair<String, String>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val (title, imageUrl) = searchResults[position]
        holder.bind(title, imageUrl)
    }

    override fun getItemCount(): Int = searchResults.size

    fun setSearchResults(results: List<Pair<String, String>>) {
        searchResults.clear()
        searchResults.addAll(results)
        notifyDataSetChanged()
    }


    inner class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.resultImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.resultTitleTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val (title, imageUrl) = searchResults[position]
                    onItemClick(imageUrl)
                }
            }
        }

        fun bind(title: String, imageUrl: String) {
            titleTextView.text = title

            if (imageUrl.isNotEmpty()) {
                imageView.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)
            } else {
                imageView.visibility = View.GONE
            }
        }
    }

}
