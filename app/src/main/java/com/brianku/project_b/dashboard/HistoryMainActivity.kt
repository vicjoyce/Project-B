package com.brianku.project_b.dashboard

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import bolts.Bolts
import com.brianku.project_b.R
import com.brianku.project_b.extension.truncateWords
import com.brianku.project_b.model.Votes
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_history_main.*
import kotlinx.android.synthetic.main.content_row_1.view.*
import kotlinx.android.synthetic.main.content_row_2.view.*
import kotlinx.android.synthetic.main.content_row_3.view.*
import kotlinx.android.synthetic.main.fragment_result.*
import kotlinx.android.synthetic.main.row_history_cell.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList

class HistoryMainActivity : AppCompatActivity() {

    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var groupAdapter:GroupAdapter<ViewHolder>
    private var dataPositions:MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_main)

        mDatabase = FirebaseDatabase.getInstance()

        groupAdapter = GroupAdapter<ViewHolder>()

        fetchHistory()
        history_recyclerview.adapter = groupAdapter

        setupItemTouchHelper()
    }

    private fun setupItemTouchHelper(){
        history_recyclerview.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        val itemTouchHelperCallback =
            object : ItemTouchHelper
            .SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean { return false }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                    val item = groupAdapter.getItem(viewHolder.adapterPosition)
                    val voteId = dataPositions.removeAt(viewHolder.adapterPosition)
                    Section().remove(item)
                    mDatabase.getReference("/Users/${DashboardActivity.currentUser!!.userId}/history/$voteId")
                        .removeValue()
                        .addOnSuccessListener {
                            mDatabase.getReference("/Votes/$voteId").removeValue()
                        }
                    groupAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(history_recyclerview)




    }

    private fun fetchHistory(){
        if(DashboardActivity.currentUser == null) return
        mDatabase.getReference("/Users/${DashboardActivity.currentUser!!.userId}/history")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        for( data in dataSnapshot.children){
                            val voteId = data.key
                            voteId?.let{
                                fetchVotes(it)
                            }
                        }
                    }
                }

            })
    }

    private fun fetchVotes(voteId:String){

        mDatabase.getReference("/Votes/$voteId").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val vote = dataSnapshot.getValue(Votes::class.java) as Votes
                groupAdapter.add(VotesItem(vote))
                dataPositions.add(vote.voteId)
            }
        })
    }
}



data class History(val voteId:String = "",val isExist:Boolean = true)


class VotesItem(val vote:Votes):Item<ViewHolder>() {


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