package com.brianku.project_b.dashboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brianku.project_b.R
import kotlinx.android.synthetic.main.activity_official_web_site.*

class OfficialWebSiteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_official_web_site)
        official_webview.loadUrl("https://www.apple.com")
    }
}
