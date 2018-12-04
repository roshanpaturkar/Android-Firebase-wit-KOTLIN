package ghrce.roshanpaturkar.com.ghrce.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import ghrce.roshanpaturkar.com.ghrce.R
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_create_voting.*
import java.io.ByteArrayOutputStream
import java.io.File

class CreateVotingActivity : AppCompatActivity() {

    var votingDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    var mStorageReference: StorageReference? = null
    var GALLERY_ID: Int = 1
    var imageUri: Uri? = null

    var userId: String? = null
    var name: String? = null

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_voting)
        supportActionBar!!.title = "What other think!"

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        userId = mCurrentUser!!.uid

        mStorageReference = FirebaseStorage.getInstance().reference

        votingDatabase = FirebaseDatabase.getInstance().reference
                .child("GHRCE").child("Voting")

        progressDialog = ProgressDialog(this)

        createVoteImageButton.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_ID)
        }

        createVoteIssueButton.setOnClickListener {
            if (imageUri == null) {
                createIssueWithoutImage()
            } else {
                createIssueWithImage()
            }
        }
    }

    private fun createIssueWithoutImage() {
        val issue: String = createVoteIssueEditText.text.toString().trim()

        if (issue.isNotEmpty()) {
            progressDialog!!.setMessage("Creating issue...")
            progressDialog!!.show()

            val userName = FirebaseDatabase.getInstance().reference.child("GHRCE").child("Users").child(userId)

            userName!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    val fname = dataSnapshot!!.child("firstName").value.toString().capitalize()
                    val lname = dataSnapshot!!.child("lastName").value.toString().capitalize()
                    name = "Created By : $fname $lname"

                    saveData(name!!, issue)
                }
            })
        } else {
            createVoteIssueEditText.setError("Describe your issue!")
        }
    }

    private fun createIssueWithImage() {
        val issue: String = createVoteIssueEditText.text.toString().trim()

        val fileUri: String = imageUri!!.lastPathSegment.toString()
        val fileName = fileUri.split("/").last()

        val filePath = mStorageReference!!.child("Voting").child(userId!!).child(fileName)

        if (issue.isNotEmpty()) {
            progressDialog!!.setMessage("Creating issue...")
            progressDialog!!.show()

            val userName = FirebaseDatabase.getInstance().reference.child("GHRCE").child("Users").child(userId)

            userName!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    val fname = dataSnapshot!!.child("firstName").value.toString().capitalize()
                    val lname = dataSnapshot!!.child("lastName").value.toString().capitalize()
                    name = "Created By : $fname $lname"
                }

            })

            val uploadTask: UploadTask = filePath.putBytes(compressImage(imageUri!!.lastPathSegment))

            uploadTask.addOnCompleteListener{
                val downloadUrl = uploadTask.result.downloadUrl.toString()

                if (uploadTask.isSuccessful) {
                    val issueObject = HashMap<String, Any>()
                    issueObject.put("name", name.toString())
                    issueObject.put("userId", userId.toString())
                    issueObject.put("issue", issue)
                    issueObject.put("image", downloadUrl)
                    issueObject.put("agree", 0)
                    issueObject.put("disAgree", 0)

                    val userIssueObject: DatabaseReference = votingDatabase!!.push()

                    userIssueObject.setValue(issueObject).addOnCompleteListener {
                        progressDialog!!.dismiss()
                        Toast.makeText(this, "Done!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this, uploadTask.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }

        } else {
            createVoteIssueEditText.setError("Enter your issue!")
        }
    }

    private fun compressImage(imgUri: String): ByteArray {
        var imageFile  = File(imgUri)

        var imageBitmap = Compressor(this)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(75)
                .compressToBitmap(imageFile)

        var byteArray = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

        var imageByteArray: ByteArray
        imageByteArray = byteArray.toByteArray()

        return imageByteArray
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK){
            imageUri = data!!.data
            createVoteImageButton.setImageURI(imageUri)
        }
    }

    private fun saveData(regards: String, issue: String) {
        val issueObject = HashMap<String, Any>()
        issueObject.put("name", regards)
        issueObject.put("userId", userId.toString())
        issueObject.put("issue", issue)
        issueObject.put("agree", 0)
        issueObject.put("disAgree", 0)

        val userIssueObject: DatabaseReference = votingDatabase!!.push()

        userIssueObject.setValue(issueObject).addOnCompleteListener {
            progressDialog!!.dismiss()
            Toast.makeText(this, "Done without image!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Field to share the issue!", Toast.LENGTH_LONG).show()
        }
    }
}
