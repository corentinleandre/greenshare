package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.firestore.SetOptions


class ReadFragment : Fragment() {
    val im = 10 // Nombre d'ViewImage et ViewText à ajouter
    val currentUser = FirebaseAuth.getInstance().currentUser?.email
    var userId=""
    var userRights = ""



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_read, container, false)
        val mFirestore = FirebaseFirestore.getInstance()
        val collectionuser = mFirestore.collection("Users")
        collectionuser
            .whereEqualTo("email", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val userDocument = document.documents[0]
                userRights = userDocument.getString("rights").toString()
                userId= userDocument.getString("identification").toString()
                createPost(view)
            }

        return view
    }

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    private fun createPost(view: View) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
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
        val commentView = inflater2.inflate(R.layout.comment_input, null)
        val cardView: CardView = commentView.findViewById(R.id.touchCard)
        val commenterlayout = view.findViewById<LinearLayout>(R.id.commenter)
        commenterlayout.addView(commentView)
        val addCommentButton: Button = commentView.findViewById(R.id.addCommentButton)
        val commentInput: EditText = commentView.findViewById(R.id.commentInput)
        addCommentButton.setOnClickListener {
            val newComment = commentInput.text.toString().trim()
            if (newComment.isNotEmpty()) {
                val sender = Comment(currentUser.toString(),newComment,"")
                val collection = FirebaseFirestore.getInstance().collection("Comment")
                collection
                    .get()
                    .addOnSuccessListener {
                        var highestId = 0
                        for (document in it) {
                            val id = document.id.toIntOrNull()
                            if (id != null && id > highestId) {
                                highestId = id
                            }
                        }
                        highestId += 1
                        collection
                            .document(highestId.toString())
                            .set(sender, SetOptions.merge())
                        FirebaseFirestore.getInstance().collection("Article")
                            .document(index.toString())
                            .get()
                            .addOnSuccessListener {
                                val existingcomments = it.getString("commentID")?.split(",")?.toMutableList()
                                existingcomments?.add(highestId.toString())
                                val commentRef = FirebaseFirestore.getInstance().collection("Article").document(index.toString())
                                commentRef.update("commentID", existingcomments?.joinToString(","))
                            }
                    }

                val inflater = LayoutInflater.from(requireContext())
                val newCommentView = inflater.inflate(R.layout.comment, null)
                val newCommentTextView: TextView = newCommentView.findViewById(R.id.textView)
                newCommentTextView.text = newComment
                val commentslayout = view.findViewById<LinearLayout>(R.id.commentLayout)
                commentslayout.addView(newCommentView)
                commentInput.text = null
            }
        }


        Article.getArticle(index.toString()){article ->
            if (article != null) {
                val buttonCross = postView.findViewById<ImageButton>(R.id.buttonCross)
                val buttonCheck = postView.findViewById<ImageButton>(R.id.buttonCheck)
                if ((userRights != "0") || (userRights == "0" && userId == article.authorID)) {
                    Log.d("marie",userRights)
                    Log.d("marie",userId)
                    Log.d("marie",article.authorID)
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
                }
                if (userRights != "0" && article.verified == "no") {
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
                if (article.mediasID != "") {
                    var medias = FirebaseFirestore.getInstance().collection("Medias")
                        .document(index.toString())
                    medias.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val media1 = documentSnapshot.getString("media1")
                            val media2 = documentSnapshot.getString("media2")
                            val media3 = documentSnapshot.getString("media3")
                            val media4 = documentSnapshot.getString("media4")
                            val mediaView1: ImageView = postView.findViewById(R.id.imageView1)
                            mediaView1.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            Glide.with(requireContext())
                                .load(media1)
                                .into(mediaView1)

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
                comments.remove("default")
                for (comment in comments){
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