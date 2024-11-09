package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Register3 : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonReg: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textview: TextView
    private lateinit var auth: FirebaseAuth


    val db = Firebase.firestore
    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Register activity started")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        // Initialize UI components
        editTextEmail = findViewById(R.id.email_input)
        editTextPassword = findViewById(R.id.password_input)
        buttonReg = findViewById(R.id.login_btn)
        progressBar = findViewById(R.id.progressBar)

        buttonReg.setOnClickListener {
            //
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                       // updateUI(null)
                    }
                }// Store additional user data in Firestore
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun registerUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        // Add more password complexity rules here (e.g., length, special characters)
        // Input Validation (More robust)
        if (email.isEmpty()) {
            editTextEmail.error = "Email is required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Invalid email format"
            return
        }
        if (password.isEmpty()) {
            editTextPassword.error = "Password is required"
            return
        }
        progressBar.visibility = View.VISIBLE

        val userData = hashMapOf(
            "email" to email,
            "password" to password)
        // Add other fields like name, surname, etc. here

        db.collection("user")
            .add(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data added to Firestore successfully")
                Toast.makeText(
                    baseContext,
                    "Registration Successful",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(applicationContext, HomePage::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user data to Firestore", e)
            }

    }
}
