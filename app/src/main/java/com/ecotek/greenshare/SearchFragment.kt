package com.ecotek.greenshare

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchScrollView: ScrollView
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var query: Query
    private lateinit var listener: ListenerRegistration
    private lateinit var linearContainer: LinearLayout
    var userRights="0"
    val currentUser = FirebaseAuth.getInstance().currentUser?.email


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val mFirestore = FirebaseFirestore.getInstance()
        val collectionuser = mFirestore.collection("Users")
        collectionuser
            .whereEqualTo("email", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val userDocument = document.documents[0]
                userRights = userDocument.getString("rights").toString()
            }
        searchView = view.findViewById(R.id.searchView)
        searchScrollView = view.findViewById(R.id.searchResultsScrollView)
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

        linearContainer = view.findViewById(R.id.fil1)

        return view
    }

    private fun performSearch(query: String) {
        this.query = firestore.collection("Article")
            .orderBy("title")
            .startAt(query)
            .endAt(query + "\uf8ff")

        Log.d("SearchFragment", "Performing search for query: $query")

        searchView.clearFocus()

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
                    document.getString("id")
                }

                updateUIWithSearchResults(searchResults)
            }
        }
    }

    private fun updateUIWithSearchResults(searchResults: List<String>) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        linearContainer.removeAllViews()

        if (searchResults.isNotEmpty()) {
            for (articleId in searchResults) {
                Article.getArticle(articleId) { article ->
                    if (article != null) {
                        val inflater = LayoutInflater.from(requireContext())
                        val postView = inflater.inflate(R.layout.post, null)
                        val cardView: CardView = postView.findViewById(R.id.touchCard)
                        val articleVerified= article.verified
                        val flagImageView = postView.findViewById<ImageView>(R.id.redFlag)
                        if (userRights == "0" || articleVerified == "yes") {
                            flagImageView.visibility = View.INVISIBLE
                        }
                        linearContainer.addView(postView)

                        val args = Bundle()

                        if (article.mediasID != "") {
                            val medias = FirebaseFirestore.getInstance().collection("Medias").document(article.id)
                            medias.get().addOnSuccessListener { documentSnapshot ->
                                val media1 = documentSnapshot.getString("media1")
                                val mediaView: ImageView = postView.findViewById(R.id.imageView)
                                Glide.with(requireContext())
                                    .load(media1)
                                    .into(mediaView)
                                mediaView.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    300
                                )
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

                        val profileImageView: ImageView = postView.findViewById(R.id.profileImageView)
                        HomeFragment().getUserInitials(article.authorID) { userInitials ->
                            val userIcon = HomeFragment().createCustomUserIcon(requireContext(), userInitials)
                            profileImageView.setImageDrawable(userIcon)
                        }


                        profileImageView.setOnClickListener {
                            (activity as HomeActivity).moveToFragment(UserFragment(article.authorID))
                        }
                    }
                }
            }
        } else {
            val alertDialog: AlertDialog? = AlertDialog.Builder(requireActivity()).create()
            if (alertDialog != null) {
                alertDialog.setTitle("Désolé")
                alertDialog.setMessage("Aucun article n'a été trouvé avec votre recherche")
                alertDialog.show()
            }
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
