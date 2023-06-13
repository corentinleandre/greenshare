package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {
    var currentId=0
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        createPost(view)

        val scrollView: ScrollView = view.findViewById(R.id.scroll)

        detectEndOfScroll(scrollView)
        //refreshScroll(scrollView)

        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun createPost(view: View){
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.fil)

        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")
        val mediaCollection =mFirestore.collection("Medias")
        // Tri par ordre décroissant des IDs
        collection
            .orderBy("date", Query.Direction.DESCENDING).limit(15)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val articles = ArrayList<Article>()
                for (document in querySnapshot) {
                    val id = document.id.toString()
                    Article.getArticle(id) { article ->
                        if (article != null) {
                            articles.add(article)
                        }
                        // Vérifie si tous les articles ont été récupérés
                        if (articles.size == querySnapshot.documents.size) {
                            articles.sortByDescending { it.date } // Trie les articles par ordre décroissant de la date
                            for (article in articles) {
                                val inflater = LayoutInflater.from(requireContext())
                                val postView = inflater.inflate(R.layout.post, null)
                                val cardView: CardView = postView.findViewById(R.id.touchCard)
                                linearContainer.addView(postView)

                                val args = Bundle()

                                if (article.mediasID != "") {
                                    val mediaDocRef = mediaCollection.document(article.mediasID)
                                    mediaDocRef.get().addOnSuccessListener { mediaSnapshot ->
                                        val mediaType = mediaSnapshot.getString("type")
                                        if (mediaType == "image") {
                                            val medias = FirebaseFirestore.getInstance().collection("Medias").document(article.id)
                                            medias.get().addOnSuccessListener { documentSnapshot ->
                                                val media1 = documentSnapshot.getString("media1")
                                                if (media1 != null) {
                                                    val mediaView: ImageView = postView.findViewById(R.id.imageView)
                                                    mediaView.layoutParams = LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        300
                                                    )
                                                    Glide.with(requireContext())
                                                        .load(media1)
                                                        .into(mediaView)
                                                }
                                            }


                                        } else if (mediaType == "video") {

                                        }


                                val textView: TextView = postView.findViewById(R.id.textView)
                                textView.text = article.title
                                cardView.setOnClickListener {
                                    args.putString("index", article.id)
                                    val readFragment = ReadFragment()
                                    readFragment.arguments = args
                                    handleClick(readFragment, args)
                                }
                                currentId = articles.lastOrNull()?.id?.toInt() ?: 0
                            }
                        }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                //
            }

    }

    private fun createnextPost(view: View) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.fil)

        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")
        collection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val articles = ArrayList<Article>()
                for (document in querySnapshot) {
                    val id = document.id.toString()
                    Article.getArticle(id) { article ->
                        if (article != null) {
                            articles.add(article)
                        }
                        // Vérifie si tous les articles ont été récupérés
                        if (articles.size == querySnapshot.documents.size) {
                            articles.sortByDescending { it.date } // Trie les articles par ordre décroissant de la date
                            val smallestId = articles.lastOrNull()?.id?.toInt() ?: 0
                            val idLimit = currentId - 5
                            val articlesToDisplay = articles.filter { it.id.toInt() < currentId && it.id.toInt() > idLimit }
                            for (article in articlesToDisplay) {
                                val inflater = LayoutInflater.from(requireContext())
                                val postView = inflater.inflate(R.layout.post, null)
                                val cardView: CardView = postView.findViewById(R.id.touchCard)
                                linearContainer.addView(postView)

                                val args = Bundle()

                                if (article.mediasID != "") {
                                    val medias = FirebaseFirestore.getInstance().collection("Medias").document(article.id)
                                    medias.get().addOnSuccessListener { documentSnapshot ->
                                        val media1 = documentSnapshot.getString("media1")
                                        if (media1 != null) {
                                            val mediaView: ImageView = postView.findViewById(R.id.imageView)
                                            mediaView.layoutParams = LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                300
                                            )
                                            Glide.with(requireContext())
                                                .load(media1)
                                                .into(mediaView)
                                        }
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
                            currentId = articlesToDisplay.lastOrNull()?.id?.toInt() ?: 0
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                //
            }
    }





    private fun detectEndOfScroll(scrollView: ScrollView) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))
            if (diff == 0) {
                //changer la fonction create post par la fonction add other posts qui ajoute les posts des id suivants
                createnextPost(view)
            }
        }
    }

//    private fun refreshScroll(scrollView: ScrollView) {
//        scrollView.viewTreeObserver.addOnScrollChangedListener {
//            if (scrollView.scrollY == 0) {
//                val linearContainer: LinearLayout = scrollView.findViewById(R.id.fil)
//                linearContainer.removeAllViews()
//                currentId=0
//                createPost(scrollView.getChildAt(0))
//
//            }
//        }
//    }

    fun handleClick(fragment: Fragment, arguments: Bundle) {
        fragment.arguments = arguments
        (activity as HomeActivity).moveToFragment(fragment)

    }
}