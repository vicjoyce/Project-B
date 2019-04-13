package com.brianku.project_b.model

import com.brianku.project_b.R
import com.brianku.project_b.extension.getTimeString
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*

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
