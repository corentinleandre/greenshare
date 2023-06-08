package com.ecotek.greenshare

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class FireStore {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun senddata(userInfo: User){
        mFirestore.collection("Users")
            .document(userInfo.identification.toString())
            .set(userInfo, SetOptions.merge())
        //.addOnSuccessListener { }
    }
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
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                FirebaseAuth.getInstance().signOut()
                val user = User(email, password)
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

    fun getname(activity: FirstFragment,identification : String){

        mFirestore.collection("Users")
            .document(identification)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    //TODO
                }
            }
    }
}