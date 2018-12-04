package ghrce.roshanpaturkar.com.ghrce.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ghrce.roshanpaturkar.com.ghrce.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var userId: String? = null
    var mAuthListner: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mAuthListner = FirebaseAuth.AuthStateListener {
            firebaseAuth: FirebaseAuth ->

            user = firebaseAuth.currentUser

            if (user != null){
                startActivity(Intent(this, DashboardActivity::class.java))
            }else{
                Toast.makeText(this, "User Not Signed In...!!!", Toast.LENGTH_LONG).show()
            }
        }

        mainActivitySignInButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        mainActivitySignUpButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListner!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListner != null){
            mAuth!!.removeAuthStateListener(mAuthListner!!)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}
