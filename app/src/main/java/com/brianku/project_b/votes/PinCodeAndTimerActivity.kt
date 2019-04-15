package com.brianku.project_b.votes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.brianku.project_b.R
import com.brianku.project_b.app_central.AppCentralActivity
import com.brianku.project_b.dashboard.DashboardActivity
import com.brianku.project_b.model.Options
import com.brianku.project_b.model.Results
import com.brianku.project_b.model.Votes
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_pin_code_and_timer.*

class PinCodeAndTimerActivity : AppCompatActivity() {


    private lateinit var mDatabase: FirebaseDatabase
    private var pinCode:String = ""
    private var subject:String = ""
    private lateinit var options:Array<String>
    private var minute:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code_and_timer)
        mDatabase = FirebaseDatabase.getInstance()

        val charPool : List<Char> =  ('A'..'Z') + ('0'..'9')
        pinCode = (1..4)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        subject = intent.getStringExtra("Subject")
        minute = intent.getIntExtra("Minute",3)
        options = intent.getStringArrayExtra("Options")

        initComponents()
    }

    private fun initComponents(){

        pin_code_0_tv.text = pinCode[0].toString()
        pin_code_1_tv.text = pinCode[1].toString()
        pin_code_2_tv.text = pinCode[2].toString()
        pin_code_3_tv.text = pinCode[3].toString()

        pin_timter_timer_tv.text = "$minute:00"

        pin_time_ready_btn.setOnClickListener {
            it.isEnabled = false
            val uid = DashboardActivity.currentUser?.userId
            val reference = mDatabase.getReference("/Votes").push()
            if( uid != null && reference.key != null){
                val vote = Votes(subject,reference.key!!,uid,System.currentTimeMillis() / 1000,minute,pinCode)
                val optionsForVote = Options(options[0],options[1],options[2],options[3])
                val resultsForVote = Results(0,0,0,0)
                reference.setValue(vote).addOnSuccessListener {
                    reference.child("options").setValue(optionsForVote)
                        .addOnSuccessListener {
                            reference.child("results").setValue(resultsForVote)
                                .addOnSuccessListener {
                                    val userReference = mDatabase.getReference("/Users/$uid")
                                    userReference.child("history").child(reference.key!!).setValue(true).addOnSuccessListener {
                                        val pinReference = mDatabase.getReference("PinCodes/$pinCode")
                                        pinReference.setValue(reference.key!!).addOnSuccessListener {
                                            navigateToAppCentral(reference.key!!)
                                        }
                                    }
                                }
                    }
                }
            }
        }
    }

    private fun navigateToAppCentral(voteId:String){
        val intent = Intent(this,AppCentralActivity::class.java)
        intent.putExtra("VoteId",voteId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
