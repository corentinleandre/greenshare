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
import android.net.Uri
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
import android.widget.MediaController
import android.widget.VideoView
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
    private lateinit var videoLayout: LinearLayout
    fun createVideoView(context: Context, videoUri: Uri): VideoView {
        val videoView = VideoView(context)

        videoView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(0f, 0f)
            mediaPlayer.isLooping = true
            mediaPlayer.start()

        }

        return videoView!!
    }


    private fun createCustomUserIcon(context: Context, initials: String): Drawable {
        val size = 512 // Taille de l'icône (en pixels)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.rgb(37,152,92)// color for background
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
        val commentView = inflater2.inflate(R.layout.comment_input, null)
        val cardView: CardView = commentView.findViewById(R.id.touchCard)
        var mediaControls: MediaController? = null
        var listname = currentUser?.split(".", "@")
        val initials = listname?.get(0)?.capitalize()?.substring(0, 1) + listname?.get(1)?.capitalize()?.substring(0,1)
        val userIcon = createCustomUserIcon(requireContext(),initials)
        val photo = commentView.findViewById<ImageView>(R.id.profileImageView)
        photo.setImageDrawable(userIcon)
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
                val photo = view.findViewById<ImageView>(R.id.profileImageView)
                photo.setImageDrawable(userIcon)
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
                            val mediaView2:ImageView=postView.findViewById(R.id.imageViewv)
                            val mediaCountTextView: TextView = postView.findViewById(R.id.mediaCountTextView)
                            val mediaCount = getMediaCount(media2, media3, media4) // Fonction pour compter le nombre de médias
                            mediaCountTextView.text = " +$mediaCount "


                            val type = documentSnapshot.getString("type")
                            if(type=="image"){
                                Glide.with(requireContext())
                                    .load(media1)
                                    .into(mediaView1)}
                            if(type == "video") {
                                Glide.with(requireContext())
                                    .load(media1)
                                    .into(mediaView2)
                            }
                            val mediaUri: Uri = Uri.parse(media1)
                            mediaView2.setOnClickListener{
                                println("bonjour")
                                (activity as HomeActivity).moveToFragment(MediaPlayer(mediaUri))
                            }

                            mediaView1.setOnClickListener {
                                val mediaView2: ImageView = postView.findViewById(R.id.imageView2)
                                val mediaView3: ImageView = postView.findViewById(R.id.imageView3)
                                val mediaView4: ImageView = postView.findViewById(R.id.imageView4)

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
                            var listname = com?.authorEmail?.split(".", "@")
                            val initials = listname?.get(0)?.capitalize()?.substring(0, 1) + listname?.get(1)?.capitalize()?.substring(0,1)
                            val userIcon = createCustomUserIcon(requireContext(),initials)
                            val photo = commentView.findViewById<ImageView>(R.id.profileImageView)
                            photo.setImageDrawable(userIcon)
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
private fun getMediaCount(vararg media: String?): Int {
    var count = 0
    for (m in media) {
        if (!m.isNullOrEmpty()) {
            count++
        }
    }
    return count
}