package com.ecotek.greenshare

import android.media.MediaPlayer.OnCompletionListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

open class RegisterActivity : LoginActivity() {
    fun addusers(){
        val email:String = "marie.koza@uha.fr"
        val password:String = "telephone"

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
        FirebaseAuth.getInstance().signOut()
    }
}