package com.brianku.project_b.model

import android.graphics.Color
import com.brianku.project_b.R
import com.brianku.project_b.extension.truncateWords
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.content_row_1.view.*
import kotlinx.android.synthetic.main.content_row_2.view.*
import kotlinx.android.synthetic.main.content_row_3.view.*
import kotlinx.android.synthetic.main.row_history_cell.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VotesItem(val vote:Votes): Item<ViewHolder>() {


    override fun getLayout(): Int {
        return R.layout.row_history_cell
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {


        bindView1(viewHolder)
        bindView2(viewHolder)
        bindView3(viewHolder)


        val pieChart = viewHolder.itemView.content_row_3_piechart
        setPie(pieChart)

        viewHolder.itemView.setOnClickListener {
            it.folding_cell.toggle(false)
        }
    }

    private fun bindView1(viewHolder: ViewHolder) {
        val subject = truncateWords(vote.subject,7,"...")
        viewHolder.itemView.cell_title_view.content_1_question_title_tv.text = subject
        viewHolder.itemView.content_1_question_title_tv.text = subject

        val dateFormatter = SimpleDateFormat("MM-dd HH:mm")
        val calender = Calendar.getInstance()
        calender.timeInMillis = vote.timestamp * 1000
        val dateString = dateFormatter.format(calender.time)

        viewHolder.itemView.content_1_create_time_tv.text = dateString
        viewHolder.itemView.cell_title_view.content_1_create_time_tv.text = dateString



        viewHolder.itemView.cell_title_view.content_1_vote_quantity_tv.text = vote.participant.size.toString()
        viewHolder.itemView.content_1_vote_quantity_tv.text = vote.participant.size.toString()

        viewHolder.itemView.cell_title_view.content_1_message_quantity_tv.text = vote.messages.size.toString()
        viewHolder.itemView.content_1_message_quantity_tv.text = vote.messages.size.toString()

    }

    private fun bindView2(viewHolder: ViewHolder) {

        val optionA = vote.options["first"] ?: ""
        val optionB = vote.options["second"] ?: ""
        val optionC = vote.options["third"] ?: ""
        val optionD = vote.options["forth"] ?: ""


        viewHolder.itemView.content_row_2_option_a_tv.text = "A: $optionA"
        viewHolder.itemView.content_row_2_option_b_tv.text = "B: $optionB"
        viewHolder.itemView.content_row_2_option_c_tv.text = "C: $optionC"
        viewHolder.itemView.content_row_2_option_d_tv.text = "D: $optionD"
        viewHolder.itemView.content_row_2_pincode_tv.text = "pincode:${vote.pinCode}"
    }

    private fun bindView3(viewHolder: ViewHolder) {
        val ansA = vote.results["ansA"] ?: 0
        val ansB = vote.results["ansB"] ?: 0
        val ansC = vote.results["ansC"] ?: 0
        val ansD = vote.results["ansD"] ?: 0

        viewHolder.itemView.content_row_3_result_a_tv.text = "A:$ansA"
        viewHolder.itemView.content_row_3_result_b_tv.text = "B:$ansB"
        viewHolder.itemView.content_row_3_result_c_tv.text = "C:$ansC"
        viewHolder.itemView.content_row_3_result_d_tv.text = "D:$ansD"

    }

    private fun setPie(pieChart: PieChart) {

        val ansA = vote.results["ansA"] ?: 0
        val ansB = vote.results["ansB"] ?: 0
        val ansC = vote.results["ansC"] ?: 0
        val ansD = vote.results["ansD"] ?: 0
        // pie attributes setting
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false


        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.transparentCircleRadius = 20f
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(15f)

        // set up the entry data

        var yValues: ArrayList<PieEntry> = arrayListOf(
            PieEntry(ansA.toFloat(), "A"),
            PieEntry(ansB.toFloat(), "B"),
            PieEntry(ansC.toFloat(), "C"),
            PieEntry(ansD.toFloat(), "D")
        )

        // create pie datasets
        val pieDataset = PieDataSet(yValues, "")

        pieDataset.sliceSpace = 3f
        pieDataset.selectionShift = 5f
        pieDataset.colors = ColorTemplate.JOYFUL_COLORS.asList()


        // setting individual data
        val data = PieData(pieDataset)

        data.setValueTextSize(20f)
        data.setValueTextColor(Color.WHITE)

        // assign data to pie
        pieChart.data = data

    }

}