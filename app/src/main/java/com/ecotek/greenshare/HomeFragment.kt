package com.ecotek.greenshare

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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {
    val max = 10 // Nombre d'ViewImage et ViewText à ajouter

    var image = mutableListOf<String>("a", "ameen", "r", "a", "ameen", "r","a", "ameen", "r", "a", "ameen", "r")
    var texte = mutableListOf<String>("a", "ameen", "r", "a", "ameen", "r","a", "ameen", "r", "a", "ameen", "r")
    var description = mutableListOf<String>("orem ipsum dolor sit amet, consectetur adipiscing elit. Proin sagittis, velit ut sollicitudin facilisis, diam justo elementum mi, ut consectetur neque leo in quam. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nullam consectetur commodo nisi vel semper. Etiam dapibus erat odio, et placerat elit maximus at. Pellentesque finibus erat a mi tempus, accumsan lobortis orci ultrices. Aenean dolor sem, varius sit amet aliquam id, pellentesque eget massa. Mauris accumsan mi et volutpat pretium. Aliquam dictum mattis massa vel bibendum. Mauris tempus augue eget est rhoncus, ac maximus nunc fermentum.", "Etiam nec neque turpis. Donec nec aliquam sapien. Nulla vehicula rutrum neque, in blandit velit placerat vitae. Suspendisse volutpat blandit sem, at consequat quam imperdiet sit amet. Curabitur maximus, urna vel fringilla volutpat, lectus mauris tincidunt dui, quis rhoncus diam ipsum in erat. Sed ex orci, tempor sit amet fermentum eu, blandit eget nisi. Nunc elit magna, tristique rhoncus egestas quis, lacinia non lacus. Sed dapibus risus a mauris varius sodales. Integer sed faucibus ipsum.", "Morbi aliquet mollis velit, a venenatis nunc semper at. Fusce et faucibus ipsum, a placerat lacus. Nulla placerat mattis risus in consectetur. Cras sit amet nibh ut lorem ullamcorper porttitor sed sed turpis. Cras mi dolor, tincidunt eu vestibulum id, condimentum et tellus. Fusce enim nisi, rutrum ut diam vel, elementum tristique massa. Aenean sodales efficitur risus, sit amet gravida nisi iaculis et. Sed maximus mauris a felis ultricies accumsan. Pellentesque vel turpis laoreet, gravida est in, porttitor massa. Phasellus ut urna sit amet tellus tristique eleifend non quis velit.", "Morbi aliquet mollis velit, a venenatis nunc semper at. Fusce et faucibus ipsum, a placerat lacus. Nulla placerat mattis risus in consectetur. Cras sit amet nibh ut lorem ullamcorper porttitor sed sed turpis. Cras mi dolor, tincidunt eu vestibulum id, condimentum et tellus. Fusce enim nisi, rutrum ut diam vel, elementum tristique massa. Aenean sodales efficitur risus, sit amet gravida nisi iaculis et. Sed maximus mauris a felis ultricies accumsan. Pellentesque vel turpis laoreet, gravida est in, porttitor massa. Phasellus ut urna sit amet tellus tristique eleifend non quis velit.", "Morbi aliquet mollis velit, a venenatis nunc semper at. Fusce et faucibus ipsum, a placerat lacus. Nulla placerat mattis risus in consectetur. Cras sit amet nibh ut lorem ullamcorper porttitor sed sed turpis. Cras mi dolor, tincidunt eu vestibulum id, condimentum et tellus. Fusce enim nisi, rutrum ut diam vel, elementum tristique massa. Aenean sodales efficitur risus, sit amet gravida nisi iaculis et. Sed maximus mauris a felis ultricies accumsan. Pellentesque vel turpis laoreet, gravida est in, porttitor massa. Phasellus ut urna sit amet tellus tristique eleifend non quis velit.", "Morbi aliquet mollis velit, a venenatis nunc semper at. Fusce et faucibus ipsum, a placerat lacus. Nulla placerat mattis risus in consectetur. Cras sit amet nibh ut lorem ullamcorper porttitor sed sed turpis. Cras mi dolor, tincidunt eu vestibulum id, condimentum et tellus. Fusce enim nisi, rutrum ut diam vel, elementum tristique massa. Aenean sodales efficitur risus, sit amet gravida nisi iaculis et. Sed maximus mauris a felis ultricies accumsan. Pellentesque vel turpis laoreet, gravida est in, porttitor massa. Phasellus ut urna sit amet tellus tristique eleifend non quis velit.")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        createPost(view)
        val scrollView: ScrollView = view.findViewById(R.id.scroll)
        detectEndOfScroll(scrollView)
        refreshScroll(scrollView)

        return view
    }

    private fun createPost(view: View) {
        if (!isAdded) {
            return  // Vérifie si le fragment est attaché avant d'accéder au contexte
        }

        val linearContainer: LinearLayout = view.findViewById(R.id.fil)



        val collection = FirebaseFirestore.getInstance().collection("Article")
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
            }
            .addOnFailureListener { exception ->
                //
            }

        for (index in 0 until max) {
            val inflater = LayoutInflater.from(requireContext())
            val postView = inflater.inflate(R.layout.post, null)
            val cardView: CardView = postView.findViewById(R.id.touchCard)

            linearContainer.addView(postView)
            val args = Bundle()
            //val cardView: CardView = view.findViewById(R.id.touchCard)
            //cardView.setOnClickListener {}

            //Rajouter une margin TOP de 40dp pour le tout premier post
            if (index == 0) {
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
            if (image.getOrNull(index) != null) {

                val imageView: ImageView = postView.findViewById(R.id.imageView)
                val imageName = image[index]
                val imageId =
                    resources.getIdentifier(imageName, "drawable", requireActivity().packageName)
                imageView.setImageResource(imageId)
            }
            if (texte.getOrNull(index) != null) {
                val textView: TextView = postView.findViewById(R.id.textView)
                Article.getArticle(index.toString()) { article ->
                    if (article != null) {
                        textView.text = article.title
                    }

                }
            }


            cardView.setOnClickListener {
                val textView: TextView = postView.findViewById(R.id.textView)
                var textValue = texte[index]
                textView.text = textValue
                val imageView: ImageView = postView.findViewById(R.id.imageView)
                val imageName = image[index]
                val imageId = resources.getIdentifier(
                    imageName,
                    "drawable",
                    requireActivity().packageName
                )
                imageView.setImageResource(imageId)

                var descript: String? = null
                if (description.getOrNull(index) != null) {
                    descript = description[index]

                }

                args.putString("index",index.toString())
                args.putString("keyd", descript)
                args.putString("keyi", imageName)
                args.putString("key", textValue)
                val readFragment = ReadFragment()
                readFragment.arguments = args
                handleClick(readFragment, args)
            }


        }
    }




    private fun detectEndOfScroll(scrollView: ScrollView) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))
            if (diff == 0) {
                createPost(view)
            }
        }
    }

    private fun refreshScroll(scrollView: ScrollView) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.scrollY == 0) {
                println("coucou")
                createPost(scrollView.getChildAt(0))
            }
        }
    }

    fun handleClick(fragment: Fragment, arguments: Bundle) {
        fragment.arguments = arguments
        (activity as HomeActivity).moveToFragment(fragment)

    }



}
