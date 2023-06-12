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
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.ArrayList
import java.util.Collections


//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
val listBlocked : List<String> = listOf("fuck","f*ck","con","abruti","batard","putain","connard","connasse","p*tain","c*n","c*nne","merde","nique","tg","fdp")
class AddPostFragment : Fragment() {

    private val MAX_IMAGE_SIZE = 1024 // Maximum image size in kilobytes (1MB)
    private lateinit var audience: TextView
    private lateinit var selectedAudience: BooleanArray
    private val audienceList = ArrayList<String>(listOf("uha","professeurs","etudiants"))
    private val selectedOptions = ArrayList<String>()

    private lateinit var titleAreaTextInput: EditText
    private lateinit var contentAreaTextInput: EditText
    private lateinit var postButton: Button
    private lateinit var imageButton: ImageButton
    private lateinit var videoButton: ImageButton
    private lateinit var imageView:ImageView
    private lateinit var mediaLayout: LinearLayout
    private lateinit var videoView: VideoView

    //private lateinit var selectedMediaUris: List<Uri> = emptyList()
    private var selectedMediaUris: List<Uri> = listOf()
    // Initialize as null
    private val selectedImageUris: MutableList<Uri> = mutableListOf()

    private fun updateSelectedLanguageArray() {
        selectedAudience = BooleanArray(audienceList.size)
    }

    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val mediaUris: List<Uri>? = data?.clipData?.let { clipData ->
                (0 until clipData.itemCount).map { index ->
                    clipData.getItemAt(index).uri
                }
            } ?: listOfNotNull(data?.data)

            if (mediaUris != null) {
                selectedMediaUris = mediaUris
                displaySelectedMedia()
            }
        }
    }

    private val mstorageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val mfirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        titleAreaTextInput = view.findViewById<TextInputEditText>(R.id.titleArea)
        contentAreaTextInput = view.findViewById<TextInputEditText>(R.id.contentArea)
        postButton = view.findViewById<Button>(R.id.postbtn)
        imageButton = view.findViewById(R.id.add_pic)
        videoButton = view.findViewById(R.id.add_vid)
        imageView = view.findViewById<ImageView>(R.id.imageView2)
        mediaLayout = view.findViewById(R.id.mediaLayout)
        //videoView = view.findViewById<VideoView>(R.id.videoView2)

        audience = view.findViewById(R.id.Audience)

        audience.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Select Audience")
            builder.setCancelable(false)

            builder.setMultiChoiceItems(
                audienceList.toTypedArray(),
                selectedAudience
            ) { dialogInterface: DialogInterface, i: Int, b: Boolean ->
                if (b) {
                    selectedOptions.add(audienceList[i])
                } else {
                    selectedOptions.remove(audienceList[i])
                }
            }

            builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
                val stringBuilder = StringBuilder()
                for (j in 0 until selectedOptions.size) {
                    stringBuilder.append(selectedOptions[j])
                    if (j != selectedOptions.size - 1) {
                        stringBuilder.append(", ")
                    }
                }
                audience.text = stringBuilder.toString()
            }

            builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }

            builder.setNeutralButton("Clear All") { _: DialogInterface, _: Int ->
                selectedAudience.fill(false)
                selectedOptions.clear()
                audience.text = ""
            }

            builder.setNeutralButton("Add New") { _: DialogInterface, _: Int ->
                val inflater = LayoutInflater.from(requireContext())
                val view = inflater.inflate(R.layout.audience_options, null)
                val editText = view.findViewById<EditText>(R.id.editText)

                val addLanguageDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Add New Language")
                    .setView(view)
                    .setPositiveButton("Add") { dialogInterface: DialogInterface, _: Int ->
                        val newLanguage = editText.text.toString()
                        if (newLanguage.isNotEmpty()) {
                            audienceList.add(newLanguage)
                            Collections.sort(audienceList)
                            updateSelectedLanguageArray()
                            selectedOptions.add(newLanguage)
                            audience.text = selectedOptions.joinToString(", ")
                        }
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    .create()

                addLanguageDialog.show()
            }

            builder.show()
        }


        updateSelectedLanguageArray()
        displaySelectedMedia()
        imageButton.setOnClickListener{

            checkPermissionAndOpenMediaPicker()
        }

        videoButton.setOnClickListener{

            checkPermissionAndOpenMediaPicker2()
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

                        fun verificationPost(message : String):Boolean{
                            var allowed=true
                            var list = message.split(" ",",",".",":",";")
                            var blockedWord=""
                            for(element in list){
                                for (f in listBlocked){
                                    if (element.toLowerCase()==f) {
                                        blockedWord=element
                                        allowed=false
                                        break //if the world isn't allowed, stop the verification
                                    }
                                }
                                if (!allowed) {
                                    blockedPost(blockedWord)
                                    break
                                }
                            }
                            return allowed
                        }

                        val titleVerif = verificationPost(titleAreaTextInput.text.toString())
                        val contentVerif=verificationPost(contentAreaTextInput.text.toString())
                        if (titleVerif && contentVerif) {
                            postArticle(titleAreaTextInput.text.toString(),userId.toString(),contentAreaTextInput.text.toString(),dateString,selectedOptions.joinToString(","),"")
                            (activity as HomeActivity).moveToFragment(HomeFragment())
                        }
                    } else { }
                }
                .addOnFailureListener { exception ->
                }

        }
        return view
    }
    private fun displaySelectedMedia() {
        mediaLayout.removeAllViews()

        selectedMediaUris.forEach { mediaUri ->
            val contentResolver = requireContext().contentResolver
            val mimeType = contentResolver.getType(mediaUri)
            if (mimeType?.startsWith("image") == true) {
                mediaLayout.addView(createImageView(requireContext(), mediaUri))

            } else if(mimeType?.startsWith("video") == true) {
                mediaLayout.addView(createVideoView(requireContext(), mediaUri))

            }else {
                mediaLayout.addView(createVideoView(requireContext(), mediaUri))
            }
            //mediaLayout.addView(mediaView)
        }
    }


    private fun isImageUri(uri: Uri): Boolean {
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri)
        return mimeType?.startsWith("image") == true || mimeType?.startsWith("video") == true
    }


    private fun createImageView(context: Context, imageUri: Uri): ImageView {
        val compressedBitmap = compressImage(imageUri)
        val imageView = ImageView(context)
        imageView.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        imageView.setPadding(0, 0, 0, 0)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageView.setImageBitmap(compressedBitmap)

        return imageView
    }

    private fun createVideoView(context: Context, videoUri: Uri): VideoView {
        val videoView = VideoView(context)
        videoView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(0f, 0f)
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }

        return videoView!!
    }

    private fun checkPermissionAndOpenMediaPicker() {
        /*if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestMediaPermission()
        } else {
            openMediaPicker()
        }*/
        openMediaPicker("image/*")
    }

    private fun checkPermissionAndOpenMediaPicker2() {
        /*if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestMediaPermission()
        } else {
            openMediaPicker()
        }*/
        openMediaPicker("video/*")
    }

    private fun requestMediaPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            AddPostFragment.PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AddPostFragment.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMediaPicker("image/*")
            } else {
                // Handle the case where the permission is denied
            }
        }
    }

    private fun openMediaPicker(type:String) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = type //"image/* video/*"  // Allow selecting both images and videos
        pickMediaLauncher.launch(intent)
    }


    fun uploadMediaFilesToFirebase(id: String) {
        // Check if there are any selected media URIs
        if (selectedMediaUris != null && selectedMediaUris!!.isNotEmpty()) {
            val data = hashMapOf<String, Any>("id" to id)

            selectedMediaUris!!.forEachIndexed { index, mediaUri ->
                val number = index + 1
                val fileName = "${id}/image$number"
                val mediaReference = mstorageReference.child(fileName)
                val uploadTask = mediaReference.putFile(mediaUri)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Media upload success, get the download URL
                    mediaReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        data["media$number"] = downloadUri.toString()

                        if (number == selectedMediaUris!!.size) {
                            mfirestore.collection("Medias")
                                .document(id)
                                .set(data)
                                .addOnSuccessListener {
                                    // Data saved successfully in Firestore
                                }
                                .addOnFailureListener { exception ->
                                    // Handle any errors that occur while saving data to Firestore
                                }
                        }
                    }
                }.addOnFailureListener { exception ->
                    // Handle any errors that occur during media upload
                }
            }
        } else {
            // Handle the case where no media is selected
        }
    }

    private fun compressImage(imageUri: Uri): Bitmap? {
        val imageStream = requireContext().contentResolver.openInputStream(imageUri)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream?.close()

        var bitmap: Bitmap? = null

        // Calculate the sample size to resize the image while maintaining aspect ratio
        val scaleFactor = calculateSampleSize(options)

        // Reopen the input stream and decode the bitmap with the calculated sample size
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor
        bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        return bitmap
    }

    private fun calculateSampleSize(options: BitmapFactory.Options): Int {
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth
        var scaleFactor = 1

        if (imageHeight > MAX_IMAGE_SIZE || imageWidth > MAX_IMAGE_SIZE) {
            val heightRatio = Math.round(imageHeight.toFloat() / MAX_IMAGE_SIZE.toFloat())
            val widthRatio = Math.round(imageWidth.toFloat() / MAX_IMAGE_SIZE.toFloat())
            scaleFactor = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        return scaleFactor
    }



    //private fun saveDataToFirestore(mediaUrl: String) {} //moved into uploadmedia function
    //requests to user to delete the blockedWord. It is like a prevention
    fun blockedPost(mot:String){
        val alertDialog: AlertDialog? =AlertDialog.Builder(requireActivity()).create()
        if (alertDialog != null) {
            alertDialog.setTitle("Attention !")
            alertDialog.setMessage("Veuillez supprimer le mot '$mot' de votre post, ce mot n'est pas autorisé. Merci d'adopter un comportement adapté et respectueux !")
            alertDialog.show()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 2
    }

    fun postArticle(title: String = "",
                    authorID: String = "",
                    content: String = "",
                    date: String = "",
                    group:String = "",
                    commentID: String = ""){
        val mFirestore = FirebaseFirestore.getInstance()
        val collection = mFirestore.collection("Article")
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
                highestId += 1
                var imageID : String
                if (selectedMediaUris == null){imageID= ""} else{imageID=highestId.toString()}
                val article = Article(highestId.toString(),title,authorID,content,date,group,"",imageID,commentID,"no")
                uploadMediaFilesToFirebase(highestId.toString())
                mFirestore.collection("Article")
                    .document(article.id.toString())
                    .set(article, SetOptions.merge())
                //.addOnSuccessListener { }
            }
            .addOnFailureListener { exception ->
                //
            }
    }
}