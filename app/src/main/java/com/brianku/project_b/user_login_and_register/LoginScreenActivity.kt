package com.brianku.project_b.user_login_and_register

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Toast
import com.brianku.project_b.R
import com.brianku.project_b.adapters.ViewPagerAdapter
import com.brianku.project_b.animation.ZoomOutPageTransformer
import com.brianku.project_b.dashboard.DashboardActivity
import com.brianku.project_b.model.TrainPage
import com.brianku.project_b.model.User
import com.facebook.*
import kotlinx.android.synthetic.main.activity_login_screen.*
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class LoginScreenActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mStorageRef:StorageReference


    private var listOfTrainPage:MutableList<TrainPage> = mutableListOf()
    private lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)


        initFirebaseAndFacebookProperties()

        setupViewpager()
        initComponent()
        pageIndicator.setViewPager(login_screen_viewpager_vp)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        currentUser?.let{
            goToDashboard()
        }
    }

    private fun initFirebaseAndFacebookProperties(){
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorageRef = FirebaseStorage.getInstance().getReference()
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }


    private fun initComponent(){
        login_screen_log_in_btn.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }

        login_screen_sign_up_btn.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        login_screen_facebook_btn.setOnClickListener{
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"))
            LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onSuccess(result: LoginResult?) {
                        result?.let{
                            handleFacebookAccessToken(it.accessToken)
                        }
                    }

                    override fun onCancel() {
                        Log.d("vicHuang","Facebook login cancel")
                    }

                    override fun onError(error: FacebookException?) {
                        Log.d("vicHuang","Facebook login Error")
                    }
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)
    }
    //=================================================================================================================
                            //Facebook auth and create User data from facebook public profile

    //=================================================================================================================

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = mAuth.currentUser
                    user?.let{
                        val usersTable = mDatabase.getReference("Users")
                        checkAccount(usersTable,it)
                    }

                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }



    // here to check if Facebook user already has account, retrieve data from db and process the validation
    private fun checkAccount(userTable: DatabaseReference, currentUser: FirebaseUser){
        userTable.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("vic","invoke firebase was cancelled")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let{
                        // if user exist, go to dashboard and finish()
                        goToDashboard()
                    }
                }else{
                    createUserFromFB(currentUser,userTable)
                }
            }
        })
    }

    private fun createUserFromFB(currentUser: FirebaseUser,usersTable:DatabaseReference) {
        val userId = currentUser.uid
        val displayName = currentUser.displayName ?: "facebook_user"
        val email = currentUser.email ?: "facebook_email"
        var imageUrl = currentUser.photoUrl.toString()
        imageUrl += "?height=500"
        val thumbUrl = imageUrl
        val user = User(displayName, userId, email,imageUrl,thumbUrl)


        usersTable.child(userId).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {

                goToDashboard()
            } else {
                Toast.makeText(this@LoginScreenActivity, "User created failed", Toast.LENGTH_SHORT).show()
            }
        }

    }



    private fun goToDashboard(){
        val dashboardIntent = Intent(this, DashboardActivity::class.java)
        dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(dashboardIntent)
        finish()
    }

//=====================================================================================================================


    private fun setupViewpager(){
        val images = arrayListOf<Int>(R.drawable.dummy_1,R.drawable.dummy_2,R.drawable.dummy_3)

        listOfTrainPage = mutableListOf(
            TrainPage("Page 1"
                ,"This page will describe the main purpose for the Vote app"
                ,images[0]
            ),
            TrainPage("Page 2"
                ,"This page will describe the how to use the vote"
                ,images[1]
            ),
            TrainPage("Page 3"
                ,"This page will describe how to chat with other people for the vote subject"
                ,images[2]
            )
        )

        pagerAdapter = ViewPagerAdapter(listOfTrainPage,applicationContext)
        login_screen_viewpager_vp.setPageTransformer(true, ZoomOutPageTransformer())
        login_screen_viewpager_vp.adapter = pagerAdapter
        login_screen_viewpager_vp.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(position: Int) {
                pageIndicator.setSelected(position)
            }

        })
    }
}
