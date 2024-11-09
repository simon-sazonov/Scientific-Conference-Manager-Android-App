package com.example.mobileappproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappproject.databinding.ActivityHomePageBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "HomePage"
private const val MAX_ITEMS_LATEST_CONFERENCES = 5  // Limit to 5 items
private const val MAX_ITEMS_LATEST_ARTICLES = 2  // Limit to 2 items

class HomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var latestConferenceAdapter: ConferenceAdapter
    private lateinit var favouriteConferenceAdapter: ConferenceAdapter
    private lateinit var articleAdapter: AdapterArticle
    private lateinit var latestConferenceListView: ListView
    private lateinit var favouriteConferenceListView: ListView
    private lateinit var articleListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupViews()
        loadLatestConferencesFromFirebase()
        loadFavouriteConferencesFromFirebase()
        loadLatestArticlesFromFirebase()
    }

    private fun setupViews() {
        binding.conferencebtn.setOnClickListener {
            val intent = Intent(this, SearchConference::class.java)
            startActivity(intent)
        }

        binding.articleBtn.setOnClickListener {
            val intent = Intent(this, SearchArticle::class.java)
            startActivity(intent)
        }

        latestConferenceListView = findViewById(R.id.latest_conference_list_view)
        favouriteConferenceListView = findViewById(R.id.favourite_conference_list_view)
        articleListView = findViewById(R.id.article_list_view)

        latestConferenceAdapter = ConferenceAdapter(this, mutableListOf())
        latestConferenceListView.adapter = latestConferenceAdapter

        favouriteConferenceAdapter = ConferenceAdapter(this, mutableListOf())
        favouriteConferenceListView.adapter = favouriteConferenceAdapter

        articleAdapter = AdapterArticle(this, mutableListOf())
        articleListView.adapter = articleAdapter

        setupListViewClickListeners()
        setupBottomNavigation()
    }

    private fun setupListViewClickListeners() {
        latestConferenceListView.setOnItemClickListener { parent, view, position, id ->
            val selectedConference = latestConferenceAdapter.getItem(position)
            val intent = Intent(this, ConferencePage::class.java).apply {
                putExtra("conferenceId", selectedConference.id)
            }
            startActivity(intent)
        }

        favouriteConferenceListView.setOnItemClickListener { parent, view, position, id ->
            val selectedConference = favouriteConferenceAdapter.getItem(position)
            val intent = Intent(this, ConferencePage::class.java).apply {
                putExtra("conferenceId", selectedConference.id)
            }
            startActivity(intent)
        }

        articleListView.setOnItemClickListener { parent, view, position, id ->
            val selectedArticle = articleAdapter.getItem(position)
            val intent = Intent(this, ArticlePage::class.java).apply {
                putExtra("articleId", selectedArticle.id)
            }
            startActivity(intent)
        }
    }

    private fun loadLatestConferencesFromFirebase() {
        val conferencesCollection = db.collection("conferences")

        conferencesCollection.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val conferencesList = mutableListOf<Conference>()
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val title = document.getString("confTitle") ?: ""

                    // Check if startDate and endDate fields are of type Timestamp
                    val startDateTimestamp = document.get("startDate")
                    val endDateTimestamp = document.get("endDate")

                    // Convert Timestamp to String if it's not null and of type Timestamp
                    val startDate = if (startDateTimestamp is Timestamp) {
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(startDateTimestamp.toDate())
                    } else {
                        ""
                    }

                    val endDate = if (endDateTimestamp is Timestamp) {
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(endDateTimestamp.toDate())
                    } else {
                        ""
                    }

                    val conference = Conference(id, title, startDate, endDate)
                    conferencesList.add(conference)
                }

                // Limit the number of items to display
                val limitedConferencesList = conferencesList.take(MAX_ITEMS_LATEST_CONFERENCES)
                latestConferenceAdapter.updateData(limitedConferencesList)
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to retrieve conferences: ", exception)
            Toast.makeText(this, "Failed to load conferences", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFavouriteConferencesFromFirebase() {
        val conferenceList = mutableListOf<Conference>()

        // Fetch the list of favorite conference IDs
        db.collection("favouritesConferences")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val favoriteConferenceIds = querySnapshot.documents.map { it.getString("confID") ?: "" }

                // Fetch conferences where confID is in favoriteConferenceIds
                db.collection("conferences")
                    .whereIn(FieldPath.documentId(), favoriteConferenceIds)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val id = document.id
                            val title = document.getString("confTitle") ?: ""
                            val startDateTimestamp = document.getTimestamp("startDate")
                            val endDateTimestamp = document.getTimestamp("endDate")

                            // Format Timestamp to String
                            val startDate = startDateTimestamp?.let {
                                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(it.toDate())
                            } ?: ""
                            val endDate = endDateTimestamp?.let {
                                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(it.toDate())
                            } ?: ""

                            val conference = Conference(id, title, startDate, endDate)
                            conferenceList.add(conference)
                        }

                        // Update the adapter with the fetched data
                        favouriteConferenceAdapter.updateData(conferenceList)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error fetching favorite conferences: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching favorite conference IDs: ", exception)
            }
    }

    private fun loadLatestArticlesFromFirebase() {
        val articlesCollection = db.collection("articles")

        articlesCollection.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val articlesList = mutableListOf<Article>()
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val title = document.getString("artTitle") ?: ""
                    val author = document.getString("artAuthor") ?: ""
                    val mainText = document.getString("mainText") ?: ""
                    val dateTimestamp = document.getTimestamp("Date")

                    // Format Timestamp to String
                    val date = dateTimestamp?.let {
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(it.toDate())
                    } ?: ""

                    val article = Article(id, title, author, mainText, date)
                    articlesList.add(article)
                }

                // Limit the number of items to display
                val limitedArticlesList = articlesList.take(MAX_ITEMS_LATEST_ARTICLES)

                // Update the adapter with the fetched data
                articleAdapter.updateArticles(limitedArticlesList)
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to retrieve articles: ", exception)
            Toast.makeText(this, "Failed to load articles", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonSettings = findViewById<ImageButton>(R.id.button_article)
        val buttonSearch = findViewById<ImageButton>(R.id.button_conference)
        val buttonProfile = findViewById<ImageButton>(R.id.button_account)
        val buttonBack = findViewById<ImageView>(R.id.back_arrow)


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
