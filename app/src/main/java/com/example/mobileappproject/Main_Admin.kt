package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Main_Admin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        val createArticleButton = findViewById<Button>(R.id.button_create_article)
        val editArticleButton = findViewById<Button>(R.id.button_edit_article)
        val createConferenceButton = findViewById<Button>(R.id.button_create_conference)
        val editConferenceButton = findViewById<Button>(R.id.button_edit_conference)
        val editCommentsButton = findViewById<Button>(R.id.button_edit_comment)

        createArticleButton.setOnClickListener {
            startActivity(Intent(this, Article_Creation::class.java))
        }

        editArticleButton.setOnClickListener {
            // Navigate to the Edit Article Activity
            startActivity(Intent(this, Admin_SearchArticle::class.java))
        }

        createConferenceButton.setOnClickListener {
            startActivity(Intent(this, Conference_Creation::class.java))
        }

        editConferenceButton.setOnClickListener {
            // Navigate to the Edit Conference Activity
            startActivity(Intent(this, Admin_SearchConference::class.java))
        }

        editCommentsButton.setOnClickListener {
            startActivity(Intent(this, Comment_Edit::class.java))
        }

        // Call setupBottomNavigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonSettings = findViewById<ImageButton>(R.id.button_article)
        val buttonSearch = findViewById<ImageButton>(R.id.button_conference)
        val buttonProfile = findViewById<ImageButton>(R.id.button_account)

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
    }
}
