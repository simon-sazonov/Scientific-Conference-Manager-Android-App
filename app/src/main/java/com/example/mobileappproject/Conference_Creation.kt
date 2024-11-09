package com.example.mobileappproject

import android.app.Activity
import android.app.AlertDialog
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
import com.example.mobileappproject.databinding.ActivityConferenceCreationBinding
import com.example.mobileappproject.databinding.ActivityConferenceEditBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class Conference_Creation : AppCompatActivity() {


    private lateinit var binding: ActivityConferenceCreationBinding
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val MAP_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConferenceCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val confTitle = intent.getStringExtra("confTitle") ?: ""
        val presenter = intent.getStringExtra("presenter") ?: ""
        val confMainText = intent.getStringExtra("confMainText") ?: ""
        val startDate = intent.getStringExtra("startDate") ?: ""
        val endDate = intent.getStringExtra("endDate") ?: ""
        val mapPoint = intent.getStringExtra("mapPoint") ?: ""


        binding.confTitleEditText.setText(confTitle)
        binding.presenterEditText.setText(presenter)
        binding.contentEditText.setText(confMainText)
        binding.startDateEditView.setText(startDate)
        binding.endDateEditView.setText(endDate)
        binding.mapPointEditText.setText(mapPoint)

        // Implement the back arrow functionality
        binding.backArrow.setOnClickListener {
            finish()
        }

        // Implement save button functionality
        binding.submitConferenceButton.setOnClickListener {
            saveConferenceChanges()
        }

        // Implement date picker for startDateEditView and endDateEditView
        binding.startDateEditView.setOnClickListener {
            showDatePickerDialog { date -> binding.startDateEditView.setText(date) }
        }

        binding.endDateEditView.setOnClickListener {
            showDatePickerDialog { date -> binding.endDateEditView.setText(date) }
        }

        // Open map picker when mapPointEditText is clicked
        binding.mapPointEditText.setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            startActivityForResult(intent, MAP_REQUEST_CODE)
        }

        setupBottomNavigation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val latitude = it.getDoubleExtra("latitude", 0.0)
                val longitude = it.getDoubleExtra("longitude", 0.0)
                val mapPointStr = "$latitude, $longitude"
                binding.mapPointEditText.setText(mapPointStr)
            }
        }
    }

    private fun saveConferenceChanges() {
        val title = binding.confTitleEditText.text.toString().trim()
        val presenter = binding.presenterEditText.text.toString().trim()
        val content = binding.contentEditText.text.toString().trim()
        val startDateStr = binding.startDateEditView.text.toString().trim()
        val endDateStr = binding.endDateEditView.text.toString().trim()
        val mapPointStr = binding.mapPointEditText.text.toString().trim()

        if (title.isEmpty() || presenter.isEmpty() || content.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || mapPointStr.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert startDateStr and endDateStr to Timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = dateFormat.parse(startDateStr)
        val endDate = dateFormat.parse(endDateStr)
        val startTimestamp = startDate?.let { Timestamp(it) }
        val endTimestamp = endDate?.let { Timestamp(it) }

        // Convert mapPointStr to GeoPoint
        val mapPointParts = mapPointStr.split(", ")
        val latitude = mapPointParts[0].toDouble()
        val longitude = mapPointParts[1].toDouble()
        val geoPoint = GeoPoint(latitude, longitude)

        val updatedConference = mapOf(
            "confTitle" to title,
            "presenter" to presenter,
            "confMainText" to content,
            "startDate" to startTimestamp,
            "endDate" to endTimestamp,
            "mapPoint" to geoPoint
        )

        firestore.collection("conferences").add(updatedConference)
            .addOnSuccessListener {
                Toast.makeText(this, "Conference added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add Conference", Toast.LENGTH_SHORT).show()
            }

    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSet(selectedDate)
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

