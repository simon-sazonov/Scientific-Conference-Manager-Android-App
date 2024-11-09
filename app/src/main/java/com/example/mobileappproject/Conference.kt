package com.example.mobileappproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView


data class Conference(
    val id: String,
    val title: String,
    val startDate: String,
    val endDate: String
)


class ConferenceAdapter(
    private val context: Context,
    private val conferenceList: MutableList<Conference>
) : BaseAdapter() {

    override fun getCount(): Int {
        return conferenceList.size
    }

    override fun getItem(position: Int): Conference {
        return conferenceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.conference_list_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val conference = getItem(position)
        viewHolder.title.text = conference.title
        viewHolder.startDate.text = conference.startDate
        viewHolder.endDate.text = conference.endDate

        return view
    }

    private class ViewHolder(view: View) {
        val title: TextView = view.findViewById(R.id.conference_title)
        val startDate: TextView = view.findViewById(R.id.conference_start_date)
        val endDate: TextView = view.findViewById(R.id.conference_end_date)
    }

    fun updateData(newConferenceList: List<Conference>) {
        conferenceList.clear()
        conferenceList.addAll(newConferenceList)
        notifyDataSetChanged()
    }
}
