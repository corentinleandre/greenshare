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
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {
    var currentId=0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        createPost(view)
        val scrollView: ScrollView = view.findViewById(R.id.scroll)
        detectEndOfScroll(scrollView)
        refreshScroll(scrollView)

        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun createPost(view: View) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.fil)


        //Fonction qui récupère l'id du post le plus récent de la base

        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")

        collection
            .get()
            .addOnSuccessListener { querySnapshot ->
                var highestId = 0
                for (document in querySnapshot) {
                    val id = document.id.toIntOrNull()
                    if (id != null && id > highestId) {
                        highestId = id
                    }
                }
                val id10 = highestId - 4
                while (highestId > id10){
                    Article.getArticle(highestId.toString()){article ->
                        if (article != null){
                            var index=article.id.toInt()
                            val inflater = LayoutInflater.from(requireContext())
                            val postView = inflater.inflate(R.layout.post, null)
                            val cardView: CardView = postView.findViewById(R.id.touchCard)

                            linearContainer.addView(postView)
                            val args = Bundle()
                            //val cardView: CardView = view.findViewById(R.id.touchCard)
                            //cardView.setOnClickListener {}

                            //Rajouter une margin TOP de 40dp pour le tout premier post
                            if (index == highestId) {
                                val layoutParams = postView.layoutParams as ViewGroup.MarginLayoutParams
                                val marginTop =
                                    resources.getDimensionPixelSize(R.dimen.margin_top) // Remplace R.dimen.margin_top par la ressource correspondante à 20dp
                                layoutParams.setMargins(
                                    layoutParams.leftMargin,
                                    marginTop,
                                    layoutParams.rightMargin,
                                    layoutParams.bottomMargin
                                )
                                postView.layoutParams = layoutParams
                            }

                            if (article.mediasID!= "") {
                                println("bonjour")

                                var medias=FirebaseFirestore.getInstance().collection("Medias").document(highestId.toString())
                                medias.get()
                                    .addOnSuccessListener {documentSnapshot->
                                        val media1=documentSnapshot.getString("media1")
                                        println("voiture")
                                        val mediaView: ImageView= postView.findViewById(R.id.imageView)
                                        //mediaView.setImageResource(R.drawable.ameen)
                                        mediaView.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/greenshare-3d0ed.appspot.com/o/56%2Fimage1?alt=media&token=780b1f8c-d74c-45be-acc7-4fc1c0ef9424"))


                                    }
                            }


                            val textView: TextView = postView.findViewById(R.id.textView)
                            textView.text = article.title



                            cardView.setOnClickListener {
                                val textView: TextView = postView.findViewById(R.id.textView)

                                textView.text = article.title


                                args.putString("index",article.id)



                                val readFragment = ReadFragment()
                                readFragment.arguments = args
                                handleClick(readFragment, args)
                            }



                            println(article.id)
                            println(article.content)

                        }
                    }
                    highestId -= 1
                }
                if (highestId > currentId) {
                    currentId = highestId}

            }
            .addOnFailureListener { exception ->
                //
            }


        }
    //Rajouter une vérification pour bloquer quand la base n'a plus de posts
    private fun createnextPost(view: View) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.fil)


        //Fonction qui récupère l'id du post le plus récent de la base

        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")
        collection
            .get()
            .addOnSuccessListener { querySnapshot ->
                var highestId = 0
                for (document in querySnapshot) {
                    val id = document.id.toIntOrNull()
                    if (id != null && id > highestId) {
                        highestId = id
                    }
                }
                val id10 = currentId - 5
                while (currentId > id10 && currentId < highestId){
                    Article.getArticle(currentId.toString()){article ->
                        if (article != null){
                            var index=article.id.toInt()
                            val inflater = LayoutInflater.from(requireContext())
                            val postView = inflater.inflate(R.layout.post, null)
                            val cardView: CardView = postView.findViewById(R.id.touchCard)

                            linearContainer.addView(postView)
                            val args = Bundle()
                            //val cardView: CardView = view.findViewById(R.id.touchCard)
                            //cardView.setOnClickListener {}

                            //Rajouter une margin TOP de 40dp pour le tout premier post
                            if (index == highestId) {
                                val layoutParams = postView.layoutParams as ViewGroup.MarginLayoutParams
                                val marginTop =
                                    resources.getDimensionPixelSize(R.dimen.margin_top) // Remplace R.dimen.margin_top par la ressource correspondante à 20dp
                                layoutParams.setMargins(
                                    layoutParams.leftMargin,
                                    marginTop,
                                    layoutParams.rightMargin,
                                    layoutParams.bottomMargin
                                )
                                postView.layoutParams = layoutParams
                            }

                            val textView: TextView = postView.findViewById(R.id.textView)
                            textView.text = article.title



                            cardView.setOnClickListener {
                                val textView: TextView = postView.findViewById(R.id.textView)

                                textView.text = article.title


                                args.putString("index",article.id)



                                val readFragment = ReadFragment()
                                readFragment.arguments = args
                                handleClick(readFragment, args)
                            }



                            println(article.id)
                            println(article.content)

                        }
                    }
                    currentId -= 1
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

        private fun refreshScroll(scrollView: ScrollView) {
            scrollView.viewTreeObserver.addOnScrollChangedListener {
                if (scrollView.scrollY == 0) {

                    createPost(scrollView.getChildAt(0))
                }
            }
        }

        fun handleClick(fragment: Fragment, arguments: Bundle) {
            fragment.arguments = arguments
            (activity as HomeActivity).moveToFragment(fragment)

        }

    private fun createImageView(context: Context, imageUri: Uri): ImageView {
        val imageView = ImageView(context)
        imageView.layoutParams = LinearLayout.LayoutParams(

            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageView.setPadding(0, 0, 0, 0) // Optional: Remove padding if present
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        imageView.setImageURI(imageUri)

        return imageView
    }



}