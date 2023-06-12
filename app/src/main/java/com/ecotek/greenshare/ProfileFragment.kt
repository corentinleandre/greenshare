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
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import android.os.Environment
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap.Config.ARGB_8888
import java.io.IOException


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
    var initials: String =""

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

        //creates icon User
        fun createCustomUserIcon(context: Context, initials: String): Drawable {
            val size = 512 // Taille de l'icône (en pixels)

            val bitmap = Bitmap.createBitmap(size, size, ARGB_8888)
            val canvas = Canvas(bitmap)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.BLACK
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

            val textSize = size / initials.length.toFloat()
            paint.color = Color.WHITE
            paint.textSize = textSize
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.CENTER

            val textBounds = Rect()
            paint.getTextBounds(initials, 0, initials.length, textBounds)
            val x = canvas.width / 2
            val y = (canvas.height / 2) - (textBounds.top + textBounds.bottom) / 2

            canvas.drawText(initials, x.toFloat(), y.toFloat(), paint)

            return BitmapDrawable(context.resources, bitmap)
        }

        mFirestore.collection("Users")
            .whereEqualTo("email", currentUser)
            .get()
            .addOnSuccessListener { document ->
                val context = requireContext()
                val userDocument = document.documents[0]
                val phoneNumber = userDocument.getString("telephone")
                val roleUser = userDocument.getString("role")
                val groupUser = userDocument.getString("group")

                var list = userDocument.getString("email")?.split(".", "@")
                var firstname = list?.get(0)?.capitalize()
                var lastname = list?.get(1)?.capitalize()
                initials=list?.get(0)?.capitalize()?.substring(0, 1) + list?.get(1)?.capitalize()?.substring(0,1)
                val userIcon = createCustomUserIcon(context,initials)

                view.findViewById<ImageView>(R.id.userImageView).setImageDrawable(userIcon)
                view.findViewById<TextView>(R.id.user_name).text = "$firstname $lastname"
                view.findViewById<TextView>(R.id.user_role).text = roleUser
                view.findViewById<TextView>(R.id.user_group).text = groupUser
                view.findViewById<TextView>(R.id.user_numero).text = phoneNumber
                view.findViewById<TextView>(R.id.user_email).text = currentUser
                view.findViewById<TextView>(R.id.user_bureau).text = "Plus tard" //TODO : add to data base "bureau"
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