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
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.ByteArrayOutputStream
import java.io.File

class CreatePostActivity : AppCompatActivity() {

    var newsfeedDatabase: DatabaseReference? = null
    var userPostDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    var mStorageReference: StorageReference? = null
    var GALLERY_ID: Int = 1
    var imageUri: Uri? = null

    var userId: String? = null
    var name: String? = null

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        supportActionBar!!.title = "Share your memory!"

        mCurrentUser = FirebaseAuth.getInstance().currentUser
        userId = mCurrentUser!!.uid

        mStorageReference = FirebaseStorage.getInstance().reference

        newsfeedDatabase = FirebaseDatabase.getInstance().reference
                .child("GHRCE").child("NewsFeeds")
        userPostDatabase = FirebaseDatabase.getInstance().reference
                .child("GHRCE").child("UserPost").child(userId)

        progressDialog = ProgressDialog(this)

        createPostImageButoon.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_ID)
        }

        createPostSendPostButton.setOnClickListener {
            if (imageUri != null) {
                startUpload()
            } else {
                Toast.makeText(this, "Please select an image!", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun startUpload() {

        var caption: String = createPostCaptionEditText.text.toString().trim()
        var description: String = createPostDescriptionEditText.text.toString().trim()

        val fileUri: String = imageUri!!.lastPathSegment.toString()
        val fileName = fileUri.split("/").last()

        val filePath = mStorageReference!!.child("NewsFeed").child(userId!!).child(fileName)

        if (caption.isNotEmpty() && description.isNotEmpty()) {

            progressDialog!!.setMessage("Uploading Post...")
            progressDialog!!.show()

            var userName = FirebaseDatabase.getInstance().reference.child("GHRCE").child("Users").child(userId)

            userName!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    val fname = dataSnapshot!!.child("firstName").value.toString().capitalize()
                    val lname = dataSnapshot!!.child("lastName").value.toString().capitalize()
                    name = "Posted By : $fname $lname"
                }

            })

            val uploadTask: UploadTask = filePath.putBytes(compressImage(imageUri!!.lastPathSegment))


            uploadTask.addOnCompleteListener{
                var downloadUrl = uploadTask.result.downloadUrl.toString()

                if (uploadTask.isSuccessful){

                    val postObject = HashMap<String, Any>()
                    postObject.put("name", name.toString())
                    postObject.put("userId", userId.toString())
                    postObject.put("caption", caption)
                    postObject.put("description", description)
                    postObject.put("image", downloadUrl)
                    postObject.put("like", 0)
                    postObject.put("dislike", 0)
                    postObject.put("comment", 0)

                    var newsFeedObject: DatabaseReference = newsfeedDatabase!!.push()

                    newsFeedObject.setValue(postObject).addOnCompleteListener {

                        var key = newsFeedObject.key

                        var userPostObject: DatabaseReference = userPostDatabase!!.child(key)

                        userPostObject.setValue(postObject).addOnCompleteListener {

                            progressDialog!!.dismiss()
                            Toast.makeText(this, "Done!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()

                        }
                    }

                }else{
                    Toast.makeText(this, uploadTask.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }

        } else {
            if (caption.isEmpty()) createPostCaptionEditText.setError("Caption required!")
            if (description.isEmpty()) createPostDescriptionEditText.setError("Description required!")
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
            createPostImageButoon.setImageURI(imageUri)
        }
    }
}
