package com.ecotek.greenshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ecotek.greenshare.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.view.View


open class LoginActivity : AppCompatActivity() {

    var name:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.login_activity)
        //RegisterActivity().addusers()

        var loginbtn = findViewById<Button>(R.id.loginbtn)
        var mail = findViewById<EditText>(R.id.email)
        var pass = findViewById<EditText>(R.id.password)

        loginbtn.setOnClickListener{
            loginuser()
        }
    }

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

    private fun loginuser() {
        val email = findViewById<EditText>(R.id.email).text.toString().trim{it <= ' '}
        val password = findViewById<EditText>(R.id.password).text.toString().trim{it <= ' '}
        if (validateLogin(email,password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                        finish()
                        //Toast.makeText(this@LoginActivity, "bien", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}