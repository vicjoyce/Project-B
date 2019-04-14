package com.brianku.project_b.dashboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brianku.project_b.R
import com.brianku.project_b.app_central.AppCentralActivity
import com.brianku.project_b.model.DashboardItem
import com.brianku.project_b.model.User
import com.brianku.project_b.model.UserItem
import com.brianku.project_b.user_login_and_register.LoginScreenActivity
import com.brianku.project_b.votes.CreateVoteActivity
import com.brianku.project_b.votes.HandlePinCodeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dashboard_row_item.view.*
import java.lang.Exception

class DashboardActivity : AppCompatActivity() {
    companion object {
        var currentUser : User? = null
    }

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        mAuth = FirebaseAuth.getInstance()
        fetchCurrentUser()
        setupRecyclerRow()
        dashboard_create_vote_btn.setOnClickListener{
            startActivity(Intent(this,CreateVoteActivity::class.java))
        }
        dashboard_go_to_vote_btn.setOnClickListener {
            startActivity(Intent(this,HandlePinCodeActivity::class.java))
        }
    }

    private fun fetchCurrentUser(){
        val uid = mAuth.uid
        var ref = FirebaseDatabase.getInstance().getReference("Users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               currentUser = dataSnapshot.getValue(User::class.java)
                setupPicture()
            }
        })
    }


    private fun setupPicture(){
        currentUser?.let{

            Picasso.get().load(it.image).into(dashboard_circleView)
            dashboard_username_tv.text = it.displayName
        }
    }


    private fun setupRecyclerRow(){
        val images = listOf(R.drawable.icon28_06_history
            ,R.drawable.icon28_05_about
            ,R.drawable.icon28_07_modify
            ,R.drawable.icon28_04_logout
        )
        val texts = listOf<String>("Vote History","About Us","Modify Profile","Sign Out")
        val groupAdapter = GroupAdapter<ViewHolder>()

        for( i in 0..3) groupAdapter.add(UserItem(DashboardItem(images[i],texts[i],i)))

        groupAdapter.setOnItemClickListener{
            item, view ->
            val userItem = item as UserItem

            when(userItem.item.order){
                0 ->{
                    goToHistory()
                }
                1 ->{
                    goToAbout()
                }
                2 ->{
                    goToModify()
                }
                3->{
                    signOut()
                }
            }
        }

        dashboard_recyclerview.adapter = groupAdapter

    }

    private fun goToAbout(){
        startActivity(Intent(this,AboutActivity::class.java))

    }

    private fun goToModify(){
        val intent = Intent(this,ModifyActivity::class.java)
        intent.putExtra("User", currentUser)
        startActivity(intent)

    }

    private fun goToHistory(){
        if(currentUser == null) return
        val intent = Intent(this,HistoryMainActivity::class.java)
        intent.putExtra("CurrentUser", currentUser)
        startActivity(intent)
    }

    private fun signOut(){
        mAuth.signOut()
        val intent = Intent(this,LoginScreenActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}


