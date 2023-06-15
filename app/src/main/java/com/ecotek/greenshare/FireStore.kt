package com.ecotek.greenshare

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException



var GeneralId=1

/**
 * This Class representes the Firestore operations management.
 */
class FireStore {

    private val mFirestore = FirebaseFirestore.getInstance()
    /**
     * Sends user data to Firestore.
     *
     * @param userInfo The user information to send.
     */
    fun senddata(userInfo: User){
        mFirestore.collection("Users")
            .document(userInfo.identification.toString())
            .set(userInfo, SetOptions.merge())
        //.addOnSuccessListener { }
    }
    /**
     * Retrieves JSON data from a file in the application's resources.
     *
     * @param context  The application context.
     * @param fileName The name of the JSON file.
     * @return A list of users if reading is successful, otherwise null.
     */
    fun getJsonDataFromAsset(context: Context, fileName: String): List<User>? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val userList = mutableListOf<User>()

            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)
                val email = jsonObject.getString("email")
                val password = jsonObject.getString("password")
                val numero= jsonObject.getInt("numero").toString()
                val role = jsonObject.getBoolean("role").toString()
                val group = jsonObject.getString("group")    // list de String
                val icon = jsonObject.getString("icon")       //list de Integer
                val rights = jsonObject.getInt("rights").toString()

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                FirebaseAuth.getInstance().signOut()
                //val pattern = Pattern.compile("^[a-z-]+\\.+[a-z-]+@uha.fr") // verification email
                var list = email.split(".","@")
                var firstname=list[0]
                var lastname=list[1]

                val identification=GeneralId.toString()
                GeneralId=GeneralId+1


                val user = User(identification, firstname, lastname,numero,email,role,group,icon,rights)
                senddata(user)
                userList.add(user)
            }

            return userList
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
            return null
        }
    }
    /**
     * The function "getname" retrieves the user's name based on their identification.
     *
     * @param activity       The relevant activity.
     * @param identification The user's identification.
     */
    fun getname(activity: FirstFragment,identification : String){

        mFirestore.collection("Users")
            .document(identification)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    // TODO: Perform operations with the user's name

                }
            }
    }
}