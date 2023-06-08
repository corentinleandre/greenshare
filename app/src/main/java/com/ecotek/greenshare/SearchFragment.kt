import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.ecotek.greenshare.Post
import com.ecotek.greenshare.R
import com.ecotek.greenshare.SearchResultsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerView: RecyclerView
    private val searchResults: MutableList<Post> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchView = view.findViewById(R.id.searchView)
        searchRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = "Search articles"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Réagit aux modification de texte en temps réel
                return true
            }
        })

        return view
    }

    private fun performSearch(query: String) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val postsRef: DatabaseReference = database.getReference("posts")

        // recherche des posts par titre et tags
        val searchTitle: Query = postsRef.orderByChild("title").startAt(query).endAt(query + "\uf8ff")
        //val searchTag: Query = postsRef.orderByChild("tags").equalTo(query)

        searchTitle.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Traitement des résultats de la recherche
                searchResults.clear() // Efface les résultats précédents
                for (postSnapshot in dataSnapshot.children) {
                    val post: Post? = postSnapshot.getValue(Post::class.java)
                    post?.let {
                        searchResults.add(it)
                    }
                }
                // Met à jour l'interface utilisateur avec les résultats de la recherche
                updateUIWithSearchResults()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gestion des erreur
                Log.e("Search Error", "Firebase Database Error: ${databaseError.message}")
            }
        })
    }

    private fun updateUIWithSearchResults() {
        // Crée un adapter pour le RecyclerView avec les résultats de recherche
        val adapter = SearchResultsAdapter(searchResults)
        // Applique l'adapter au RecyclerView
        searchRecyclerView.adapter = adapter
    }
}
