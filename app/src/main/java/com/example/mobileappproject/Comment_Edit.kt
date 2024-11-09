package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Comment_Edit : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentsAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_edit)

        recyclerView = findViewById(R.id.comments_list_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CommentsAdapter(this, mutableListOf()) { commentId ->
            deleteComment(commentId)
        }
        recyclerView.adapter = adapter

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            onBackPressed()
        }

        setupBottomNavigation()
        loadComments()
    }

    private fun loadComments() {
        db.collection("comments").get()
            .addOnSuccessListener { querySnapshot ->
                val commentsList = mutableListOf<CommentWithArticle>()
                for (document in querySnapshot.documents) {
                    val articleId = document.getString("articleId") ?: ""
                    val commentId = document.id
                    val commentText = document.getString("mainText") ?: ""
                    Log.d("CommentsPage", "Comment ID: $commentId, Article ID: $articleId")

                    if (articleId.isNotEmpty()) {
                        db.collection("articles").document(articleId).get()
                            .addOnSuccessListener { articleDocument ->
                                val articleTitle = articleDocument.getString("artTitle") ?: "Unknown Title"
                                val commentWithArticle = CommentWithArticle(commentId, commentText, articleTitle)
                                commentsList.add(commentWithArticle)
                                Log.d("CommentsPage", "Added Comment: $commentId with Article Title: $articleTitle")
                                adapter.updateComments(commentsList)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("CommentsPage", "Failed to fetch article title", exception)
                            }
                    } else {
                        Log.e("CommentsPage", "Invalid article ID for comment: $commentId")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CommentsPage", "Failed to fetch comments", exception)
                Toast.makeText(this, "Failed to load comments", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteComment(commentId: String) {
        db.collection("comments").document(commentId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Comment deleted", Toast.LENGTH_SHORT).show()
                loadComments() // Refresh comments list
            }
            .addOnFailureListener { exception ->
                Log.e("CommentsPage", "Failed to delete comment", exception)
                Toast.makeText(this, "Failed to delete comment", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupBottomNavigation() {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonArticle = findViewById<ImageButton>(R.id.button_article)
        val buttonConference = findViewById<ImageButton>(R.id.button_conference)
        val buttonAccount = findViewById<ImageButton>(R.id.button_account)

        buttonHome.setOnClickListener {
            startActivity(Intent(this, Main_Admin::class.java))
            finish()
        }

        buttonArticle.setOnClickListener {
            startActivity(Intent(this, Admin_SearchArticle::class.java))
            finish()
        }

        buttonConference.setOnClickListener {
            startActivity(Intent(this, Admin_SearchConference::class.java))
            finish()
        }

        buttonAccount.setOnClickListener {
            startActivity(Intent(this, AccountPage::class.java))
            finish()
        }
    }


}
