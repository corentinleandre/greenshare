package com.ecotek.greenshare

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.fragment.app.Fragment


class ReadFragment : Fragment() {
    val im = 10 // Nombre d'ViewImage et ViewText à ajouter



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_read, container, false)
        createPost(view)
        return view
    }

    @SuppressLint("MissingInflatedId")
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
        val des=arguments?.getString("keyd")
        val titre=arguments?.getString("key")

        if (imageName != null) {
            val imageId =
                resources.getIdentifier(imageName, "drawable", requireActivity().packageName)
            imageView.setImageResource(imageId)
        }
        val textView: TextView = postView.findViewById(R.id.textView)
        textView.text = titre

        if (des!= null) {
        val description:TextView=postView.findViewById(R.id.description)
        description.text=des
        }


    }


}



