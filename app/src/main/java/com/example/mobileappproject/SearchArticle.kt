package com.example.mobileappproject

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappproject.databinding.ActivitySearchArticleBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class SearchArticle : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySearchArticleBinding
    private lateinit var articleListView: ListView
    private lateinit var articleAdapter: AdapterArticle

    private val allArticles = mutableListOf<Article>()  // To keep a backup of all Articles
    private val articleList = mutableListOf<Article>()
    private var selectedSearchOption = "Title" // Default search option
    //
    //
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleListView = binding.articleListView
        articleAdapter = AdapterArticle(this, articleList)
        articleListView.adapter = articleAdapter


        loadAllArticlesFromFirebase()
        setupSearchView()

        binding.articleListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedArticle = articleAdapter.getItem(position)
            val intent = Intent(this, ArticlePage::class.java).apply {
                putExtra("articleTitle", selectedArticle.title)
                putExtra("articleAuthor", selectedArticle.author)
                putExtra("articleMainText", selectedArticle.mainText)
                putExtra("articleDate", selectedArticle.date)
                putExtra("articleId", selectedArticle.id)
            }
            startActivity(intent)
        }
        setupBottomNavigation()
        setupOptionsBox()
    }

    private fun setupOptionsBox() {
        val optionsBox = findViewById<LinearLayout>(R.id.options_box)
        val selectedOptionTextView = findViewById<TextView>(R.id.selected_option)
        selectedOptionTextView.text = selectedSearchOption // Set initial text

        optionsBox.setOnClickListener {
            val popupMenu = PopupMenu(this, optionsBox)
            popupMenu.menuInflater.inflate(R.menu.options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                selectedSearchOption = menuItem.title.toString()
                selectedOptionTextView.text = selectedSearchOption
                binding.searchView.setQuery("", false) // Clear search bar
                true
            }
            popupMenu.show()
        }
    }

    private fun loadAllArticlesFromFirebase() {
        db.collection("articles")
            .get()
            .addOnSuccessListener { querySnapshot ->
                articleList.clear()
                allArticles.clear()
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val artTitle = document.getString("artTitle") ?: ""
                    val artAuthor = document.getString("artAuthor") ?: ""
                    val mainText = document.getString("mainText") ?: ""
                    val Date = document.getDate("startDate")
                    val formattedDate = Date?.let {
                        SimpleDateFormat("'Start:' MMMM d, 'at' h a", Locale.getDefault()).format(it)
                    } ?: ""

                    val article = Article(id, artTitle, artAuthor, mainText, formattedDate)
                    articleList.add(article)
                    allArticles.add(article)
                }

                articleAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching articles: ", exception)
            }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchArticles(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter only if selectedSearchOption is set (avoids filtering on app launch)
                if (selectedSearchOption.isNotEmpty()) {
                    newText?.let { searchArticles(it) }
                }
                return true
            }
        })
    }

    private fun searchArticles(query: String) {
        val filteredList = when (selectedSearchOption) {
            "Title" -> allArticles.filter {
                it.title.contains(query, ignoreCase = true)
            }
            "Author" -> allArticles.filter {
                it.author.contains(query, ignoreCase = true)
            }
            else -> allArticles // Default to show all if the option is invalid
        }
        articleAdapter.updateArticles(filteredList)
    }

    private fun setupBottomNavigation() {
        val buttonHome = findViewById<ImageButton>(R.id.button_home)
        val buttonSettings = findViewById<ImageButton>(R.id.button_article)
        val buttonSearch = findViewById<ImageButton>(R.id.button_conference)
        val buttonProfile = findViewById<ImageButton>(R.id.button_account)
        val buttonBack = findViewById<ImageView>(R.id.back_arrow)

        buttonBack.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
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
