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
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class AddPostFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val addPostTitleTextView = view.findViewById<TextView>(R.id.addPostTitle)
        val titleAreaTextInput = view.findViewById<TextInputEditText>(R.id.titleArea)
        val contentAreaTextInput = view.findViewById<TextInputEditText>(R.id.contentArea)
        val postButton = view.findViewById<Button>(R.id.postbtn)

        imageButton = view.findViewById<ImageButton>(R.id.imgbtn)
        imageView = view.findViewById<ImageView>(R.id.imageView4)

        imageButton.setOnClickListener {
            checkPermissionAndOpenGallery(imageView,imageButton)
        }

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
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val dateString = dateFormat.format(currentDate)
                        Article.postArticle(titleAreaTextInput.text.toString(),userId.toString(),contentAreaTextInput.text.toString(),dateString,"")
                        (activity as HomeActivity).moveToFragment(HomeFragment())

                    } else { }
                }
                .addOnFailureListener { exception ->
                }

        }
    }

    private val PERMISSION_REQUEST_CODE = 2

    //private lateinit var imageButton: ImageButton
    //private lateinit var imageView: ImageView

    private fun checkPermissionAndOpenGallery(imageView:ImageButton,imageButton:ImageView) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            openGallery(imageView)
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                // Handle the case where the permission is denied
            }
        }
    }
    private fun openGallery(imageView:ImageView) {
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = result.data
                val pickedPhoto: Uri? = data?.data
                if (pickedPhoto != null) {
                    imageView.setImageURI(pickedPhoto)
                }
            }
        }

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    companion object {

    }
}
