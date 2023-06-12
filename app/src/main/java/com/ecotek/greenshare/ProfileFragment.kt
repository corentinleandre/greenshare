package com.ecotek.greenshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        val logoutButton: ImageButton = view.findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent((activity as HomeActivity),LoginActivity::class.java))
            this.activity?.finish()
        }
        //add the user's personal data
        val currentUser = FirebaseAuth.getInstance().currentUser?.email
        val mFirestore = FirebaseFirestore.getInstance()

        mFirestore.collection("Users")
            .whereEqualTo("email", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val userDocument = document.documents[0]
                val phoneNumber = userDocument.getString("telephone")
                val roleUser = userDocument.getString("role")
                val groupUser = userDocument.getString("group")
                var list = userDocument.getString("email")?.split(".","@")
                var firstname= list?.get(0)?.capitalize()
                var lastname= list?.get(1)?.capitalize()
                view.findViewById<TextView>(R.id.user_name).text = "$firstname $lastname"
                view.findViewById<TextView>(R.id.user_role).text= roleUser
                view.findViewById<TextView>(R.id.user_group).text= groupUser
                view.findViewById<TextView>(R.id.user_numero).text= phoneNumber
                view.findViewById<TextView>(R.id.user_email).text= currentUser
                view.findViewById<TextView>(R.id.user_bureau).text= "Plus tard" //TODO : add to data base "bureau"

            }

        return view
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