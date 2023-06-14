package com.ecotek.greenshare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SettingsFragment(email:String) : Fragment() {

    private lateinit var bureauEditText: EditText
    private lateinit var groupEditText: EditText
    private lateinit var telephoneEditText: EditText
    private lateinit var saveButton: Button
    var currentUser=email

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        checkUser(view)
        return view
    }

    fun checkUser(view : View){
        bureauEditText = view.findViewById(R.id.bureauEditText)
        telephoneEditText = view.findViewById(R.id.telephoneEditText)
        saveButton = view.findViewById(R.id.saveButton)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        saveButton.setOnClickListener {
            val bureau = bureauEditText.text.toString()
            val telephone = telephoneEditText.text.toString()

            //envoyer les données !!
            val pattern = Pattern.compile("^\\d{10}$")
            val match=pattern.matcher(telephone)
            if((telephone.isEmpty() || match.matches()) && bureau.isNotEmpty()){
                val mFirestore = FirebaseFirestore.getInstance()
                val collection = mFirestore.collection("Users")
                collection
                    .get()
                    .addOnSuccessListener { querySnapshot->
                        for (document in querySnapshot) {
                            if(document.getString("email")==currentUser){
                                collection.document(document.id).update("telephone",telephone)
                                collection.document(document.id).update("bureau",bureau)
                                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

            }
            else {
                Toast.makeText(requireContext(), "nope", Toast.LENGTH_SHORT).show()
            }


        }

        backButton.setOnClickListener {
            val profileFragment = ProfileFragment(FirebaseAuth.getInstance().currentUser?.email.toString())
            (activity as? HomeActivity)?.moveToFragment(profileFragment)
        }

        /*fun checkAdmin(view:View){
            val mFirestore = FirebaseFirestore.getInstance()
            mFirestore.collection("Users")
                .whereEqualTo("email", currentUser)
                .get()
                .addOnSuccessListener { document ->
                    val userDocument = document.documents[0]
                    userRights = userDocument.getString("telephone").toString()
                }
            if (userRights=="2"){
                val settingsView = inflater.inflate(R.layout.fragment_settings, null)
                val settingsAdmin=settingsView.findViewById<ViewGroup>(R.id.settingsAdmin)
                settingsAdmin.visibility=View.VISIBLE
            }
        }*/
    }
}
