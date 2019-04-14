package com.brianku.project_b.votes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brianku.project_b.R
import com.brianku.project_b.extension.hideKeyboard
import kotlinx.android.synthetic.main.activity_create_options.*

class CreateOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_options)

        val subject = intent.getStringExtra("Subject")
        create_options_subject_tv.text = "Subject: $subject"

        create_create_option_description_btn.setOnClickListener {

            var optionA = create_options_option_a_ev.text.toString().trim()
            var optionB = create_options_option_b_ev.text.toString().trim()
            var optionC = create_options_option_c_ev.text.toString().trim()
            var optionD = create_options_option_d_ev.text.toString().trim()

            if(optionA == "") optionA = "A"
            if(optionB == "") optionB = "B"
            if(optionC == "") optionC = "C"
            if(optionD == "") optionD = "D"



            create_options_option_a_ev.hideKeyboard()
            create_options_option_b_ev.hideKeyboard()
            create_options_option_c_ev.hideKeyboard()
            create_options_option_d_ev.hideKeyboard()

            val options = arrayOf(optionA,optionB,optionC,optionD)

            val intent = Intent(this,SubmitVoteActivity::class.java)
            intent.putExtra("Subject",subject)
            intent.putExtra("Options",options)

            startActivity(intent)
        }
    }
}
