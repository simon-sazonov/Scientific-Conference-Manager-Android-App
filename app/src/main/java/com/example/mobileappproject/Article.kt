package com.example.mobileappproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

data class Article(
    val id: String,
    val title: String,
    val author: String,
    val mainText: String,
    val date: String,
)

class AdapterArticle(private val context: Context, private val articles: MutableList<Article>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return articles.size
    }

    override fun getItem(position: Int): Article {
        return articles[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: inflater.inflate(R.layout.article_list_item, parent, false)

        val article = getItem(position)

        val titleTextView = rowView.findViewById<TextView>(R.id.article_title)
        val authorTextView = rowView.findViewById<TextView>(R.id.article_author)

        titleTextView.text = article.title
        authorTextView.text = article.author

        return rowView
    }

    fun updateArticles(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }
}
