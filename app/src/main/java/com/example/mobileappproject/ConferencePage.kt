package com.example.mobileappproject

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappproject.databinding.ActivityConferencePageBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class ConferencePage : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityConferencePageBinding
    private lateinit var map: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private lateinit var conferenceId: String

    var articleName: String = ""
    var articleAuthor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConferencePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conferenceId = intent.getStringExtra("conferenceId") ?: ""

        Log.d(TAG, "Conference ID: $conferenceId")

        if (conferenceId.isNotEmpty()) {
            getConferenceDetails(conferenceId)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupBottomNavigation()
    }

    private fun getConferenceDetails(conferenceId: String) {
        db.collection("conferences").document(conferenceId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val conferenceName = document.getString("confTitle")
                    val startDateTimestamp = document.getTimestamp("startDate")
                    val endDateTimestamp = document.getTimestamp("endDate")
                    val location = document.getGeoPoint("mapPoint")

                    binding.conferenceName.text = conferenceName

                    if (startDateTimestamp != null) {
                        val startDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(startDateTimestamp.toDate())
                        binding.conferenceStartDate.text = startDate
                    }

                    if (endDateTimestamp != null) {
                        val endDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(endDateTimestamp.toDate())
                        binding.conferenceEndDate.text = endDate
                    }

                    if (location != null) {
                        val conferenceLocation = LatLng(location.latitude, location.longitude)
                        map.addMarker(MarkerOptions().position(conferenceLocation).title(conferenceName))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(conferenceLocation, 15f))
                    }

                    if (startDateTimestamp != null && endDateTimestamp != null) {
                        val startDateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(startDateTimestamp.toDate())
                        val endDateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(endDateTimestamp.toDate())
                        setupDaysBox(startDateStr, endDateStr)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ConferencePage", "Error fetching conference details", exception)
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun setupDaysBox(startDate: String, endDate: String) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)

        if (start != null && end != null) {
            val diff = end.time - start.time
            val days = TimeUnit.MILLISECONDS.toDays(diff).toInt() + 1

            val daysBox = findViewById<LinearLayout>(R.id.days_box)
            daysBox.removeAllViews()

            for (i in 1..days) {
                val dayBox = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, 100).apply {
                        weight = 1f
                        marginStart = 8
                        marginEnd = 8
                    }
                    setBackgroundResource(R.drawable.box_day)
                    val textView = TextView(this@ConferencePage).apply {
                        text = "Day $i"
                        textSize = 18f
                        setTextColor(resources.getColor(android.R.color.black, null))
                        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                            gravity = android.view.Gravity.CENTER
                        }
                    }
                    addView(textView)
                    setOnClickListener {
                        fetchEventsForDay(i)
                    }
                }
                daysBox.addView(dayBox)
            }
        } else {
            Log.e("ConferencePage", "Error parsing start or end date")
        }
    }

    private fun fetchEventsForDay(dayNumber: Int) {
        Log.d("ConferencePage", "Fetching events for day $dayNumber")
        db.collection("days")
            .whereEqualTo("conferenceID", conferenceId)
            .whereEqualTo("dayNumber", dayNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val eventsList = mutableListOf<Event>()

                // Track the number of tasks to wait for
                var tasksCount = querySnapshot.size()
                if (tasksCount == 0) {
                    updateEventsListView(eventsList) // Update UI with empty list if no events found
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    val activityName = document.getString("activityName") ?: ""
                    val time = document.get("time")
                    val room = document.getString("room") ?: ""
                    val info = document.getString("info") ?: ""
                    val articleId = document.getString("articleId") ?: ""

                    // Handle time field which could be either a Timestamp or a String
                    val timeString = when (time) {
                        is String -> time
                        is Timestamp -> SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(time.toDate())
                        else -> ""
                    }

                    // Fetch article details asynchronously
                    db.collection("articles")
                        .document(articleId)
                        .get()
                        .addOnSuccessListener { articleDocument ->
                            if (articleDocument.exists()) {
                                val articleName = articleDocument.getString("artTitle") ?: ""
                                val articleAuthor = articleDocument.getString("artAuthor") ?: ""

                                // Create Event object and add to eventsList
                                val event = Event(activityName, timeString, room, info, articleName, articleAuthor)
                                eventsList.add(event)

                                // Check if all tasks are completed
                                tasksCount--
                                if (tasksCount == 0) {
                                    updateEventsListView(eventsList)
                                }
                            } else {
                                Log.e("ConferencePage", "Article document not found for articleId: $articleId")
                                tasksCount-- // Ensure task count is decremented even on failure
                                if (tasksCount == 0) {
                                    updateEventsListView(eventsList)
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ConferencePage", "Error fetching article: $exception")
                            tasksCount-- // Ensure task count is decremented even on failure
                            if (tasksCount == 0) {
                                updateEventsListView(eventsList)
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ConferencePage", "Failed to retrieve events", exception)
                Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateEventsListView(eventsList: List<Event>) {
        if (eventsList.isEmpty()) {
            binding.noEventsText.visibility = View.VISIBLE
            binding.conferenceListView.visibility = View.GONE
        } else {
            binding.noEventsText.visibility = View.GONE
            binding.conferenceListView.visibility = View.VISIBLE
            val adapter = EventAdapter(this, eventsList)
            binding.conferenceListView.adapter = adapter
        }
    }

    private fun setupBottomNavigation() {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonSettings = findViewById<ImageButton>(R.id.button_article)
        val buttonSearch = findViewById<ImageButton>(R.id.button_conference)
        val buttonProfile = findViewById<ImageButton>(R.id.button_account)
        val buttonBack = findViewById<ImageView>(R.id.back_arrow)

        buttonBack.setOnClickListener {
            val intent = Intent(this, SearchConference::class.java)
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
