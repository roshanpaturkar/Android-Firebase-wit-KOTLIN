package ghrce.roshanpaturkar.com.ghrce.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import ghrce.roshanpaturkar.com.ghrce.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null

    var progressDailog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.title = "Login!"

        mAuth = FirebaseAuth.getInstance()
        progressDailog = ProgressDialog(this)

        forgetPassword.setOnClickListener {
            var email: String = loginActivityEmailEditText.text.toString().trim()

            if (email.isEmpty()){
                loginActivityEmailEditText.setError("Enter email!")
            } else {
                progressDailog!!.setMessage("Sending request to server...")
                progressDailog!!.show()

                mAuth!!.sendPasswordResetEmail(email)
                        .addOnCompleteListener{

                            task ->

                            if (task.isSuccessful){
                                progressDailog!!.dismiss()

                                var data = AlertDialog.Builder(this)
                                with(data){
                                    setTitle("Request Send!")
                                    setMessage("Please check your mailbox and follow the instructions! \n\t\t\t\t\t       Thank You!")
                                    setPositiveButton("OK"){
                                        dialog, which ->
                                        dialog.dismiss().also {
                                            startActivity(Intent(context, MainActivity::class.java))
                                            finish()
                                        }
                                    }
                                }

                                data.show()

                            }else{
                                progressDailog!!.dismiss()
                                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                            }
                        }
            }
        }

        loginActivityLoginButton.setOnClickListener {
            var email: String = loginActivityEmailEditText.text.toString().trim()
            var password: String = loginActivityPasswordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()){
                loginUser(email, password)
            }else{
                if (TextUtils.isEmpty(email)){
                    loginActivityEmailEditText.setError("Email required !")
                }
                if (TextUtils.isEmpty(password)){
                    loginActivityPasswordEditText.setError("Password required !")
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {

        progressDailog!!.setMessage("Signing In...")
        progressDailog!!.show()

        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    task: Task<AuthResult> ->

                    if (task.isSuccessful){
                        progressDailog!!.dismiss()
                        var userName = email.split("@")[0] //split = roshan@gmail.com --> [roshan],[gmail.com]

                        var dashboardIntent = Intent(this, DashboardActivity::class.java)
                        dashboardIntent.putExtra("name", userName)
                        startActivity(dashboardIntent)
                        finish()
                    }else{
                        progressDailog!!.dismiss()
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                    }

                }
    }
}
