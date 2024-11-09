package com.example.mobileappproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView


data class CommentWithArticle(
    val commentId: String,
    val commentText: String,
    val articleTitle: String
)

class CommentsAdapter(
    private val context: Context,
    private val comments: MutableList<CommentWithArticle>,
    private val onDeleteComment: (String) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val articleTitleTextView: TextView = view.findViewById(R.id.article_title_text_view)
        val commentTextView: TextView = view.findViewById(R.id.comment_text_view)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.articleTitleTextView.text = comment.articleTitle
        holder.commentTextView.text = comment.commentText

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Yes") { _, _ ->
                    onDeleteComment(comment.commentId)
                    comments.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount() = comments.size

    fun updateComments(newComments: List<CommentWithArticle>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }
}
