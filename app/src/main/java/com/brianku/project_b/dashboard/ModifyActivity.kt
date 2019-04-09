package com.brianku.project_b.dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import com.brianku.project_b.R
import com.brianku.project_b.extension.hideKeyboard
import com.brianku.project_b.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_modify.*
import java.io.File
import java.util.*

class ModifyActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var selectedPhotoUri: Uri? = null
    private var smallThumbPhotoUri: Uri? = null
    private var imageUrl:String = "default"
    private var thumbnaiUrl:String = "default"
    private var displayName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)
        modify_progressBar.visibility = ProgressBar.INVISIBLE

        val user = intent.getSerializableExtra("User") as User
        modify_name_et.setText(user.displayName)
        Picasso.get().load(user.image).into(modify_circleimageview_imageview)
        modify_select_photo_btn.alpha = 0f

        mAuth = FirebaseAuth.getInstance()
        initComponents()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == RC_FOR_PHOTO && resultCode == Activity.RESULT_OK) {
            data?.let {intent ->
                val image = intent.data
                CropImage.activity(image)
                    .setAspectRatio(1,1)
                    .start(this)
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            compressImage(data)
        }
    }

    private fun initComponents(){
        modify_update_btn.setOnClickListener{
            displayName = modify_name_et.text.toString().trim()
            if(displayName.isEmpty() && (imageUrl == "default")) return@setOnClickListener
            modify_name_et.hideKeyboard()
            updateUser()
        }

        // set button to select photo using image-cropper

        modify_select_photo_btn.setOnClickListener{

            // must to use get content for CropImage
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(galleryIntent,
                RC_FOR_PHOTO
            )
        }
    }

    private fun updateUser(){
        val uid = mAuth.uid
        var ref = FirebaseDatabase.getInstance().getReference("Users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let{
                    if (user.displayName != displayName && displayName != "") user.displayName = displayName
                    if (imageUrl != "default"){
                        user.image = imageUrl
                        user.thumbImage = thumbnaiUrl
                    }
                    ref.setValue(user).addOnCompleteListener{
                       if(it.isSuccessful) {
                           goToDashboard()
                           Log.d("vic","update ${user.displayName} / ${user.image}")
                       }
                    }.addOnFailureListener{
                        Log.d("vic","error occured: ${it.message}")
                    }
                }
            }
        })
    }


    private fun goToDashboard(){
        modify_progressBar.visibility = ProgressBar.INVISIBLE
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val intent = Intent(this,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    // Handle Image relative helper memthods

    private fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri == null || smallThumbPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val thumbFilename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")
        var thumbRef = FirebaseStorage.getInstance().getReference("/images/thumbs/${thumbFilename}")
        modify_progressBar.visibility = ProgressBar.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    thumbRef.putFile(smallThumbPhotoUri!!)
                        .addOnSuccessListener {
                            thumbRef.downloadUrl.addOnSuccessListener {uri->
                                thumbnaiUrl = uri.toString()

                                modify_progressBar.visibility = ProgressBar.INVISIBLE
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                        }
                }
            }
    }

    private fun compressImage(data: Intent?){

        // set normal profile image file and uri
        val thumbFile = File(CropImage.getActivityResult(data).uri.path)
        val compressFile = Compressor(this).compressToFile(thumbFile)
        selectedPhotoUri = Uri.fromFile(compressFile)
        setProfileImage(selectedPhotoUri!!)

        // set small and sqaure image for thumbnail image

        val smallCompressImage = Compressor(this)
            .setMaxWidth(200)
            .setMaxHeight(200)
            .setQuality(60)
            .compressToFile(thumbFile);
        smallThumbPhotoUri = Uri.fromFile(smallCompressImage)
        uploadImageToFirebaseStorage()
    }

    private fun setProfileImage(photoUri:Uri){
//        val bitmapSrc = ImageDecoder.createSource(contentResolver,photoUri)
//        val bitmap = ImageDecoder.decodeBitmap(bitmapSrc)
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,photoUri)
        modify_circleimageview_imageview.setImageBitmap(bitmap)
        modify_select_photo_btn.alpha = 0f
    }

    companion object {
        val RC_FOR_PHOTO = 200
    }
}
