package com.ecotek.greenshare

import android.media.MediaPlayer.OnCompletionListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException


var GeneralId=1
open class RegisterActivity : LoginActivity() {
    fun addusers(){
        val email:String = "marie.koza@uha.fr"
        val password:String = "telephone"

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
        FirebaseAuth.getInstance().signOut()


    }

    data class User(val email: String, val password: String)

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


}