package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappproject.databinding.ActivityArticlePageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class ArticlePage : AppCompatActivity() {

    private lateinit var binding: ActivityArticlePageBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var articleId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticlePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Retrieve the articleId passed through the Intent
        articleId = intent.getStringExtra("articleId") ?: ""

        Log.d("ArticlePage", "Article ID: $articleId")

        if (articleId.isNotEmpty()) {
            getArticleDetails(articleId)
        } else {
            Toast.makeText(this, "Article ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Handle click event for the "See More" button
        binding.seeMoreBtn.setOnClickListener {
            val intent = Intent(this, FullCommentSection::class.java).apply {
                putExtra("articleId", articleId)
            }
            startActivity(intent)
        }

        // Handle click event for the back arrow button
        binding.backArrow.setOnClickListener {
            val intent = Intent(this, SearchArticle::class.java)
            startActivity(intent)
            finish()
        }

        // Handle click event for the "Post Comment" button
        binding.postCommentButton.setOnClickListener {
            postComment()
        }
    }

    private fun getArticleDetails(articleId: String) {
        firestore.collection("articles").document(articleId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Log the entire document data
                    Log.d("ArticlePage", "Document data: ${document.data}")

                    val articleTitle = document.getString("artTitle")
                    val articleAuthor = document.getString("artAuthor")
                    val articleMainText = document.getString("mainText")
                    val articleDateTimestamp = document.getTimestamp("Date")

                    // Convert Timestamp to String if it's not null
                    val articleDate = articleDateTimestamp?.let {
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(it.toDate())
                    }

                    Log.d("ArticlePage", "Fetched Article Title: $articleTitle")
                    Log.d("ArticlePage", "Fetched Article Author: $articleAuthor")
                    Log.d("ArticlePage", "Fetched Article Main Text: $articleMainText")
                    Log.d("ArticlePage", "Fetched Article Date: $articleDate")

                    // Set the data to the respective views
                    binding.ArticleName.text = articleTitle ?: "Title not available"
                    binding.AuthorName.text = articleAuthor ?: "Author not available"
                    binding.ArticleText.text = articleMainText ?: "Content not available"
                    binding.Date.text = articleDate ?: "Date not available"
                } else {
                    Log.e("ArticlePage", "No such document")
                    Toast.makeText(this, "Article not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ArticlePage", "Error fetching article details", exception)
                Toast.makeText(this, "Failed to load article", Toast.LENGTH_SHORT).show()
            }
    }

    private fun postComment() {
        val commentText = binding.commentInput.text.toString().trim()

        if (commentText.isNotEmpty()) {
            val comment = hashMapOf(
                "articleId" to articleId,
                "commentAuthorID" to "exampleAuthorId", // Replace with actual user ID
                "mainText" to commentText
            )

            firestore.collection("comments")
                .add(comment)
                .addOnSuccessListener {
                    Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show()
                    binding.commentInput.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.e("ArticlePage", "Error adding comment", e)
                    Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonSettings = findViewById<ImageButton>(R.id.button_article)
        val buttonSearch = findViewById<ImageButton>(R.id.button_conference)
        val buttonProfile = findViewById<ImageButton>(R.id.button_account)
        val buttonBack = findViewById<ImageView>(R.id.back_arrow)

        buttonBack.setOnClickListener {
            val intent = Intent(this, SearchArticle::class.java)
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
