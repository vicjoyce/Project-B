package com.brianku.project_b.votes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brianku.project_b.R
import com.google.firebase.database.FirebaseDatabase

class PinCodeAndTimerActivity : AppCompatActivity() {


    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var randomString:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code_and_timer)

        val charPool : List<Char> =  ('A'..'Z') + ('0'..'9')
        randomString = (1..4)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
    }
}
