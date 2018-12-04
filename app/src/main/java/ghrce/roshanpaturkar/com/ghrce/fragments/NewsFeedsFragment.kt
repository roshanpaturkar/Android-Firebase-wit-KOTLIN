package ghrce.roshanpaturkar.com.ghrce.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import ghrce.roshanpaturkar.com.ghrce.R
import ghrce.roshanpaturkar.com.ghrce.adapters.NewsFeedsAdapter
import kotlinx.android.synthetic.main.fragment_news_feeds.*


/**
 * A simple [Fragment] subclass.
 *
 */
class NewsFeedsFragment : Fragment() {

    var mUserDatabase: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feeds, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        mUserDatabase = FirebaseDatabase.getInstance().reference.child("GHRCE").child("NewsFeeds")
        newsFeedsRecyclerView.setHasFixedSize(true)

        newsFeedsRecyclerView.layoutManager = linearLayoutManager
        newsFeedsRecyclerView.adapter = NewsFeedsAdapter(mUserDatabase!!, context)

    }

}
