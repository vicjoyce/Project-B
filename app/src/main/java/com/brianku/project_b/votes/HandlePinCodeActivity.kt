package com.brianku.project_b.votes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.brianku.project_b.R
import com.brianku.project_b.app_central.AppCentralActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_handle_pin_code.*

class HandlePinCodeActivity : AppCompatActivity() {

    private lateinit var mDatabase:FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_pin_code)

        mDatabase = FirebaseDatabase.getInstance()

        handle_pin_next_btn.setOnClickListener {
            val pinFirst = handle_pin_0_et.text.toString().trim()
            val pinSecond = handle_pin_1_et.text.toString().trim()
            val pinThird = handle_pin_2_et.text.toString().trim()
            val pinForth = handle_pin_3_et.text.toString().trim()

            if(pinFirst.isEmpty() || pinSecond.isEmpty() || pinThird.isEmpty() || pinForth.isEmpty()){
                Toast.makeText(this,"Pin Code Can't Be Empty",Toast.LENGTH_LONG).show()
                handle_pin_0_et.setText("")
                handle_pin_1_et.setText("")
                handle_pin_2_et.setText("")
                handle_pin_3_et.setText("")
                return@setOnClickListener
            }

            val userInputPinCode = "$pinFirst$pinSecond$pinThird$pinForth"

            val reference = mDatabase.getReference("/PinCodes")
            reference.child(userInputPinCode).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                   if(dataSnapshot.exists()){
                       val voteId = dataSnapshot.getValue(String::class.java)
                       voteId?.let{
                           navigateToAppCentral(it)
                       }
                   }else{
                       Toast.makeText(this@HandlePinCodeActivity,"The Pin Code in Invalid, Please Try Another",Toast.LENGTH_LONG).show()
                   }
                }
            })
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
