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
import java.util.regex.Pattern
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString


var GeneralId=1
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