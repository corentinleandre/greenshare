package com.ecotek.greenshare

import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class AddPostFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val addPostTitleTextView = view.findViewById<TextView>(R.id.addPostTitle)
        val titleAreaTextInput = view.findViewById<TextInputEditText>(R.id.titleArea)
        val contentAreaTextInput = view.findViewById<TextInputEditText>(R.id.contentArea)
        val postButton = view.findViewById<Button>(R.id.postbtn)

        postButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userEmail = currentUser?.email.toString()
            val usersCollection = FirebaseFirestore.getInstance().collection("Users")
            usersCollection
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val userId = querySnapshot.documents[0].getString("identification")
                        val currentDate = Date()
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateString = dateFormat.format(currentDate)
                        //Article.postArticle(titleAreaTextInput.text.toString(),userId.toString(),contentAreaTextInput.text.toString(),dateString,"")
                    } else { }
                }
                .addOnFailureListener { exception ->
                }

        }
    }

    companion object {

    }
}