package com.ecotek.greenshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

/**
 * This class represents the login screen of the application.
 */
open class LoginActivity : AppCompatActivity() {

    var name:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        if (FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
            finish()
        }
        // Login button click listener

        val loginbtn = findViewById<Button>(R.id.loginbtn)

        loginbtn.setOnClickListener{
            loginuser()
        }
        // Forgot password button click listener

        val forgotpassbtn = findViewById<Button>(R.id.forgot)

        forgotpassbtn.setOnClickListener{
            startActivity(Intent(this@LoginActivity,RecoveryActivity::class.java))
            finish()
        }

    }
    /**
     * Validates the login credentials.
     *
     * @param mail The user's email.
     * @param pass The user's password.
     * @return True if the credentials are valid, false otherwise.
     */
    private fun validateLogin(mail: String, pass: String):Boolean{
        return when{
            mail.isEmpty()->{
                false
            }
            pass.isEmpty()->{
                false
            }
            else ->{
                true
            }
        }
    }

    /**
     * Logs in the user with the provided credentials.
     */
    private fun loginuser() {
        val email = findViewById<EditText>(R.id.email).text.toString().trim{it <= ' '}
        val password = findViewById<EditText>(R.id.password).text.toString().trim{it <= ' '}
        if (validateLogin(email,password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                        finish()
                    }

                }
                .addOnFailureListener { e ->
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }  else {
                        Toast.makeText(this@LoginActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}