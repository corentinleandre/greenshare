package com.ecotek.greenshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


open class LoginActivity : AppCompatActivity() {

    var name:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.login_activity)
        //FireStore().getJsonDataFromAsset(this, "prof_data.json")
        //FireStore().getJsonDataFromAsset(this, "student_data.json")
        //Article.getArticle("1"){article ->
        //    if (article != null){
        //        Toast.makeText(this@LoginActivity, article.content, Toast.LENGTH_LONG).show()
        //    }
        //}

        var loginbtn = findViewById<Button>(R.id.loginbtn)
        var mail = findViewById<EditText>(R.id.email)
        var pass = findViewById<EditText>(R.id.password)

        loginbtn.setOnClickListener{
            loginuser()
        }

        var forgotpassbtn = findViewById<Button>(R.id.forgot)

        forgotpassbtn.setOnClickListener{
            startActivity(Intent(this@LoginActivity,RecoveryActivity::class.java))
            finish()
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