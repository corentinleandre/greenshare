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
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream


//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
val listBlocked : List<String> = listOf("fuck","f*ck","con","abruti","batard","putain","connard","connasse","p*tain","c*n","c*nne","merde","nique","tg","fdp")
class AddPostFragment : Fragment() {


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
                            postArticle(titleAreaTextInput.text.toString(),userId.toString(),contentAreaTextInput.text.toString(),dateString,"")
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
        val imageView = ImageView(context)
        imageView.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        imageView.setPadding(0, 0, 0, 0) // Optional: Remove padding if present
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        imageView.setImageURI(imageUri)

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


    fun uploadMediaFilesToFirebase(id: String,context: Context) {
        // Check if there are any selected media URIs
        if (selectedMediaUris.isNotEmpty()) {
            val data = hashMapOf<String, Any>("id" to id)

            selectedMediaUris.forEachIndexed { index, mediaUri ->
                val number = index + 1
                val fileName = "${id}/image$number"
                val mediaReference = mstorageReference.child(fileName)
                val selectedImageUri : Uri = mediaUri
                val imageStream = context.contentResolver.openInputStream(selectedImageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val lightImage = lightImage(selectedImage)
                val imageArray = ByteArrayOutputStream()
                lightImage.compress(Bitmap.CompressFormat.JPEG, 75, imageArray)
                val datas = imageArray.toByteArray()
                val uploadTask = mediaReference.putBytes(datas)

                //val uploadTask = mediaReference.putFile(mediaUri)


                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Media upload success, get the download URL
                    mediaReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        data["media$number"] = downloadUri.toString()

                        if (number == selectedMediaUris.size) {
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
                }.addOnFailureListener {
                    // Handle any errors that occur during media upload
                }
            }
        } else {
            // Handle the case where no media is selected
        }
    }

    private fun lightImage(image : Bitmap) : Bitmap {
        val width = image.width
        val height = image.height
        if(width <= 1280 && height <= 720) {
            return image
        }
        val ratio : Float
        if(width > height) {
            ratio = 1280.toFloat() / width
        }
        else {
            ratio = 720.toFloat() / height
        }
        val sizedWidth : Int = (width * ratio).toInt()
        val sizedHeight : Int = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(image, sizedWidth, sizedHeight, true)
    }

    //private fun saveDataToFirestore(mediaUrl: String) {} //moved into uploadmedia function

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
                val article = Article(highestId.toString(),title,authorID,content,date,imageID,commentID)
                uploadMediaFilesToFirebase(highestId.toString(), require())
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