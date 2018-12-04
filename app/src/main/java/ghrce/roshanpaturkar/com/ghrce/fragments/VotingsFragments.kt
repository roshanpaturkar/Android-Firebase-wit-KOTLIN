package ghrce.roshanpaturkar.com.ghrce.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import ghrce.roshanpaturkar.com.ghrce.R
import ghrce.roshanpaturkar.com.ghrce.adapters.VotingAdapter
import kotlinx.android.synthetic.main.fragment_votings_fragments.*

/**
 * A simple [Fragment] subclass.
 *
 */
class VotingsFragments : Fragment() {

    var mUserDatabase: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_votings_fragments, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager =  LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        mUserDatabase = FirebaseDatabase.getInstance().reference.child("GHRCE").child("Voting")
        votingRecyclerView.setHasFixedSize(true)

        votingRecyclerView.layoutManager = linearLayoutManager
        votingRecyclerView.adapter = VotingAdapter(mUserDatabase!!, context)
    }

}
