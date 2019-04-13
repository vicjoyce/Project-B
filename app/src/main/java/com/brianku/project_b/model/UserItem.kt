package com.brianku.project_b.model

import com.brianku.project_b.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.dashboard_row_item.view.*

class UserItem(val item:DashboardItem): Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.dashboard_row_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.dashboard_row_item_textview.text = item.text
        viewHolder.itemView.dashboard_row_item_imageview.setImageResource(item.imageId)
    }
}