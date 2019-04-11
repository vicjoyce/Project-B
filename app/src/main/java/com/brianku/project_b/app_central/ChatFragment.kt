package com.brianku.project_b.app_central


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brianku.project_b.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

//        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_recyclerview) as RecyclerView
//        val adapter = GroupAdapter<ViewHolder>()
//
//        adapter.add(ChatItem())
//        adapter.add(ChatItem())
//        adapter.add(ChatItem())
//        adapter.add(ChatItem())
//
//        recyclerView.adapter = adapter

        return view
    }




}


class ChatItem: Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

}