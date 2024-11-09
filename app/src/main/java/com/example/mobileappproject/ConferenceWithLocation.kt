package com.example.mobileappproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class ConferenceWithLocation(
    val id: String,
    val title: String,
    val startDate: Timestamp,
    val endDate: Timestamp,
    val latitude: Double,
    val longitude: Double
)

class ConferenceWithLocationAdapter(
    private val context: Context,
    private val conferenceList: MutableList<ConferenceWithLocation>
) : BaseAdapter() {

    override fun getCount(): Int {
        return conferenceList.size
    }

    override fun getItem(position: Int): ConferenceWithLocation {
        return conferenceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.conference_with_location_list_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val conference = getItem(position)
        viewHolder.title.text = conference.title
        viewHolder.startDate.text = formatDate(conference.startDate)
        viewHolder.endDate.text = formatDate(conference.endDate)
        viewHolder.location.text = "Lat: ${conference.latitude}, Long: ${conference.longitude}"

        return view
    }

    private class ViewHolder(view: View) {
        val title: TextView = view.findViewById(R.id.conference_title)
        val startDate: TextView = view.findViewById(R.id.conference_start_date)
        val endDate: TextView = view.findViewById(R.id.conference_end_date)
        val location: TextView = view.findViewById(R.id.conference_location)
    }

    fun updateData(newConferenceList: List<ConferenceWithLocation>) {
        conferenceList.clear()
        conferenceList.addAll(newConferenceList)
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }
}
