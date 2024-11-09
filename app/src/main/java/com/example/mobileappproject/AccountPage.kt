package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AccountPage : AppCompatActivity() {

    private lateinit var userIdTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var logoutButton: Button
    private lateinit var adminButton: Button
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val ADMIN_EMAIL = "admin@gmail.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page)


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        userIdTextView = findViewById(R.id.userId)
        passwordTextView = findViewById(R.id.password)
        logoutButton = findViewById(R.id.logoutButton)
        adminButton = findViewById(R.id.adminButton)

        val adminSharedPref = getSharedPreferences("adminSession", MODE_PRIVATE)
        val userSharedPref = getSharedPreferences("userSession", MODE_PRIVATE)

        val adminEmail = adminSharedPref.getString("adminEmail", null)
        val adminPassword = adminSharedPref.getString("adminPassword", null)

        setupBottomNavigation(adminEmail, adminPassword)
        if (adminEmail != null && adminPassword != null) {
            userIdTextView.setText(adminEmail)
            passwordTextView.setText(adminPassword)
            adminButton.visibility = View.VISIBLE
        } else {
            val userId = userSharedPref.getString("userId", null)
            if (userId != null) {
                userIdTextView.setText(userId)
                passwordTextView.setText("********") // Masked password
            }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            with(userSharedPref.edit()) {
                clear()
                apply()
            }
            with(adminSharedPref.edit()) {
                clear()
                apply()
            }
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        adminButton.setOnClickListener {
            val intent = Intent(this, Main_Admin::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation(adminEmail: String?, adminPassword: String?) {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonSettings = findViewById<ImageButton>(R.id.button_article)
        val buttonSearch = findViewById<ImageButton>(R.id.button_conference)
        val buttonProfile = findViewById<ImageButton>(R.id.button_account)
        val buttonBack = findViewById<ImageView>(R.id.back_arrow)
        if (adminEmail != null && adminPassword != null) {
            buttonHome.setOnClickListener {
                val intent = Intent(this, Main_Admin::class.java)
                startActivity(intent)
                finish()
            }

            buttonSettings.setOnClickListener {
                val intent = Intent(this, Admin_SearchArticle::class.java)
                startActivity(intent)
                finish()
            }

            buttonSearch.setOnClickListener {
                val intent = Intent(this, Admin_SearchConference::class.java)
                startActivity(intent)
                finish()
            }

            buttonProfile.setOnClickListener {
                val intent = Intent(this, AccountPage::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            buttonHome.setOnClickListener {
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
                finish()
            }

            buttonSettings.setOnClickListener {
                val intent = Intent(this, SearchArticle::class.java)
                startActivity(intent)
                finish()
            }

            buttonSearch.setOnClickListener {
                val intent = Intent(this, SearchConference::class.java)
                startActivity(intent)
                finish()
            }

            buttonProfile.setOnClickListener {
                val intent = Intent(this, AccountPage::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
