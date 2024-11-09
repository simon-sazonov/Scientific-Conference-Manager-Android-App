package com.example.mobileappproject

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappproject.databinding.ActivityAdminSearchConferenceBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Admin_SearchConference : AppCompatActivity() {

    private lateinit var binding: ActivityAdminSearchConferenceBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var conferenceAdapter: ConferenceAdapter
    private lateinit var conferenceListView: ListView
    private var selectedDate: Calendar? = null


    private val allConferences = mutableListOf<Conference>()  // To keep a backup of all conferences
    private val conferenceList = mutableListOf<Conference>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSearchConferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conferenceListView = binding.conferenceListView
        conferenceAdapter = ConferenceAdapter(this, conferenceList)
        conferenceListView.adapter = conferenceAdapter

        val filterButton = findViewById<Button>(R.id.filterButton)
        filterButton.setOnClickListener {showDatePicker() }
        loadAllConferencesFromFirebase()
        setupSearchView()
        binding.conferenceListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedConference = conferenceAdapter.getItem(position) as Conference
            val intent = Intent(this, Conference_Edit::class.java)
            intent.putExtra("conferenceId", selectedConference.id)
            startActivity(intent)

            startActivity(intent)
        }
        setupBottomNavigation()
    }

    private fun loadAllConferencesFromFirebase() {
        db.collection("conferences")
            .get()
            .addOnSuccessListener { querySnapshot ->
                conferenceList.clear()
                for (document in querySnapshot.documents) {
                    val confTitle = document.getString("confTitle") ?: ""
                    val startDate = document.getDate("startDate")
                    val endDate = document.getDate("endDate")
                    val id = document.id


                    val formattedStartDate = startDate?.let {
                        SimpleDateFormat("'Start:' MMMM d, 'at' h a", Locale.getDefault()).format(it)
                    } ?: ""

                    val formattedEndDate = endDate?.let {
                        SimpleDateFormat("'End:' MMMM d, 'at' h a", Locale.getDefault()).format(it)
                    } ?: ""

                    val conference = Conference(id, confTitle, formattedStartDate, formattedEndDate)
                    conferenceList.add(conference)
                    allConferences.add(conference)
                }

                conferenceAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching conferences: ", exception)
            }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchConferences(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchConferences(it) }
                return true
            }
        })
    }

    private fun searchConferences(query: String) {
        val filteredList = allConferences.filter { conference ->
            val matchesQuery = conference.title.contains(query, ignoreCase = true)

            val matchesDate = selectedDate?.let { selectedDate ->
                try {
                    val confStartDateFormat = SimpleDateFormat("'Start:' MMMM d, 'at' h a", Locale.getDefault())
                    val confEndDateFormat = SimpleDateFormat("'End:' MMMM d, 'at' h a", Locale.getDefault())

                    val confStartDate = confStartDateFormat.parse(conference.startDate)
                    val confEndDate = confEndDateFormat.parse(conference.endDate)

                    if (confStartDate != null && confEndDate != null) {
                        val confStartCalendar = Calendar.getInstance().apply { time = confStartDate }
                        val confEndCalendar = Calendar.getInstance().apply { time = confEndDate }

                        val selectedDay = selectedDate.get(Calendar.DAY_OF_MONTH)
                        val selectedMonth = selectedDate.get(Calendar.MONTH)

                        // Compare day and month only
                        (selectedDay >= confStartCalendar.get(Calendar.DAY_OF_MONTH) &&
                                selectedMonth == confStartCalendar.get(Calendar.MONTH)) &&
                                (selectedDay <= confEndCalendar.get(Calendar.DAY_OF_MONTH) &&
                                        selectedMonth == confEndCalendar.get(Calendar.MONTH))

                    } else {
                        false // Handle case where parsing fails
                    }
                } catch (e: ParseException) {
                    Log.e(TAG, "Error parsing date: ", e)
                    false // Handle date parsing exception
                }
            } ?: true  // No selectedDate, include all conferences

            matchesQuery && matchesDate
        }

        conferenceAdapter.updateData(filteredList)
    }
    private fun showDatePicker() {
        val currentCalendar = Calendar.getInstance()
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, monthOfYear)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                searchConferences(binding.searchView.query.toString()) // Re-filter after date selection
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
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
