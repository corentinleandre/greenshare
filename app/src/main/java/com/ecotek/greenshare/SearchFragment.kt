package com.ecotek.greenshare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchRecyclerView: RecyclerView
    private val adapter: SearchResultsAdapter = SearchResultsAdapter { article ->
        navigateToPostDetails(article)
    }
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var query: Query
    private lateinit var listener: ListenerRegistration

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
                // Réagit aux modifications de texte en temps réel
                return true
            }
        })

        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.adapter = adapter

        return view
    }

    private fun performSearch(query: String) {
        this.query = firestore.collection("posts")
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + "\uf8ff")

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
                Log.d("SearchFragment","snapshot found")
                val searchResults: List<Article> = snapshot.documents.mapNotNull { document ->
                    document.toObject(Article::class.java)
                }

                updateUIWithSearchResults(searchResults)
            }
        }
    }

    private fun updateUIWithSearchResults(searchResults: List<Article>) {
        adapter.setSearchResults(searchResults)
    }

    override fun onStop() {
        super.onStop()
        stopListening()
    }

    private fun stopListening() {
        listener.remove()
    }

    private fun navigateToPostDetails(article: Article) {
        val readFragment = ReadFragment()
        val args = Bundle()
        args.putString("key", article.title)
        args.putString("keyi", "ameen")
        args.putString("keyd", article.content)
        args.putString("index", article.id)
        readFragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, readFragment)
            .addToBackStack(null)
            .commit()
    }
}
