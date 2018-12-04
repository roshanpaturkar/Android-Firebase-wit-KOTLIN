package ghrce.roshanpaturkar.com.ghrce.activities

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import ghrce.roshanpaturkar.com.ghrce.R
import ghrce.roshanpaturkar.com.ghrce.adapters.CommentAdapter
import kotlinx.android.synthetic.main.activity_view_post.*
import kotlinx.android.synthetic.main.comment_popup.view.*
import kotlinx.android.synthetic.main.fragment_my_posts_fragments.*

class ViewPostActivity : AppCompatActivity() {

    var postId: String? = null
    var postUserId: String? = null

    var mDatabase: DatabaseReference? = null
    var userId: String? = null
    var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        val intent = intent
        if (intent != null) {
            postId = intent.extras.get("postId").toString()
        }

        mDatabase = FirebaseDatabase.getInstance().reference
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        userId = mCurrentUser!!.uid

        val postData = mDatabase!!.child("GHRCE").child("NewsFeeds").child(postId)

        postData.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val name = dataSnapshot!!.child("name").value.toString()
                val caption = dataSnapshot.child("caption").value.toString()
                val description = dataSnapshot.child("description").value.toString()
                val comment = dataSnapshot.child("comment").value.toString()
                val like = dataSnapshot.child("like").value.toString()
                val dislike = dataSnapshot.child("dislike").value.toString()
                val image = dataSnapshot.child("image").value.toString()
                val postUId = dataSnapshot.child("userId").value.toString()

                postUserId = postUId

                postUserNameTextView.text = name
                postCaptionTextView.text = caption
                postDescriptionTextView.text = description
                postCommentTextView.text = comment
                postLikeTextView.text = like
                postDislikeTextView.text = dislike

                Picasso.with(this@ViewPostActivity)
                        .load(image)
                        .into(postImageImageView)
            }

        })

        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true

        mDatabase = FirebaseDatabase.getInstance().reference.child("GHRCE").child("NewsFeeds").child(postId).child("comments")
        postCommentRecyclerView.setHasFixedSize(true)

        postCommentRecyclerView.layoutManager = linearLayoutManager
        postCommentRecyclerView.adapter = CommentAdapter(mDatabase!!, this)

        postLikeButton.setOnClickListener {view ->
            Snackbar.make(view, "Lo main agaya!", Snackbar.LENGTH_LONG).show()
            Toast.makeText(this, "Hit!", Toast.LENGTH_LONG).show()
            like(postId.toString())
        }

        postDislikeButton.setOnClickListener {
            Toast.makeText(this, "Hit!", Toast.LENGTH_LONG).show()
            dislike(postId.toString())
        }

        postCommentButton.setOnClickListener {
            comment(postId.toString())
        }
    }

    private fun like(itemKey: String) {
        var likeData = FirebaseDatabase.getInstance().reference
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

        val view = LayoutInflater.from(this).inflate(R.layout.comment_popup, null)
        val commentEdit = view.popupCommentEditText
        val sendButton = view.popupSendButton
        val cancelButton = view.popupCancelButton

        sendButton.text = "Send"
        cancelButton.text = "Cancel"

        dialogBuilder = AlertDialog.Builder(this).setView(view)
        dialog = dialogBuilder.create()
        dialog.show()

        sendButton.setOnClickListener {

            var userId: String? = null
            var name: String? = null

            mCurrentUser = FirebaseAuth.getInstance().currentUser
            userId = mCurrentUser!!.uid

            val comment = commentEdit.text.toString().trim()

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
                Toast.makeText(this, "Successfully Done!", Toast.LENGTH_LONG).show()

                val countCommentPath = FirebaseDatabase.getInstance().reference
                        .child("GHRCE")
                        .child("NewsFeeds")
                        .child(itemKey)
                        .child("comments")

                countCommentPath.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        val getCount = dataSnapshot!!.childrenCount
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
