package com.brianku.project_b.votes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.brianku.project_b.R
import com.brianku.project_b.extension.hideKeyboard
import kotlinx.android.synthetic.main.activity_create_vote.*

class CreateVoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_vote)

        create_create_subject_btn.setOnClickListener {
            val subject = create_subject_et.text.toString().trim()
            if(subject.isEmpty()) {
                Toast.makeText(this,"Subject can't be empty",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            create_subject_et.hideKeyboard()
            val intent = Intent(this, CreateOptionsActivity::class.java)
            intent.putExtra("Subject",subject)
            startActivity(intent)

        }
    }
}
