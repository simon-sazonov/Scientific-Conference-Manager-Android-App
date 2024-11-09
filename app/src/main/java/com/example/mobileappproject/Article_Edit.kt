package com.example.mobileappproject

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappproject.databinding.ActivityArticleEditBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class Article_Edit : AppCompatActivity() {

    private lateinit var binding: ActivityArticleEditBinding
    private lateinit var firestore: FirebaseFirestore
    private var articleId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val articleTitle = intent.getStringExtra("articleTitle") ?: ""
        val articleAuthor = intent.getStringExtra("articleAuthor") ?: ""
        val articleMainText = intent.getStringExtra("articleMainText") ?: ""
        val articleDate = intent.getStringExtra("articleDate") ?: ""
        articleId = intent.getStringExtra("articleId") ?: ""

        binding.titleEditText.setText(articleTitle)
        binding.authorEditText.setText(articleAuthor)
        binding.contentEditText.setText(articleMainText)
        binding.dateTextView.text = articleDate

        // Implement the back arrow functionality
        binding.backArrow.setOnClickListener {
            finish()
        }

        // Implement delete button functionality with confirmation dialog
        binding.DeleteButton.setOnClickListener {
            showDeleteConfirmationDialog(articleId)
        }

        // Implement save button functionality
        binding.saveButton.setOnClickListener {
            saveArticleChanges(articleId)
        }

        // Implement date picker for dateTextView
        binding.dateTextView.setOnClickListener {
            showDatePickerDialog()
        }
        setupBottomNavigation()
    }

    private fun showDeleteConfirmationDialog(articleId: String) {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete this article?")
            .setPositiveButton("Delete") { _, _ ->
                deleteArticle(articleId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteArticle(articleId: String) {
        firestore.collection("articles").document(articleId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Article deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete article", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveArticleChanges(articleId: String) {
        val title = binding.titleEditText.text.toString().trim()
        val author = binding.authorEditText.text.toString().trim()
        val content = binding.contentEditText.text.toString().trim()
        val dateStr = binding.dateTextView.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || content.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert dateStr to Timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateStr)
        val timestamp = date?.let { Timestamp(it) }

        val updatedArticle = mapOf(
            "artTitle" to title,
            "artAuthor" to author,
            "mainText" to content,
            "Date" to timestamp  // Store date as Timestamp
        )

        firestore.collection("articles").document(articleId).set(updatedArticle)
            .addOnSuccessListener {
                Toast.makeText(this, "Article updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update article", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.dateTextView.text = selectedDate
        }, year, month, day)

        datePickerDialog.show()
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
