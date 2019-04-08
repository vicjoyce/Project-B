package com.brianku.project_b.dashboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brianku.project_b.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        about_go_to_official_site_btn.setOnClickListener {
            startActivity(Intent(this,OfficialWebSiteActivity::class.java))
        }
    }
}
