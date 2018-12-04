package ghrce.roshanpaturkar.com.ghrce.activities

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ghrce.roshanpaturkar.com.ghrce.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mDatabaseRef: DatabaseReference? = null

    var progressDailog: ProgressDialog? = null

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var DOB: String? = null
    var mobileNumber: String? = null
    var branch: String? = null
    var section: String? = null
    var collegeID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.title = "Registration!"

        mAuth = FirebaseAuth.getInstance()
        progressDailog = ProgressDialog(this)

        registerActivityRegisterButton.setOnClickListener {

            firstName = registerActivityFirstNameEditText.text.toString().trim()
            lastName = registerActivityLastNameEditText.text.toString().trim()
            email = registerActivityEmailEditText.text.toString().trim()
            password = registerActivityPasswordEditText.text.toString().trim()
            confirmPassword = registerActivityRepeatPasswordEditText.text.toString().trim()
            DOB = registerActivityDOBEditText.text.toString().trim()
            mobileNumber = registerActivityMobileNumberEditText.text.toString().trim()
            branch = registerActivityBranchEditText.text.toString().trim()
            section = registerActivitySectionEditText.text.toString().trim()
            collegeID = registerActivityCollegeIDEditText.text.toString().trim()

            if (isDataIsNotEmpty()) {
                if (checkEmail(email!!) && checkNumber(mobileNumber!!) && checkPassword(password!!, confirmPassword!!)) {
                    registerUser(email!!, password!!)
                }
            }

        }

    }

    private fun registerUser(registerEmail: String, registerPassward: String) {
        progressDailog!!.setMessage("Signing Up...")
        progressDailog!!.show()
        mAuth!!.createUserWithEmailAndPassword(registerEmail, registerPassward)
                .addOnCompleteListener {
                    task: Task<AuthResult> ->

                    if (task.isSuccessful) {
                        var currentUser = mAuth!!.currentUser
                        var userID = currentUser!!.uid

                        mDatabaseRef = FirebaseDatabase.getInstance().reference
                                .child("GHRCE").child("Users").child(userID)

                        var userObject = HashMap<String, String> ()
                        userObject.put("firstName", firstName!!)
                        userObject.put("lastName", lastName!!)
                        userObject.put("email", registerEmail!!)
                        userObject.put("DOB", DOB!!)
                        userObject.put("mobile", mobileNumber!!)
                        userObject.put("branch", branch!!)
                        userObject.put("section", section!!)
                        userObject.put("colledeID", collegeID!!)

                        mDatabaseRef!!.setValue(userObject).addOnCompleteListener {
                            task: Task<Void> ->

                            if (task.isSuccessful){
                                progressDailog!!.dismiss()
                                Toast.makeText(this, "$firstName Successfully Sign Up!", Toast.LENGTH_LONG).show()

                                var dashboardIntent = Intent(this, DashboardActivity::class.java)
                                dashboardIntent.putExtra("name", firstName)
                                startActivity(dashboardIntent)
                                finish()
                            } else {
                                progressDailog!!.dismiss()
                                Toast.makeText(this, "$firstName Failed to save the data!", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        progressDailog!!.dismiss()
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun isDataIsNotEmpty(): Boolean{
        if (firstName!!.isNotEmpty()
                && lastName!!.isNotEmpty()
                && email!!.isNotEmpty()
                && password!!.isNotEmpty()
                && confirmPassword!!.isNotEmpty()
                && DOB!!.isNotEmpty()
                && mobileNumber!!.isNotEmpty()
                && branch!!.isNotEmpty()
                && section!!.isNotEmpty()
                && collegeID!!.isNotEmpty()) {
            return true
        } else {
            if (firstName!!.isEmpty()) registerActivityFirstNameEditText.setError("First name required!")
            if (lastName!!.isEmpty()) registerActivityLastNameEditText.setError("Last name required!")
            if (email!!.isEmpty()) registerActivityEmailEditText.setError("Email required!")
            if (password!!.isEmpty()) registerActivityPasswordEditText.setError("Password required!")
            if (confirmPassword!!.isEmpty()) registerActivityRepeatPasswordEditText.setError("Re-enter password!")
            if (DOB!!.isEmpty()) registerActivityDOBEditText.setError("DOB is required!")
            if (mobileNumber!!.isEmpty()) registerActivityMobileNumberEditText.setError("Mobile number required!")
            if (branch!!.isEmpty()) registerActivityBranchEditText.setError("Branch required!")
            if (section!!.isEmpty()) registerActivitySectionEditText.setError("Section required!")
            if (collegeID!!.isEmpty()) registerActivityCollegeIDEditText.setError("CollegeID required!")

            return false
        }
    }

    private fun checkPassword(passwd: String, cnpasswd: String): Boolean{
        if (TextUtils.equals(passwd, cnpasswd)) {
            return true
        }
        registerActivityRepeatPasswordEditText.setError("Password not match!")
        return false
    }

    private fun checkEmail(email: String): Boolean {
        val valid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (valid) {
            return true
        } else {
            registerActivityEmailEditText.setError("Email not valid!")
            return false
        }
    }

    private fun checkNumber(number: String): Boolean {
       if (number.length == 10) {
           return true
       } else {
           registerActivityMobileNumberEditText.setError("Number not valid!")
           return false
       }
    }

}
