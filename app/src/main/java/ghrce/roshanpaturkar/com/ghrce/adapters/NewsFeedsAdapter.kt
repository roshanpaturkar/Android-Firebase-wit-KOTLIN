package ghrce.roshanpaturkar.com.ghrce.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import ghrce.roshanpaturkar.com.ghrce.R
import ghrce.roshanpaturkar.com.ghrce.activities.ViewPostActivity
import ghrce.roshanpaturkar.com.ghrce.models.Post
import kotlinx.android.synthetic.main.comment_popup.view.*

class NewsFeedsAdapter(dataBaseQuery: DatabaseReference, var context: Context)
    : FirebaseRecyclerAdapter<Post, NewsFeedsAdapter.ViewHolder>(
        Post::class.java,
        R.layout.timeline_row,
        NewsFeedsAdapter.ViewHolder::class.java,
        dataBaseQuery
){

    var postUserId : String? = null
    var mDatabase: DatabaseReference? = null
    var userId: String? = null
    var mCurrentUser: FirebaseUser? = null

    override fun populateViewHolder(viewHolder: NewsFeedsAdapter.ViewHolder?, post: Post?, position: Int) {
        val itemKey = getRef(position).key


        viewHolder!!.bindView(post!!, context)

        mDatabase = FirebaseDatabase.getInstance().reference
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        userId = mCurrentUser!!.uid

        viewHolder.likeImageView.setOnClickListener {
            postUserId = viewHolder.userId.toString()
            like(itemKey)
        }

        viewHolder.disLikeImageView.setOnClickListener {
            postUserId = viewHolder.userId.toString()
            dislike(itemKey)
        }

        viewHolder.commentImageView.setOnClickListener {
            postUserId = viewHolder.userId.toString()
            comment(itemKey)
        }

        viewHolder.itemView.setOnClickListener {
            var viewActivityIntent = Intent(context, ViewPostActivity::class.java)
            viewActivityIntent.putExtra("postId", itemKey)
            context.startActivity(viewActivityIntent)
        }


//        viewHolder.itemView.setOnClickListener {
//
////            postUserId = viewHolder.userId.toString()
////
////            showMenu(itemKey)
////
//        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userName: String? = null
        var userId: String? = null
        var caption: String? = null
        var description: String? = null
        var image: String? = null
        var like: Int? = null
        var dislike: Int? = null
        var comment: Int? = null

        var name = itemView.findViewById<TextView>(R.id.userNamePost)

        var title = itemView.findViewById<TextView>(R.id.postCaption)
        var desc = itemView.findViewById<TextView>(R.id.postDesc)
        var postImage = itemView.findViewById<ImageView>(R.id.postImage)

        var likeView = itemView.findViewById<TextView>(R.id.likeTextView)
        var dislikeView = itemView.findViewById<TextView>(R.id.dislikeTextView)
        var commentView = itemView.findViewById<TextView>(R.id.commentTextView)

        var likeImageView = itemView.findViewById<ImageView>(R.id.likeImageView)
        var disLikeImageView = itemView.findViewById<ImageView>(R.id.dislikeImageView)
        var commentImageView = itemView.findViewById<ImageView>(R.id.commentImageView)

        fun bindView(post: Post, context: Context) {

            userName = post.name
            userId = post.userId
            caption = post.caption
            description = post.description
            image = post.image
            like = post.like
            dislike = post.dislike
            comment = post.comment

            name.text = post.name
            title.text = post.caption
            desc.text = post.description
            likeView.text = post.like.toString()
            dislikeView.text = post.dislike.toString()
            commentView.text = post.comment.toString()

            Picasso.with(context)
                    .load(image)
                    .placeholder(R.drawable.waiting)
                    .into(postImage)

        }
    }

    private fun showMenu(key: String?) {

        val options = arrayOf("Like", "Dislike", "Comment", "View Post")
        val builder = android.support.v7.app.AlertDialog.Builder(context)
        builder.setTitle("Selecte Options")
        builder.setItems(options, DialogInterface.OnClickListener { dialogInterface, i ->

            if (i == 0) {
                like(key.toString())
            }else if(i == 1) {
                dislike(key.toString())
            } else if (i == 2) {
                comment(key.toString())
            } else {

            }
        })

        builder.show()
    }

    private fun like(itemKey: String) {
        val likeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("NewsFeeds")
                .child(itemKey)
                .child("decisions")
                .child("like")
                .child(userId)
        val agreeObject = HashMap<String, Any>()
        agreeObject.put("decision", "like")

        likeData.setValue(agreeObject).addOnSuccessListener {
            mDatabase!!
                    .child("GHRCE")
                    .child("NewsFeeds")
                    .child(itemKey)
                    .child("decisions")
                    .child("dislike")
                    .child(userId)
                    .setValue(null)
            setStatus(itemKey)
        }

        val userlikeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("UserPost")
                .child(postUserId)
                .child(itemKey)
                .child("decisions")
                .child("like")
                .child(userId)

        userlikeData.setValue(agreeObject).addOnSuccessListener {
            mDatabase!!
                    .child("GHRCE")
                    .child("UserPost")
                    .child(postUserId)
                    .child(itemKey)
                    .child("decisions")
                    .child("dislike")
                    .child(userId)
                    .setValue(null)
            setStatus(itemKey)
        }
    }

    private fun dislike(itemKey: String) {
        val dislikeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("NewsFeeds")
                .child(itemKey)
                .child("decisions")
                .child("dislike")
                .child(userId)
        val likeObject = HashMap<String, Any>()
        likeObject.put("decision", "dislike")

        dislikeData.setValue(likeObject).addOnSuccessListener {
            mDatabase!!
                    .child("GHRCE")
                    .child("NewsFeeds")
                    .child(itemKey)
                    .child("decisions")
                    .child("like")
                    .child(userId)
                    .setValue(null)
            setStatus(itemKey)
        }

        val userDislikeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("UserPost")
                .child(postUserId)
                .child(itemKey)
                .child("decisions")
                .child("dislike")
                .child(userId)
        val userDislikeObject = HashMap<String, Any>()
        userDislikeObject.put("decision", "dislike")

        userDislikeData.setValue(userDislikeObject).addOnSuccessListener {
            mDatabase!!
                    .child("GHRCE")
                    .child("UserPost")
                    .child(postUserId)
                    .child(itemKey)
                    .child("decisions")
                    .child("like")
                    .child(userId)
                    .setValue(null)
            setStatus(itemKey)
        }
    }

    private fun setStatus(itemKey: String) {
        val likeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("NewsFeeds")
                .child(itemKey)
                .child("decisions")
                .child("like")

        likeData!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val count = dataSnapshot!!.childrenCount
                setCount(itemKey, count.toInt(), "like")
            }
        })

        val userLikeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("UserPost")
                .child(postUserId)
                .child(itemKey)
                .child("decisions")
                .child("like")

        userLikeData!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val count = dataSnapshot!!.childrenCount
                setCount(itemKey, count.toInt(), "like")
            }
        })

        val disLikeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("NewsFeeds")
                .child(itemKey)
                .child("decisions")
                .child("dislike")

        disLikeData!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val count = dataSnapshot!!.childrenCount
                setCount(itemKey, count.toInt(), "dislike")
            }
        })

        val userDisLikeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("UserPost")
                .child(postUserId)
                .child(itemKey)
                .child("decisions")
                .child("dislike")

        userDisLikeData!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val count = dataSnapshot!!.childrenCount
                setCount(itemKey, count.toInt(), "dislike")
            }
        })
    }

    private fun setCount(itemKey: String,count: Int, userDecision: String) {
        mDatabase!!
                .child("GHRCE")
                .child("NewsFeeds")
                .child(itemKey)
                .child(userDecision)
                .setValue(count)

        mDatabase!!
                .child("GHRCE")
                .child("UserPost")
                .child(postUserId)
                .child(itemKey)
                .child(userDecision)
                .setValue(count)
    }

    private fun comment(itemKey: String) {

        val dialogBuilder: AlertDialog.Builder?
        val dialog: AlertDialog?

        val view = LayoutInflater.from(context).inflate(R.layout.comment_popup, null)
        val commentEdit = view.popupCommentEditText
        val sendButton = view.popupSendButton
        val cancelButton = view.popupCancelButton

        sendButton.text = "Send"
        cancelButton.text = "Cancel"

        dialogBuilder = AlertDialog.Builder(context).setView(view)
        dialog = dialogBuilder.create()
        dialog.show()

        sendButton.setOnClickListener {

            var userId: String? = null
            var name: String? = null

            mCurrentUser = FirebaseAuth.getInstance().currentUser
            userId = mCurrentUser!!.uid

            var comment = commentEdit.text.toString().trim()

            val userName = FirebaseDatabase.getInstance().reference.child("GHRCE").child("Users").child(userId)

            userName!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    val fname = dataSnapshot!!.child("firstName").value.toString().capitalize()
                    val lname = dataSnapshot!!.child("lastName").value.toString().capitalize()
                    name = "$fname $lname : "
                    saveData(name!!, comment, itemKey)
                }
            })
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun saveData(name: String, comment: String, itemKey: String) {
        val commentObject = HashMap<String, Any>()
        commentObject.put("name", name)
        commentObject.put("userId", userId.toString())
        commentObject.put("comment", comment)

        val data = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("NewsFeeds")
                .child(itemKey)
                .child("comments")
                .push()

        data!!.setValue(commentObject).addOnSuccessListener {
            val userData = FirebaseDatabase.getInstance().reference
                    .child("GHRCE")
                    .child("UserPost")
                    .child(postUserId)
                    .child(itemKey)
                    .child("comments")
                    .push()

            userData.setValue(commentObject).addOnCompleteListener {
                Toast.makeText(context, "Successfully Done!", Toast.LENGTH_LONG).show()

                val countCommentPath = FirebaseDatabase.getInstance().reference
                        .child("GHRCE")
                        .child("NewsFeeds")
                        .child(itemKey)
                        .child("comments")

                countCommentPath.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        var getCount = dataSnapshot!!.childrenCount
                        setCommentCount(itemKey, getCount.toInt())
                    }

                })
            }
        }
    }

    private fun setCommentCount(itemKey: String, count: Int) {
        mDatabase!!
                .child("GHRCE")
                .child("NewsFeeds")
                .child(itemKey)
                .child("comment")
                .setValue(count)

        mDatabase!!
                .child("GHRCE")
                .child("UserPost")
                .child(postUserId)
                .child(itemKey)
                .child("comment")
                .setValue(count)
    }
}