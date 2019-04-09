package com.brianku.project_b.votes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brianku.project_b.R
import kotlinx.android.synthetic.main.activity_submit_vote.*

class SubmitVoteActivity : AppCompatActivity() {

    private var minute:Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_vote)
        initComponents()
    }

    private fun initComponents(){
        val subject = intent.getStringExtra("Subject")
        val options = intent.getStringArrayExtra("Options")

        submit_subject_content_tv.text = subject
        submit_option_a_tv.text = options[0]
        submit_option_b_tv.text = options[1]
        submit_option_c_tv.text = options[2]
        submit_option_d_tv.text = options[3]

        submit_number_picker.value = minute
        submit_number_picker.setWrapSelectorWheel(true)
        submit_number_picker.setOnValueChangedListener { _, _, newVal ->
              if(newVal == 3){
                  minute = 10
              }else{
                  minute = newVal - 1
              }

        }

        submit_submit_btn.setOnClickListener {
            val intent = Intent(this,PinCodeAndTimerActivity::class.java)
            intent.putExtra("Subject",subject)
            intent.putExtra("Minute",minute)

            startActivity(intent)
        }
    }
}
