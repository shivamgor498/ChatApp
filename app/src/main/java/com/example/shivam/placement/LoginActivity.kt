package com.example.shivam.placement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button.setOnClickListener {
            val email = email_login.text.toString()
            val password = password_login.text.toString()
            Log.d("LoginActivity", "username: $email")
            Log.d("LoginActivity", "password: $password")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener {
                Toast.makeText(this,"Successfully logged in",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,Dashboard::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this,"Failed to sign in: ${it.message}",Toast.LENGTH_SHORT).show()
            }
        }
        not_registered.setOnClickListener {
            Log.d("LoginActivity", "User wants to register")
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}