package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.widget.ImageView
import android.graphics.Bitmap.Config.ARGB_8888
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide

class UserFragment (authorID: String): Fragment() {
    var initials: String =""
    //val currentUser = FirebaseAuth.getInstance().currentUser?.email
    val currentUser = authorID

    fun createCustomUserIcon(context: Context, initials: String): Drawable {
        val size = 512 // Taille de l'icône (en pixels)

        val bitmap = Bitmap.createBitmap(size, size, ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(37,152,92) // color for background
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_user, container, false)

        //add the user's personal data

        val mFirestore = FirebaseFirestore.getInstance()

        //creates icon User


        mFirestore.collection("Users")
            .whereEqualTo("identification", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val context = requireContext()
                val userDocument = document.documents[0]
                val phoneNumber = userDocument.getString("telephone")
                val roleUser = userDocument.getString("role")
                val groupUser = userDocument.getString("group")
                val email = userDocument.getString("email")
                val authorID = userDocument.getString("identification")
                val userRights = userDocument.getString("rights")

                var list = userDocument.getString("email")?.split(".", "@")
                var firstname = list?.get(0)?.capitalize()
                var lastname = list?.get(1)?.capitalize()
                initials=list?.get(0)?.capitalize()?.substring(0, 1) + list?.get(1)?.capitalize()?.substring(0,1)
                val userIcon = createCustomUserIcon(context,initials)

                view.findViewById<ImageView>(R.id.userImageView).setImageDrawable(userIcon)
                view.findViewById<TextView>(R.id.user_name).text = "$firstname $lastname"
                view.findViewById<TextView>(R.id.user_role).text = "Role: "+roleUser
                view.findViewById<TextView>(R.id.user_numero).text = "Phone Number: "+phoneNumber
                view.findViewById<TextView>(R.id.user_email).text = "Email: "+email
                view.findViewById<TextView>(R.id.user_bureau).text = "Bureau : Plus tard " //TODO : add to data base "bureau"

                createPost(view,userRights!!,userIcon,authorID!!)
            }
        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun createPost(view: View,userRights:String,userIcon:Drawable,authorID:String){
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.postHere)

        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")
        // Tri par ordre décroissant des IDs
        collection
            .whereEqualTo("authorID",authorID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val articles = ArrayList<Article>()
                for (document in querySnapshot) {
                    val id = document.id
                    Article.getArticle(id) { article ->
                        if (article != null) {
                            articles.add(article)
                        }
                        // Vérifie si tous les articles ont été récupérés
                        if (articles.size == querySnapshot.documents.size) {
                            articles.sortByDescending { it.date } // Trie les articles par ordre décroissant de la date


                            for (article in articles) {

                                val articleVerified= article.verified

                                if (article != null && (articleVerified != "no" || (articleVerified == "no" && userRights != "0"))) {

                                    val inflater = LayoutInflater.from(requireContext())
                                    val postView = inflater.inflate(R.layout.post, null)
                                    val cardView: CardView = postView.findViewById(R.id.touchCard)
                                    postView.findViewById<ImageView>(R.id.profileImageView).setImageDrawable(userIcon)
                                    linearContainer.addView(postView)
                                    val flagImageView = postView.findViewById<ImageView>(R.id.redFlag)
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

    fun handleClick(fragment: Fragment, arguments: Bundle) {
        fragment.arguments = arguments
        (activity as HomeActivity).moveToFragment(fragment)
    }

    companion object {

    }
}
