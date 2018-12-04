package ghrce.roshanpaturkar.com.ghrce.adapters

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
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
import ghrce.roshanpaturkar.com.ghrce.models.Vote
import kotlinx.android.synthetic.main.voting_row.view.*

class VotingAdapter(dataBaseQuery: DatabaseReference, var context: Context)
    : FirebaseRecyclerAdapter<Vote, VotingAdapter.ViewHolder>(
        Vote::class.java,
        R.layout.voting_row,
        VotingAdapter.ViewHolder::class.java,
        dataBaseQuery
) {
    var mDatabase: DatabaseReference? = null
    var userId: String? = null
    var mCurrentUser: FirebaseUser? = null

    override fun populateViewHolder(viewHolder: VotingAdapter.ViewHolder?, vote: Vote?, position: Int) {
        val itemKey = getRef(position).key
        viewHolder!!.bindView(vote!!, context)

        mDatabase = FirebaseDatabase.getInstance().reference
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        userId = mCurrentUser!!.uid

        viewHolder.itemView.setOnClickListener {

            val decision = AlertDialog.Builder(context)  // warning dialog
            with(decision){
                setTitle("Give your decision!")
                setMessage("Are you agree with above?")
                setPositiveButton("Disagree"){
                    dialog, which ->
                    disAgree(itemKey)
                }
                setNegativeButton("Agree"){
                    dialog, which ->
                    agree(itemKey)
                }
            }
            decision.show()
        }

        viewHolder.itemView.setOnLongClickListener {
            val decisionLong = AlertDialog.Builder(context)  // warning dialog
            with(decisionLong){
                setTitle("Warning!")
                setMessage("Do you really want delete this?")
                setPositiveButton("Yes"){
                    dialog, which ->
                    deleteVoting(itemKey)
                }
                setNegativeButton("No"){
                    dialog, which ->
                    dialog.dismiss()
                }
            }
            decisionLong.show()
            true
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        //var userName: String? = null
        var issue: String? = null
        var image: String? = null
        var agree: Int? = null
        var disAgree: Int? = null

        fun bindView(vote: Vote, context: Context){
            //var name  = itemView.findViewById<TextView>(R.id.votingName)
            val voteIssue = itemView.findViewById<TextView>(R.id.votingIssue)
            val voteImage = itemView.findViewById<ImageView>(R.id.votingImage)
            val votigAgree = itemView.findViewById<TextView>(R.id.agreeTextView)
            val votingDisagree = itemView.findViewById<TextView>(R.id.disagreeTextView)

            //userName = vote.name
            issue = vote.issue
            image = vote.image
            agree = vote.agree
            disAgree = vote.disAgree

            //name.text = vote.name
            voteIssue.text = vote.issue
            votigAgree.text = vote.agree.toString()
            votingDisagree.text = vote.disAgree.toString()

            Picasso.with(context)
                    .load(image)
                    .into(voteImage)
        }
    }

    private fun agree(itemKey: String) {
        var agreeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("Voting")
                .child(itemKey)
                .child("decisions")
                .child("agree")
                .child(userId)
        val agreeObject = HashMap<String, Any>()
        agreeObject.put("decision", "agree")

        agreeData.setValue(agreeObject).addOnSuccessListener {
            mDatabase!!
                    .child("GHRCE")
                    .child("Voting")
                    .child(itemKey)
                    .child("decisions")
                    .child("disAgree")
                    .child(userId)
                    .setValue(null)
            setStatus(itemKey)
        }
    }

    private fun disAgree(itemKey: String) {
        var disAgreeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("Voting")
                .child(itemKey)
                .child("decisions")
                .child("disAgree")
                .child(userId)
        val agreeObject = HashMap<String, Any>()
        agreeObject.put("decision", "disagree")

        disAgreeData.setValue(agreeObject).addOnSuccessListener {
            mDatabase!!
                    .child("GHRCE")
                    .child("Voting")
                    .child(itemKey)
                    .child("decisions")
                    .child("agree")
                    .child(userId)
                    .setValue(null)
            setStatus(itemKey)
        }
    }

    private fun setStatus(itemKey: String) {
        val agreeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("Voting")
                .child(itemKey)
                .child("decisions")
                .child("agree")

        agreeData!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val count = dataSnapshot!!.childrenCount
                setCount(itemKey, count.toInt(), "agree")
            }
        })

        val disAgreeData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("Voting")
                .child(itemKey)
                .child("decisions")
                .child("disAgree")

        disAgreeData!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val count = dataSnapshot!!.childrenCount
                setCount(itemKey, count.toInt(), "disAgree")
            }
        })
    }

    private fun setCount(itemKey: String,count: Int, userDecision: String) {
        mDatabase!!
                .child("GHRCE")
                .child("Voting")
                .child(itemKey)
                .child(userDecision)
                .setValue(count)
    }

    private fun deleteVoting(itemKey: String) {
        val deleteData = FirebaseDatabase.getInstance().reference
                .child("GHRCE")
                .child("Voting")
                .child(itemKey)
                .child("userId")

        deleteData!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val id = dataSnapshot!!.value.toString()
                if (id.equals(userId)) {
                    delete(itemKey)
                }  else if (id.equals("null")) {
                    delete(itemKey)
                } else {
                    Toast.makeText(context, "You only delete the things that you create!", Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun delete(itemKey: String) {
        mDatabase!!
                .child("GHRCE")
                .child("Voting")
                .child(itemKey)
                .setValue(null).addOnSuccessListener {
                    Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_LONG).show()
                }

    }
}