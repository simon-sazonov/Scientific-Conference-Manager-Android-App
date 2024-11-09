package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappproject.databinding.ActivityFullCommentSectionBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView

class FullCommentSection : AppCompatActivity() {

    private lateinit var binding: ActivityFullCommentSectionBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var articleId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullCommentSectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Retrieve the articleId passed through the Intent
        articleId = intent.getStringExtra("articleId") ?: ""

        // Fetch comments for the specific article
        fetchComments()

        // Handle click event for the back arrow button
        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun fetchComments() {
        firestore.collection("comments")
            .whereEqualTo("articleId", articleId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val commentsList = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val commentText = document.getString("mainText") ?: ""
                    commentsList.add(commentText)
                }
                // Assuming you have a ListView to display the comments
                binding.fullCommentsListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, commentsList)
            }
            .addOnFailureListener { e ->
                Log.e("FullCommentSection", "Error fetching comments", e)
            }
    }

    private fun setupBottomNavigation() {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonSettings = findViewById<ImageButton>(R.id.button_article)
        val buttonSearch = findViewById<ImageButton>(R.id.button_conference)
        val buttonProfile = findViewById<ImageButton>(R.id.button_account)
        val buttonBack = findViewById<ImageView>(R.id.back_arrow)

        buttonBack.setOnClickListener {
            val intent = Intent(this, Main_Admin::class.java)
            startActivity(intent)
            finish()
        }

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
