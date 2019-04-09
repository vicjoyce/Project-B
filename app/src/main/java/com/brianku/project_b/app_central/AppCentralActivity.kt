package com.brianku.project_b.app_central

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.widget.Toast
import com.brianku.project_b.R
import kotlinx.android.synthetic.main.activity_app_central.*

class AppCentralActivity : AppCompatActivity() {

    private val mOnNavigationItemListIntent = BottomNavigationView.OnNavigationItemSelectedListener{
        when(it.itemId){
            R.id.vote ->{
                Toast.makeText(this@AppCentralActivity,"vote is clicked",Toast.LENGTH_LONG).show()
                replaceFragment(VoteFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.chat ->{
                Toast.makeText(this@AppCentralActivity,"chat is clicked",Toast.LENGTH_LONG).show()
                replaceFragment(ChatFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.result->{
                Toast.makeText(this@AppCentralActivity,"result is clicked",Toast.LENGTH_LONG).show()
                replaceFragment(ResultFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_central)
        bottom_navigation_bar.setOnNavigationItemSelectedListener(mOnNavigationItemListIntent)
        replaceFragment(VoteFragment())
    }

    private fun replaceFragment(fragment:Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()

    }
}
