package com.brianku.project_b.model

import com.brianku.project_b.R
import com.brianku.project_b.extension.getTimeString
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatToItem(val text: String,val displayName:String,val timeStamp:Long): Item<ViewHolder>(){
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