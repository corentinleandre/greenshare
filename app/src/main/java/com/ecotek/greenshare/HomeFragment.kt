package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {
    var currentId=0
    var userRights="0"
    val currentUser = FirebaseAuth.getInstance().currentUser?.email

    private lateinit var progressBar: ProgressBar

    private fun getUserInitials(authorID: String, onComplete: (String) -> Unit) {
        val mFirestore = FirebaseFirestore.getInstance()
        val userRef = mFirestore.collection("Users").document(authorID)
        userRef.get().addOnSuccessListener { document ->
            val firstName = document.getString("firstname") ?: ""
            val lastName = document.getString("lastname") ?: ""

            val initials = buildString {
                if (firstName.isNotEmpty()) {
                    append(firstName[0].uppercase())
                }
                if (lastName.isNotEmpty()) {
                    append(lastName[0].uppercase())
                }
            }

            onComplete(initials)
        }
    }


    private fun createCustomUserIcon(context: Context, initials: String): Drawable {
        val size = 512 // Taille de l'icône (en pixels)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(94,132,71) // color for background
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        val textSize = size / initials.length.toFloat()
        paint.color = Color.WHITE // color for text
        paint.textSize = textSize
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)
        val x = canvas.width / 2
        val y = (canvas.height / 2) - (textBounds.top + textBounds.bottom) / 2

        canvas.drawText(initials, x.toFloat(), y.toFloat(), paint)

        return BitmapDrawable(context.resources, bitmap)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        createPost(view)
        val scrollView: ScrollView = view.findViewById(R.id.scroll)
        detectEndOfScroll(scrollView)
        refreshScroll(scrollView)

        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun createPost(view: View){
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.fil)

        val mFirestore = FirebaseFirestore.getInstance()
        val collectionuser = mFirestore.collection("Users")
        collectionuser
            .whereEqualTo("email", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val userDocument = document.documents[0]
                userRights = userDocument.getString("rights").toString()
            }
        val collection = mFirestore.collection("Article")
        // Tri par ordre décroissant des IDs
        collection
            .orderBy("date", Query.Direction.DESCENDING).limit(8)
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

                                val articleVerified = article.verified.toString()

                                if (article != null && (articleVerified != "no" || (articleVerified == "no" && userRights != "0"))) {

                                    val inflater = LayoutInflater.from(requireContext())
                                    val postView = inflater.inflate(R.layout.post, null)
                                    getUserInitials(article.authorID) { userInitials ->
                                        val userIcon = createCustomUserIcon(requireContext(), userInitials)
                                        val profileImageView = postView.findViewById<ImageView>(R.id.profileImageView)
                                        profileImageView.setImageDrawable(userIcon)
                                    }

                                    val cardView: CardView =
                                        postView.findViewById(R.id.touchCard)
                                    linearContainer.addView(postView)
                                    val flagImageView =
                                        postView.findViewById<ImageView>(R.id.redFlag)
                                    if (userRights == "0" || articleVerified == "yes") {
                                        flagImageView.visibility = View.INVISIBLE
                                    }

                                    val args = Bundle()

                                    if (article.mediasID != "") {
                                        val medias =
                                            FirebaseFirestore.getInstance().collection("Medias")
                                                .document(article.id)
                                        medias.get().addOnSuccessListener { documentSnapshot ->
                                            val media1 = documentSnapshot.getString("media1")
                                            if (media1 != null) {
                                                val mediaView: ImageView =
                                                    postView.findViewById(R.id.imageView)
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

                                    val profileImageView: ImageView = postView.findViewById(R.id.profileImageView)
                                    profileImageView.setOnClickListener {
                                        (activity as HomeActivity).moveToFragment(UserFragment(article.authorID))
                                    }

                                    currentId = articles.lastOrNull()?.id?.toInt() ?: 0

                                }


                            }

                        }
                    }
                }
            }
            .addOnFailureListener { exception ->}
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
                        val articleVerified=article?.verified.toString()
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
                                val flagImageView = postView.findViewById<ImageView>(R.id.redFlag)
                                getUserInitials(article.authorID) { userInitials ->
                                    val userIcon = createCustomUserIcon(requireContext(), userInitials)
                                    val profileImageView = postView.findViewById<ImageView>(R.id.profileImageView)
                                    profileImageView.setImageDrawable(userIcon)
                                }
                                if (userRights == "0" || articleVerified == "yes") {
                                    flagImageView.visibility = View.INVISIBLE
                                }

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

                                val profileImageView: ImageView = postView.findViewById(R.id.profileImageView)
                                profileImageView.setOnClickListener {
                                    (activity as HomeActivity).moveToFragment(UserFragment(article.authorID))
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





     fun detectEndOfScroll(scrollView: ScrollView) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))
            if (diff == 0) {
                //changer la fonction create post par la fonction add other posts qui ajoute les posts des id suivants
                createnextPost(view)
            }
        }
    }

     fun refreshScroll(scrollView: ScrollView) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.scrollY == 0) {
                val linearContainer: LinearLayout = scrollView.findViewById(R.id.fil)
                linearContainer.removeAllViews()
                currentId=0
                createPost(scrollView.getChildAt(0))

            }
        }
    }

    fun handleClick(fragment: Fragment, arguments: Bundle) {
        fragment.arguments = arguments
        (activity as HomeActivity).moveToFragment(fragment)
    }
}