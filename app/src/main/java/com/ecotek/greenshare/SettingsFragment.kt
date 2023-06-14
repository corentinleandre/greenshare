package com.ecotek.greenshare

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.common.io.LineReader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SettingsFragment(email:String) : Fragment() {

    private lateinit var bureauEditText: EditText
    private lateinit var telephoneEditText: EditText
    private lateinit var addModo: EditText
    private lateinit var delModo: EditText
    private lateinit var addAdmin: EditText
    private lateinit var delAdmin: EditText
    private lateinit var saveButton: Button
    private lateinit var delTelButton: Button
    var currentUser=email
    var userRights=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        checkUser(view,inflater)
        return view
    }

    fun checkUser(view : View,inflater: LayoutInflater){
        bureauEditText = view.findViewById(R.id.bureauEditText)
        telephoneEditText = view.findViewById(R.id.telephoneEditText)
        addModo = view.findViewById(R.id.moderatorAddText)
        delModo = view.findViewById(R.id.moderatorDelText)
        addAdmin = view.findViewById(R.id.adminAddText)
        delAdmin = view.findViewById(R.id.adminDelText)
        saveButton = view.findViewById(R.id.saveButton)
        val delTelButton= view.findViewById<ImageButton>(R.id.delTelephoneButton)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        val mFirestore = FirebaseFirestore.getInstance()
        mFirestore.collection("Users")
            .whereEqualTo("email", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val userDocument = document.documents[0]
                userRights = userDocument.getString("rights").toString()
                if (userRights=="2"){
                    val settingsAdmin = view.findViewById<LinearLayout>(R.id.settingsAdmin)
                    settingsAdmin.visibility = View.VISIBLE
                }
            }

        saveButton.setOnClickListener {
            val bureau = bureauEditText.text.toString()
            val telephone = telephoneEditText.text.toString()
            val addModo = addModo.text.toString()
            val delModo = delModo.text.toString()
            val addAdmin = addAdmin.text.toString()
            val delAdmin = delAdmin.text.toString()

            val pattern = Pattern.compile("^(03|06|07)\\d{8}$")
            val match=pattern.matcher(telephone)
            if(match.matches() || bureau.isNotEmpty() || addModo.isNotEmpty() || delModo.isNotEmpty() || addAdmin.isNotEmpty() || delAdmin.isNotEmpty()){
                val mFirestore = FirebaseFirestore.getInstance()
                val collection = mFirestore.collection("Users")
                collection
                    .get()
                    .addOnSuccessListener { querySnapshot->
                        for (document in querySnapshot) {
                            if(document.getString("email")==currentUser){
                                if (telephone.isNotEmpty()){
                                    collection.document(document.id).update("telephone",telephone)
                                }
                                if (bureau.isNotEmpty()){
                                    collection.document(document.id).update("bureau",bureau)
                                }
                            }
                            if(document.getString("email")==addModo){
                                collection.document(document.id).update("rights","1")
                            }
                            if(document.getString("email")==delModo){
                                collection.document(document.id).update("rights","0")
                            }
                            if(document.getString("email")==addAdmin){
                                collection.document(document.id).update("rights","2")
                            }
                            if(document.getString("email")==delAdmin){
                                collection.document(document.id).update("rights","0")
                            }
                        }
                    }
                val profileFragment = ProfileFragment(FirebaseAuth.getInstance().currentUser?.email.toString())
                (activity as? HomeActivity)?.moveToFragment(profileFragment)
            }
            else {
                if (userRights=="2"){
                    val alertDialog: AlertDialog? = AlertDialog.Builder(requireActivity()).create()
                    if (alertDialog != null) {
                        alertDialog.setTitle("Attention !")
                        alertDialog.setMessage("\n Veuillez remplir correctement les champs :\n\n-10 chiffres pour le numéro de téléphone (non obligatoire)\n-Un endroit où vous travaillez (obligatoire) \n-Email d'un utilisateur dont vous voulez changer les droits")
                        alertDialog.show()
                    }
                }
                else{
                    val alertDialog: AlertDialog? = AlertDialog.Builder(requireActivity()).create()
                    if (alertDialog != null) {
                        alertDialog.setTitle("Attention !")
                        alertDialog.setMessage("\n Veuillez remplir correctement les champs :\n\n-10 chiffres pour le numéro de téléphone (non obligatoire)\n-Un endroit où vous travaillez (obligatoire)")
                        alertDialog.show()
                    }
                }
            }
        }

        backButton.setOnClickListener {
            val profileFragment = ProfileFragment(FirebaseAuth.getInstance().currentUser?.email.toString())
            (activity as? HomeActivity)?.moveToFragment(profileFragment)
        }

        delTelButton.setOnClickListener {
            val mFirestore = FirebaseFirestore.getInstance()
            val collection = mFirestore.collection("Users")
            collection
                .get()
                .addOnSuccessListener { querySnapshot->
                    for (document in querySnapshot) {
                        if(document.getString("email")==currentUser){
                            collection.document(document.id).update("telephone","")
                        }
                    }
                }
            val profileFragment = ProfileFragment(FirebaseAuth.getInstance().currentUser?.email.toString())
            (activity as? HomeActivity)?.moveToFragment(profileFragment)
        }
    }
}
