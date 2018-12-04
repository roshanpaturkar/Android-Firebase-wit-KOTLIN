package ghrce.roshanpaturkar.com.ghrce.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import ghrce.roshanpaturkar.com.ghrce.R
import ghrce.roshanpaturkar.com.ghrce.models.Comment

class CommentAdapter(dataBaseQuery: DatabaseReference, var context: Context)
    : FirebaseRecyclerAdapter <Comment, CommentAdapter.ViewHolder>(
        Comment::class.java,
        R.layout.comment_row,
        CommentAdapter.ViewHolder::class.java,
        dataBaseQuery
){
    override fun populateViewHolder(viewHolder: CommentAdapter.ViewHolder?, comment: Comment?, position: Int) {
        viewHolder!!.bindView(comment!!)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userName: String? = null
        var userComment: String? = null

        var name = itemView.findViewById<TextView>(R.id.commentUserName)
        var comment = itemView.findViewById<TextView>(R.id.userComment)

        fun bindView(comment: Comment) {

            userName = comment.name
            userComment = comment.comment

            name.text = comment.name
            this.comment.text = comment.comment

        }
    }

}