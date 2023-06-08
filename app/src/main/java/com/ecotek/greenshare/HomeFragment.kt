package com.ecotek.greenshare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {
    val im = 10 // Nombre d'ViewImage et ViewText à ajouter

    var image = mutableListOf<String>("a", "ameen", "r", "a", "ameen", "r")
    var texte = mutableListOf<String>("a", "ameen", "r")

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
        for (index in 0 until 9) {
            val inflater = LayoutInflater.from(requireContext())
            val postView = inflater.inflate(R.layout.post, null)
            linearContainer.addView(postView)

            if (image.getOrNull(index) != null) {
                val imageView: ImageView = postView.findViewById(R.id.imageView)
                val imageName = image[index]
                val imageId = resources.getIdentifier(imageName, "drawable", requireActivity().packageName)
                imageView.setImageResource(imageId)
                imageView.setOnClickListener {
                    handleImageClick()
                }
            }
            if (texte.getOrNull(index) != null) {
                val textView: TextView = postView.findViewById(R.id.textView)
                val textValue = texte[index]
                textView.text = textValue
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
                createPost(scrollView.getChildAt(0))
            }
        }
    }
    private fun handleImageClick() {
        println("Post")
        (activity as HomeActivity).moveToFragment(NewContentFragment())

    }


}