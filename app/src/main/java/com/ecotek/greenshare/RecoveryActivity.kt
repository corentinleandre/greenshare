package com.ecotek.greenshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity for password recovery functionality.
 */
class RecoveryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rec_mdp)

        // Back to login button
        val backToLogin = findViewById<Button>(R.id.return_to_login)

        backToLogin.setOnClickListener{
            // Return to LoginActivity
            startActivity(Intent(this@RecoveryActivity, LoginActivity::class.java))
            finish()
        }

        var sendemail = findViewById<Button>(R.id.reset)

        sendemail.setOnClickListener{
                FirebaseAuth.getInstance().sendPasswordResetEmail(findViewById<EditText>(R.id.emailrecovery).text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Reset password email sent", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Insert a valid email", Toast.LENGTH_LONG).show()
                }
        }
    }
}
