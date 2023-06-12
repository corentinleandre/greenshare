package com.ecotek.greenshare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var noResultsTextView: TextView
    private val adapter: SearchResultsAdapter = SearchResultsAdapter { title ->
        navigateToPostDetails(title)
    }
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
        searchRecyclerView.adapter = adapter

        return view
    }

    private fun performSearch(query: String) {
        this.query = firestore.collection("Article")
            .whereEqualTo("title", query)

        Log.d("SearchFragment", "Performing search for query: $query")

        startListening()
    }

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
                    document.getString("title")
                }

                updateUIWithSearchResults(searchResults)
            }
        }
    }

    private fun updateUIWithSearchResults(searchResults: List<String>) {
        if (searchResults.isEmpty()) {
            showNoResultsMessage(true)
            return
        }

        val resultsWithImageUrls = mutableListOf<Pair<String, String>>() // Pair of title and image URL
        var isPhotoAvailable = true

        firestore.collection("Article")
            .whereIn("title", searchResults)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title")
                    val articleId = document.id
                    firestore.collection("Medias")
                        .document(articleId)
                        .get()
                        .addOnSuccessListener { mediaDocument ->
                            val imageUrl = mediaDocument.getString("media1")
                            if (imageUrl != null && title != null) {
                                resultsWithImageUrls.add(Pair(title, imageUrl))
                            } else {
                                isPhotoAvailable = false
                            }
                            // Vérifie si toutes les paires d'images ont été récupérées
                            if (resultsWithImageUrls.size == searchResults.size) {
                                if (isPhotoAvailable) {
                                    adapter.setSearchResults(resultsWithImageUrls)
                                } else {
                                    adapter.setSearchResults(searchResults.map { Pair(it, "") })
                                }
                                showNoResultsMessage(false)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("SearchFragment", "Error retrieving image URL: ${exception.message}")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SearchFragment", "Error retrieving articles: ${exception.message}")
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

    private fun navigateToPostDetails(title: String) {
        val args = Bundle()
        args.putString("key", title)
        val readFragment = ReadFragment()
        readFragment.arguments = args
        handleClick(readFragment, args)
    }

    fun handleClick(fragment: Fragment, arguments: Bundle) {
        fragment.arguments = arguments
        (activity as HomeActivity).moveToFragment(fragment)
    }
}

