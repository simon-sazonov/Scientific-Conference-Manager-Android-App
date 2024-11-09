package com.example.mobileappproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

data class Event(
    val name: String,        // Assuming this is event name, keeping it as is
    val time: String,        // Assuming this is event time, keeping it as is
    val room: String,        // Assuming this is event room, keeping it as is
    val info: String,        // Assuming this is event info, keeping it as is
    val articleName: String, // New field for article name
    val articleAuthor: String // New field for article author
)
class EventAdapter(private val context: Context, private val events: List<Event>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = events.size

    override fun getItem(position: Int): Any = events[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: inflater.inflate(R.layout.event_list_item, parent, false)
        val event = getItem(position) as Event

        val eventName = view.findViewById<TextView>(R.id.event_name)
        val eventTime = view.findViewById<TextView>(R.id.event_time)
        val eventRoom = view.findViewById<TextView>(R.id.event_room)
        val eventInfo = view.findViewById<TextView>(R.id.event_info)
        val articleNameTextView = view.findViewById<TextView>(R.id.article_name)
        val articleAuthorTextView = view.findViewById<TextView>(R.id.article_author)

        eventName.text = event.name
        eventTime.text = event.time
        eventRoom.text = event.room
        eventInfo.text = event.info
        articleNameTextView.text = event.articleName
        articleAuthorTextView.text = event.articleAuthor

        return view
    }
}
