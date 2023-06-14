package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ReadFragment : Fragment() {
    val im = 10 // Nombre d'ViewImage et ViewText à ajouter
    val currentUser = FirebaseAuth.getInstance().currentUser?.email



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_read, container, false)
        createPost(view)
        return view
    }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    private fun createPost(view: View) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }
        var userRights = "0"
        val mFirestore = FirebaseFirestore.getInstance()
        val collectionuser = mFirestore.collection("Users")
        collectionuser
            .whereEqualTo("email", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val userDocument = document.documents[0]
                userRights = userDocument.getString("rights").toString()
            }

        val linearContainer: LinearLayout = view.findViewById(R.id.fil)
        val inflater = LayoutInflater.from(requireContext())
        val postView = inflater.inflate(R.layout.post_read, null)
        linearContainer.addView(postView)

        val imageView: ImageView = postView.findViewById(R.id.imageView1)
        val imageName = arguments?.getString("keyi")
        val index=arguments?.getString("index")
        val numlikes = view.findViewById<TextView>(R.id.likeCount)
        val liker = view.findViewById<ImageButton>(R.id.likeButton)

        fun showlikes(){
            Article.getArticle(index.toString()) { article ->
                if (article != null){
                    val listlikes = article.likes.split(",").toMutableList()
                    numlikes.text = (listlikes.count()-1).toString()
                    val username = FirebaseAuth.getInstance().currentUser?.email.toString()
                    if (listlikes.contains(username)){
                        liker.setImageResource(R.drawable.baseline_like)
                    }
                }
            }
        }
        showlikes()

        liker.setOnClickListener{
            Article.getArticle(index.toString()) { article ->
                if (article != null){
                    val listlikes = article.likes.split(",").toMutableList()
                    val username = FirebaseAuth.getInstance().currentUser?.email.toString()
                    if (listlikes.contains(username)){
                        liker.setImageResource(R.drawable.baseline_unlike)
                        listlikes.remove(username)
                    }
                    else {
                        liker.setImageResource(R.drawable.baseline_like)
                        listlikes.add(username)
                    }
                    val articleRef = FirebaseFirestore.getInstance().collection("Article").document(index.toString())
                    articleRef.update("likes", listlikes.joinToString(","))
                    showlikes()
                }
            }
        }

        val inflater2 = LayoutInflater.from(requireContext())
        val commentView = inflater2.inflate(R.layout.comment, null)
        val cardView: CardView = commentView.findViewById(R.id.touchCard)
        val commenterlayout = view.findViewById<LinearLayout>(R.id.commenter)
        commenterlayout.addView(commentView)


        Article.getArticle(index.toString()){article ->
            if (article != null) {
                if (article.mediasID != "") {
                    var medias = FirebaseFirestore.getInstance().collection("Medias")
                        .document(index.toString())
                    medias.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val media1 = documentSnapshot.getString("media1")
                            val media2 = documentSnapshot.getString("media2")
                            val media3 = documentSnapshot.getString("media3")
                            val media4 = documentSnapshot.getString("media4")
                            val buttonCross = postView.findViewById<ImageButton>(R.id.buttonCross)
                            val buttonCheck = postView.findViewById<ImageButton>(R.id.buttonCheck)
                            val mediaView1: ImageView = postView.findViewById(R.id.imageView1)
                            Glide.with(requireContext())
                              .load(media1)
                              .into(mediaView1)
                            if (userRights != "0") {
                                buttonCheck.visibility = View.VISIBLE
                                buttonCross.visibility = View.VISIBLE
                            }
                            if ((userRights != "0" && article.verified == "yes")) {
                                buttonCross.visibility=View.VISIBLE
                                buttonCross.setOnClickListener {
                                    val mFirestore = FirebaseFirestore.getInstance()
                                    val collection = mFirestore.collection("Article")
                                    collection
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (document in querySnapshot) {
                                                if (document.getString("id") == index.toString()) {
                                                    document.reference.delete()
                                                        .addOnSuccessListener {
                                                            // Suppression réussie, retourner sur la page d'accueil
                                                            val homeFragment = HomeFragment()
                                                            val fragmentManager =
                                                                requireActivity().supportFragmentManager
                                                            fragmentManager.beginTransaction()
                                                                .replace(
                                                                    R.id.container_view,
                                                                    homeFragment
                                                                )
                                                                .commit()
                                                        }.addOnFailureListener { exception ->
                                                            // Gérer les erreurs de suppression ici
                                                        }
                                                    break  // Sortir de la boucle après la suppression de l'article
                                                }
                                            }
                                        }
                                }
                            } else {
                                buttonCross.visibility = View.VISIBLE
                                buttonCheck.visibility = View.VISIBLE
                                buttonCross.setOnClickListener {
                                    val mFirestore = FirebaseFirestore.getInstance()
                                    val collection = mFirestore.collection("Article")
                                    collection
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (document in querySnapshot) {
                                                if (document.getString("id") == index.toString()) {
                                                    document.reference.delete()
                                                        .addOnSuccessListener {
                                                            // Suppression réussie, retourner sur la page d'accueil
                                                            val homeFragment = HomeFragment()
                                                            val fragmentManager =
                                                                requireActivity().supportFragmentManager
                                                            fragmentManager.beginTransaction()
                                                                .replace(
                                                                    R.id.container_view,
                                                                    homeFragment
                                                                )
                                                                .commit()
                                                        }.addOnFailureListener { exception ->
                                                            // Gérer les erreurs de suppression ici
                                                        }
                                                    break  // Sortir de la boucle après la suppression de l'article
                                                }
                                            }
                                        }
                                }
                                buttonCheck.setOnClickListener {
                                    val mFirestore = FirebaseFirestore.getInstance()
                                    val collection = mFirestore.collection("Article")
                                    collection
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (document in querySnapshot) {
                                                if (document.getString("id") == index.toString()) {
                                                    document.reference.update("verified", "yes")
                                                }
                                            }
                                            // Retour à la page d'accueil
                                            val homeFragment = HomeFragment()
                                            val fragmentManager =
                                                requireActivity().supportFragmentManager
                                            fragmentManager.beginTransaction()
                                                .replace(R.id.container_view, homeFragment)
                                                .commit()
                                        }.addOnFailureListener { exception ->
                                            // Gérer les erreurs de suppression ici
                                        }
                                }
                            }
                            mediaView1.setOnClickListener {
                                val mediaView2: ImageView = postView.findViewById(R.id.imageView2)
                                val mediaView3: ImageView = postView.findViewById(R.id.imageView3)
                                val mediaView4: ImageView = postView.findViewById(R.id.imageView4)
                                mediaView2.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                mediaView3.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                mediaView4.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                Glide.with(requireContext())
                                    .load(media2)
                                    .into(mediaView2)
                                Glide.with(requireContext())
                                    .load(media3)
                                    .into(mediaView3)
                                Glide.with(requireContext())
                                    .load(media4)
                                    .into(mediaView4)
                            }
                        }
                }
                val comments = article.commentID.split(",").toMutableList()
                println(comments)
                println("here")
                for (comment in comments){
                    println("hello")
                    val inflater = LayoutInflater.from(requireContext())
                    val commentView = inflater.inflate(R.layout.comment, null)
                    val cardView: CardView = commentView.findViewById(R.id.touchCard)
                    val commenterlayout = view.findViewById<LinearLayout>(R.id.commentLayout)
                    commenterlayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    FirebaseFirestore.getInstance().collection("Comment").document(comment)
                        .get()
                        .addOnSuccessListener {it ->
                            val com = it.toObject(Comment::class.java)
                            val commentTextView: TextView = commentView.findViewById(R.id.textView)
                            commentTextView.text = com?.content
                        }
                    commenterlayout.addView(commentView)
                }
            }
        }

        val textView: TextView = postView.findViewById(R.id.textView)
        Article.getArticle(index.toString()) { article ->
            if (article != null) {
                textView.text = article.title
            }
        }

        val description:TextView=postView.findViewById(R.id.description)
        Article.getArticle(index.toString()) { article ->
            if (article != null) {
                description.text = article.content
            }
        }
    }
}