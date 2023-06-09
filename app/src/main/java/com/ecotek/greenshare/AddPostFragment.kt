package com.ecotek.greenshare

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
val listBlocked : List<String> = listOf("fuck","f*ck","con","abruti","batard","putain","connard","connasse","p*tain","c*n","c*nne","merde","nique","tg","fdp")
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

                        fun verificationPost(message : String):Boolean{
                            var allowed=true
                            var list = message.split(" ",",",".",":",";")
                            var blockedWord=""
                            for(element in list){
                                for (f in listBlocked){
                                    if (element.toLowerCase()==f) {
                                        println("MOT INTERDIT !")
                                        blockedWord=element
                                        allowed=false
                                        break //if the world isn't allowed, stop the verification
                                    }
                                }
                                if (!allowed) {
                                    println("post bloqué")
                                    blockedPost(blockedWord) //requests to user to delete the blockedWord
                                    break
                                }
                            }
                            return allowed
                        }

                        val titleVerif = verificationPost(titleAreaTextInput.text.toString())
                        val contentVerif=verificationPost(contentAreaTextInput.text.toString())
                        if (titleVerif && contentVerif) {
                            Article.postArticle(titleAreaTextInput.text.toString(),userId.toString(),contentAreaTextInput.text.toString(),dateString,"")
                            (activity as HomeActivity).moveToFragment(HomeFragment())
                        }



                    } else { }
                }
                .addOnFailureListener { exception ->
                }

        }
    }

    fun blockedPost(mot:String){
        val alertDialog: AlertDialog? =AlertDialog.Builder(requireActivity()).create()
        if (alertDialog != null) {
            alertDialog.setTitle("Attention !")
            alertDialog.setMessage("Veuillez supprimer le mot '$mot' de votre post, ce mot n'est pas autorisé. Merci d'adopter un comportement adapté et respectueux !")
            alertDialog.show()
        }
    }

    companion object {

    }
}