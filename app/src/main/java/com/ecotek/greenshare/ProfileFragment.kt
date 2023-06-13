package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var currentId=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        //add the user's personal data
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserEmail = currentUser?.email
        var currentUserID:String = ""
        val mFirestore = FirebaseFirestore.getInstance()

        mFirestore.collection("Users")
            .whereEqualTo("email", currentUserEmail)
            .get()
            .addOnSuccessListener { document ->
                val userDocument = document.documents[0]
                val phoneNumber = userDocument.getString("telephone")
                val roleUser = userDocument.getString("role")
                val groupUser = userDocument.getString("group")
                currentUserID = userDocument.getString("identification").toString()
                var list = userDocument.getString("email")?.split(".", "@")
                var firstname = list?.get(0)?.capitalize()
                var lastname = list?.get(1)?.capitalize()
              /*  view.findViewById<TextView>(R.id.user_name).text = "$firstname $lastname"
                view.findViewById<TextView>(R.id.user_role).text = roleUser
                view.findViewById<TextView>(R.id.user_group).text = groupUser
                view.findViewById<TextView>(R.id.user_numero).text = phoneNumber
                view.findViewById<TextView>(R.id.user_email).text = currentUserEmail
                view.findViewById<TextView>(R.id.user_bureau).text = "Plus tard"*/ //TODO : add to data base "bureau"

                Log.d("ProfileFragment","Current user :" + currentUserID)
                createPost(view,currentUserID)
                val scrollView: ScrollView = view.findViewById(R.id.scroller)
                detectEndOfScroll(scrollView, currentUserID)
                refreshScroll(scrollView, currentUserID)

            }
        return view
    }
    @SuppressLint("MissingInflatedId")
    private fun createPost(view: View,currentUserID:String?){
        Log.d("ProfileFragment", "createPost called with currentUserID: $currentUserID")
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.scroller)

        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")
        // Tri par ordre décroissant des IDs
        collection
            .whereEqualTo("authorID", currentUserID)
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
                                val inflater = LayoutInflater.from(requireContext())
                                val postView = inflater.inflate(R.layout.post, null)
                                val cardView: CardView = postView.findViewById(R.id.touchCard)
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
                                currentId = articles.lastOrNull()?.id?.toInt() ?: 0
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                //
            }

    }

    private fun createnextPost(view: View, currentUserID: String?) {
        Log.d("ProfileFragment", "Current user 2 :$currentUserID")

        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.scroller)

        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")
        collection
            .whereEqualTo("authorID", currentUserID)
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
                                        val mediaView: ImageView = postView.findViewById(R.id.imageView)
                                        Glide.with(requireContext())
                                            .load(media1)
                                            .into(mediaView)
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





    private fun detectEndOfScroll(scrollView: ScrollView, currentUserID: String?) {
        Log.d("ProfileFragment", "detectEndOfScroll called with currentUserID: $currentUserID")
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))
            if (diff == 0) {
                //changer la fonction create post par la fonction add other posts qui ajoute les posts des id suivants
                createnextPost(view, currentUserID)
            }
        }
    }

    private fun refreshScroll(scrollView: ScrollView, currentUserID: String?) {
        Log.d("ProfileFragment", "refreshScroll called with currentUserID: $currentUserID")
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.scrollY == 0) {
                currentId=0

                createPost(scrollView.getChildAt(0),currentUserID)

            }
        }
    }

    fun handleClick(fragment: Fragment, arguments: Bundle) {
        fragment.arguments = arguments
        (activity as HomeActivity).moveToFragment(fragment)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}