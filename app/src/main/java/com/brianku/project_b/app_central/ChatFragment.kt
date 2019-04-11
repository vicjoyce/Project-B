package com.brianku.project_b.app_central


import android.os.Bundle

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.brianku.project_b.R
import com.brianku.project_b.dashboard.DashboardActivity
import com.brianku.project_b.extension.getTimeString
import com.brianku.project_b.model.ChatMessage
import com.brianku.project_b.model.User
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var groupAdapter:GroupAdapter<ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance()
        groupAdapter = GroupAdapter<ViewHolder>()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_recyclerview) as RecyclerView
        listenFromMessage()
        recyclerView.adapter = groupAdapter

        return view
    }

    override fun onStart() {
        super.onStart()

        chat_send_btn.setOnClickListener {
            sendMessageAndPost()
        }

    }

    private fun listenFromMessage(){
        val reference = mDatabase.getReference("/Votes/${VoteFragment.mVoteId}").child("messages")
        reference.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val chatMessage = dataSnapshot.getValue(ChatMessage ::class.java) as ChatMessage
                mDatabase.getReference("/Users/${chatMessage.userId}")
                    .addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(dataSnapshot : DataSnapshot) {
                            val user = dataSnapshot.getValue(User::class.java) as User
                            if(user.userId == DashboardActivity.currentUser!!.userId){
                                groupAdapter.add(ChatToItem(chatMessage.text,user.displayName,chatMessage.timestamp))
                            }else{
                                groupAdapter.add(ChatFromItem(chatMessage.text,user.displayName,user.thumbImage,chatMessage.timestamp))
                            }
                        }

                    })
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }

    private fun sendMessageAndPost(){
        val text = chat_message_et.text.toString().trim()
        if(text.isEmpty()) return


        val reference = mDatabase.getReference("/Votes/${VoteFragment.mVoteId}").child("messages").push()
        val chatMessage = ChatMessage(reference.key!!,DashboardActivity.currentUser!!.userId,text)
        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d("vic","${chatMessage.toString()}")
        }
    }
}


class ChatFromItem(val text:String,val displayName:String, val userImageUrl:String,val timeStamp: Long): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.row_from_message_tv.text = text
        Picasso.get().load(userImageUrl).into(viewHolder.itemView.row_circleImageView)
        val milisecond = timeStamp * 1000

        val timeString = getTimeString(milisecond)
        viewHolder.itemView.row__from_username_time_tv.text = "$displayName, $timeString"


    }

}



class ChatToItem(val text: String,val displayName:String,val timeStamp:Long):Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.row_to_message_tv.text = text
        val milisecond = timeStamp * 1000
        val timeString = getTimeString(milisecond)
        viewHolder.itemView.row_to_username_time_tv.text = "$displayName, $timeString"
    }

}