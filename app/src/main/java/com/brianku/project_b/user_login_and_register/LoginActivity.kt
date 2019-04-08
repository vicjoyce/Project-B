package com.brianku.project_b.user_login_and_register

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.brianku.project_b.R
import com.brianku.project_b.dashboard.DashboardActivity
import com.brianku.project_b.extension.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        login_login_btn.setOnClickListener{
            val email = login_email_et.text.toString().trim()
            val password = login_password_et.text.toString().trim()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Email or Password must be applied!!",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            login_email_et.hideKeyboard()
            login_password_et.hideKeyboard()
            loginUser(email,password)
        }
    }


    private fun loginUser(email:String,password:String){
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(it.isSuccessful){

                    goToDashboard()
                }else{
                    Toast.makeText(this@LoginActivity,
                        "The email and password doesn't match"
                        ,Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener{
                Log.d("vic","sign in failed : ${it.message}")
            }
    }

    private fun goToDashboard(){
        val intent = Intent(this,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }


}
