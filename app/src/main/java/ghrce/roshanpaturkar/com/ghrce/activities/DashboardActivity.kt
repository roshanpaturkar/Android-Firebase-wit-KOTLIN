package ghrce.roshanpaturkar.com.ghrce.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ghrce.roshanpaturkar.com.ghrce.R
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.nav_header_dashboard.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import ghrce.roshanpaturkar.com.ghrce.adapters.SectionPageAdapter
import kotlinx.android.synthetic.main.content_dashboard.*


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var sectionPagerAdapter: SectionPageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Dashboard!"
        /*--------------------------------------------------------------------------------------------------*/

        sectionPagerAdapter = SectionPageAdapter(supportFragmentManager)
        dashboardViewPager.adapter = sectionPagerAdapter
        dashboardTabLayout.setupWithViewPager(dashboardViewPager)
        dashboardTabLayout.setTabTextColors(Color.BLACK, Color.WHITE)

        setDrawerData()



        /*--------------------------------------------------------------------------------------------------*/
        dashBoardAddButton.setOnClickListener { view ->

            if (dashboardTabLayout.selectedTabPosition == 0 || dashboardTabLayout.selectedTabPosition == 1){
                startActivity(Intent(this, CreatePostActivity::class.java))
            } else {
                startActivity(Intent(this, CreateVotingActivity::class.java))
            }
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            moveTaskToBack(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_camera -> {

            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setDrawerData() {
        var mCurrentUser: FirebaseUser? = null
        var userId: String? = null

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        userId = mCurrentUser!!.uid

        var userName = FirebaseDatabase.getInstance().reference.child("GHRCE").child("Users").child(userId)

        userName!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                var fname = dataSnapshot!!.child("firstName").value.toString().capitalize()
                var lname = dataSnapshot!!.child("lastName").value.toString().capitalize()
                var email = dataSnapshot!!.child("email").value.toString()

                navigationName.text = "$fname $lname"
                navigationEmail.text = email
            }
        })

    }

}
