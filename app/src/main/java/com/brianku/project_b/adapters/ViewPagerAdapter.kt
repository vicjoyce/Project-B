package com.brianku.project_b.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brianku.project_b.R
import com.brianku.project_b.model.TrainPage
import kotlinx.android.synthetic.main.viewpager_content.view.*


class ViewPagerAdapter(val listOfTrainPage:MutableList<TrainPage>,val context: Context): PagerAdapter(){
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int = listOfTrainPage.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val pageView = layoutInflater.inflate(R.layout.viewpager_content,container,false)
        container.addView(pageView)

        val trainPage = listOfTrainPage[position]
        pageView.viewpager_title_tv.text = trainPage.title
        pageView.viewpager_description_tv.text = trainPage.descript
        pageView.viewpager_image_imageview.setImageResource(trainPage.picture)

        return pageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {

        container.removeView(view as View)
    }

}