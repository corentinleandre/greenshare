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

/**
 * An activity that handles user registration.
 */
open class RegisterActivity : LoginActivity() {
    /**
     * Adds users to the system.
     */
    fun addusers(){
        val email:String = "marie.koza@uha.fr"
        val password:String = "telephone"
        // Create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
        // Sign out user
        FirebaseAuth.getInstance().signOut()


    }
}