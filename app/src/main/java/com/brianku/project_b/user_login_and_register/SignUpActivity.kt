package com.brianku.project_b.user_login_and_register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import com.brianku.project_b.R
import com.brianku.project_b.dashboard.DashboardActivity
import com.brianku.project_b.extension.hideKeyboard
import com.brianku.project_b.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.File
import java.util.*

class SignUpActivity : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth
    private var selectedPhotoUri: Uri? = null
    private var smallThumbPhotoUri: Uri? = null
    private var imageUrl:String = "default"
    private var thumbnaiUrl:String = "default"
    private var displayName:String = ""
    private var hasPhoto:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sign_up_progressBar.visibility = ProgressBar.INVISIBLE

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

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        currentUser?.let{
            Log.d("vic","${it.displayName} / ${it.email}")
            goToDashboard()
        }
    }


    private fun initComponents(){
        sign_up_register_btn.setOnClickListener{


            if(!hasPhoto){
                Toast.makeText(this@SignUpActivity,"Please select a photo for user setting",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val email = sign_up_email_et.text.toString().trim()
            displayName = sign_up_name_et.text.toString().trim()
            val password = sign_up_password_et.text.toString().trim()
            val comfirmPassword = sign_up_confirm_password_et.text.toString().trim()

            if(password != comfirmPassword){
                Toast.makeText(this@SignUpActivity,"password confirmed incorrect",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if((email.isEmpty() || displayName.isEmpty() || password.isEmpty() || comfirmPassword.isEmpty())
                && password != comfirmPassword){
                Toast.makeText(this@SignUpActivity,"email/username/password can't be empty",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            sign_up_email_et.hideKeyboard()
            sign_up_name_et.hideKeyboard()
            sign_up_password_et.hideKeyboard()
            sign_up_confirm_password_et.hideKeyboard()

            sign_up_progressBar.visibility = ProgressBar.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


            registerAccount(email,password)

        }

        // set button to select photo using image-cropper

        main_select_photo_btn.setOnClickListener{


            // must to use get content for CropImage

            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(galleryIntent,
                RC_FOR_PHOTO
            )
        }
    }



    private fun goToDashboard(){
        sign_up_progressBar.visibility = ProgressBar.INVISIBLE
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val intent = Intent(this,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun saveToDB(){
        val currentUser = mAuth.currentUser
        currentUser?.let{
            val user = User(displayName,it.uid,it.email!!,imageUrl,thumbnaiUrl)
            val databaseRef = FirebaseDatabase.getInstance().reference
            databaseRef.child("Users").child(it.uid).setValue(user)
                .addOnCompleteListener{
                    if(it.isSuccessful) goToDashboard()
                }.addOnFailureListener{
                    Toast.makeText(this,"Failed: ${it.message}",Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun registerAccount(email:String,password:String){
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    uploadImageToFirebaseStorage()
                }else{
                    Toast.makeText(this,"Registered failed, your email or password format incorrect",Toast.LENGTH_LONG).show()
                }
            }
    }

    // Handle Image relative helper memthods

    private fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri == null || smallThumbPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val thumbFilename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")
        var thumbRef = FirebaseStorage.getInstance().getReference("/images/thumbs/${thumbFilename}")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    thumbRef.putFile(smallThumbPhotoUri!!)
                        .addOnSuccessListener {
                            thumbRef.downloadUrl.addOnSuccessListener {uri->
                                thumbnaiUrl = uri.toString()
                                saveToDB()
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
        hasPhoto = true
    }

    private fun setProfileImage(photoUri:Uri){
        val bitmapSrc = ImageDecoder.createSource(contentResolver,photoUri)
        val bitmap = ImageDecoder.decodeBitmap(bitmapSrc)
        main_circleimageview_imageview.setImageBitmap(bitmap)
        main_select_photo_btn.alpha = 0f
    }


    companion object {
        val RC_FOR_PHOTO = 2019
    }
}

