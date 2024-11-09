package com.example.mobileappproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Article_Creation : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var dateTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var backArrow: ImageView
    private lateinit var submitButton: Button
    private lateinit var db: FirebaseFirestore
    private var selectedDateTimestamp: Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_creation)

        dateTextView = findViewById(R.id.dateTextView)
        titleEditText = findViewById(R.id.titleEditText)
        authorEditText = findViewById(R.id.authorEditText)
        contentEditText = findViewById(R.id.contentEditText)
        backArrow = findViewById(R.id.back_arrow)
        submitButton = findViewById(R.id.submitButton)
        db = FirebaseFirestore.getInstance()

        dateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        backArrow.setOnClickListener {
            startActivity(Intent(this, Main_Admin::class.java))
            finish()
        }

        submitButton.setOnClickListener {
            submitArticle()
        }
        setupBottomNavigation()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, this, year, month, day).show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDateTimestamp = Timestamp(calendar.time)
        val selectedDate = "$dayOfMonth/${month + 1}/$year"
        dateTextView.text = selectedDate
    }

    private fun submitArticle() {
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || selectedDateTimestamp == null || content.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val article = hashMapOf(
            "artTitle" to title,
            "artAuthor" to author,
            "Date" to selectedDateTimestamp,
            "mainText" to content
        )

        db.collection("articles")
            .add(article)
            .addOnSuccessListener {
                Toast.makeText(this, "Article submitted successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Main_Admin::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit article: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
