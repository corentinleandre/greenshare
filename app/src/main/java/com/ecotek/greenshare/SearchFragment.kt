package com.ecotek.greenshare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var noResultsTextView: TextView
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var query: Query
    private lateinit var listener: ListenerRegistration

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        noResultsTextView = view.findViewById(R.id.noResultsTextView)
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
                // Réagit aux modifications de texte en temps réel
                return true
            }
        })

        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    private fun performSearch(query: String) {
        this.query = firestore.collection("Article")
            .orderBy("title")
            .startAt(query)
            .endAt(query + "\uf8ff")

        Log.d("SearchFragment", "Performing search for query: $query")

        // Réinitialise les résultats de la recherche précédente
        showNoResultsMessage(false)

        startListening()
    }



    //TODO permettre d'ignorer les majuscules et minucules
    /*private fun performSearch(query: String) {
        val lowercaseQuery = query.lowercase(Locale.ROOT)

        this.query = firestore.collection("Article")
            .orderBy("title")
            .whereGreaterThanOrEqualTo("titleLowercase", lowercaseQuery)
            .whereLessThanOrEqualTo("titleLowercase", lowercaseQuery + "\uf8ff")

        Log.d("SearchFragment", "Performing search for query: $query")

        // Réinitialise les résultats de la recherche précédente
        showNoResultsMessage(false)

        startListening()
    }*/



    private fun startListening() {
        listener = query.addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                // Gérer l'erreur
                Log.e("Search Error", "Firestore Error: ${exception.message}")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                Log.d("SearchFragment", "snapshot found")
                val searchResults: List<String> = snapshot.documents.mapNotNull { document ->
                    document.getString("id")
                }

                view?.let { updateUIWithSearchResults(searchResults, it, ) }
            }
        }
    }

    private fun updateUIWithSearchResults(searchResults: List<String>, view: View) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.search_fragment)

        if (searchResults.isNotEmpty()) {
            for (articleId in searchResults) {
                Article.getArticle(articleId) { article ->
                    if (article != null) {
                        val inflater = LayoutInflater.from(requireContext())
                        val postView = inflater.inflate(R.layout.post, null)
                        val cardView: CardView = postView.findViewById(R.id.touchCard)
                        linearContainer.addView(postView, 1)
                        showNoResultsMessage(false)

                        val args = Bundle()

                        if (article.mediasID != "") {
                            val medias = FirebaseFirestore.getInstance().collection("Medias").document(article.id)
                            medias.get().addOnSuccessListener { documentSnapshot ->
                                val media1 = documentSnapshot.getString("media1")
                                val mediaView: ImageView = postView.findViewById(R.id.imageView)
                                Glide.with(requireContext())
                                    .load(media1)
                                    .into(mediaView)
                            }
                        }

                        val textView: TextView = postView.findViewById(R.id.textView)
                        textView.text = article.title

                        cardView.setOnClickListener {
                            args.putString("index", article.id)
                            val readFragment = ReadFragment()
                            readFragment.arguments = args
                            handleClick(readFragment, args)
                        }
                    }
                }
            }
        } else {
            showNoResultsMessage(true)
        }
    }




    private fun showNoResultsMessage(noResults: Boolean) {
        if (noResults) {
            noResultsTextView.visibility = View.VISIBLE // Affiche le message d'absence de résultats
        } else {
            noResultsTextView.visibility = View.GONE // Masque le message d'absence de résultats
        }
    }

    override fun onStop() {
        super.onStop()
        stopListening()
    }


    private fun stopListening() {
        if (::listener.isInitialized) {
            listener.remove()
        }
    }

    fun handleClick(fragment: Fragment, arguments: Bundle) {
        fragment.arguments = arguments
        (activity as HomeActivity).moveToFragment(fragment)

    }
}

