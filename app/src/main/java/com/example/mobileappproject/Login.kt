package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLog: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textview: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "LoginActivity"
        private const val ADMIN_EMAIL = "admin@gmail.com"
        private const val ADMIN_PASSWORD = "admin123"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        // Initialize UI elements
        editTextEmail = findViewById(R.id.email_input)
        editTextPassword = findViewById(R.id.password_input)
        buttonLog = findViewById(R.id.login_btn)
        progressBar = findViewById(R.id.progressBar)
        textview = findViewById(R.id.registernow)

        textview.setOnClickListener {
            startActivity(Intent(this, Register3::class.java))
            finish()
        }

        buttonLog.setOnClickListener {
            loginUser()
        }

    }


    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        // Input validation
        if (email.isEmpty()) {
            editTextEmail.error = "Email is required"
            return
        }
        if (password.isEmpty()) {
            editTextPassword.error = "Password is required"
            return
        }

        progressBar.visibility = View.VISIBLE

        // Check for admin credentials
        if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
            saveAdminSession(email, password)
            progressBar.visibility = View.GONE
            startActivity(Intent(this, Main_Admin::class.java))
            finish()
            return
        }

        // Sign in using Firebase Authentication for regular users
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    saveUserSession(user)
                    startActivity(Intent(this, HomePage::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun saveAdminSession(email: String, password: String) {
        val sharedPref = getSharedPreferences("adminSession", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("adminEmail", email)
            putString("adminPassword", password)
            apply()
        }
    }

    private fun saveUserSession(user: FirebaseUser?) {
        user?.let {
            val userId = user.uid
            val sharedPref = getSharedPreferences("userSession", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("userId", userId)
                apply()
            }
        }
    }

}
