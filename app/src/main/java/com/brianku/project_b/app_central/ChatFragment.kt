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
import com.brianku.project_b.extension.hideKeyboard
import com.brianku.project_b.model.*
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var groupAdapter:GroupAdapter<ViewHolder>
    private var itemSize = 0
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
        groupAdapter.registerAdapterDataObserver(
            object :RecyclerView.AdapterDataObserver(){
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    recyclerView.layoutManager!!.smoothScrollToPosition(recyclerView,null,groupAdapter.getItemCount())
                }
            }
        )
        recyclerView.adapter = groupAdapter

        return view
    }

    override fun onStart() {
        super.onStart()
        chat_send_btn.setOnClickListener {
            sendMessageAndPost()
        }

        val user = DashboardActivity.currentUser
        user?.let{
            Picasso.get().load(it.thumbImage).into(chat_owner_imageview)
        }
        chat_entry_pincode_tv.text = VoteFragment.currentVote?.pinCode ?: ""
    }



    private fun listenFromMessage(){
        val reference = mDatabase.getReference("/Votes/${VoteFragment.mVoteId}").child("messages")
        reference.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val chatMessage = dataSnapshot.getValue(ChatMessage ::class.java) as ChatMessage
                if(DashboardActivity.currentUser == null) return
                if(chatMessage.userId == DashboardActivity.currentUser!!.userId) {
                    groupAdapter.add(ChatToItem(chatMessage.text,chatMessage.displayName,chatMessage.timestamp))
                }else {
                    groupAdapter.add(ChatFromItem(chatMessage.text,chatMessage.displayName,chatMessage.thumbImage,chatMessage.timestamp))
                }
                itemSize = dataSnapshot.childrenCount.toInt()
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

        chat_message_et.setText("")
        chat_message_et.hideKeyboard()


        val reference = mDatabase.getReference("/Votes/${VoteFragment.mVoteId}").child("messages").push()
        val user = DashboardActivity.currentUser
        if (user == null) return
        val chatMessage = ChatMessage(reference.key!!,user.userId,text,user.displayName,user.thumbImage)
        reference.setValue(chatMessage)
    }
}
