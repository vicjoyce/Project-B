package com.brianku.project_b.app_central


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brianku.project_b.R
import com.brianku.project_b.model.Results
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_result.*


class ResultFragment : Fragment() {

    private lateinit var mDatabase: FirebaseDatabase
    private var pieSource:List<Int> = listOf(0,0,0,0)
    private val answersList = listOf("A","B","C","D")
    private var subject: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance()

        mDatabase.getReference("/Votes/${VoteFragment.mVoteId}")
            .child("results").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val results = dataSnapshot.getValue(Results::class.java)
                    results?.let{
                        pieSource = listOf<Int>(it.ansA,it.ansB,it.ansC,it.ansD)
                        subject = VoteFragment.currentVote?.subject ?: ""
                        for ( data in pieSource) Log.d("vic","$data")
                        if(result_result_a_tv != null && result_result_b_tv != null &&
                                result_result_c_tv != null &&
                                result_result_d_tv != null && result_piechart != null ){
                            setViewToMatchData()
                            setPie()
                        }

                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onStart() {
        super.onStart()
        setViewToMatchData()
        setPie()

    }

    private fun setViewToMatchData(){
        result_result_a_tv.text = pieSource[0].toString()
        result_result_b_tv.text = pieSource[1].toString()
        result_result_c_tv.text = pieSource[2].toString()
        result_result_d_tv.text = pieSource[3].toString()
    }

    private fun setPie(){
        // pie attributes setting
        result_piechart.setUsePercentValues(true)
        result_piechart.description.isEnabled = false
        result_piechart.setExtraOffsets(5f,10f,5f,5f)

        result_piechart.dragDecelerationFrictionCoef = 0.95f
        result_piechart.isDrawHoleEnabled = true
        result_piechart.setHoleColor(Color.WHITE)
        result_piechart.transparentCircleRadius = 61f
        result_piechart.setEntryLabelColor(Color.WHITE)
        result_piechart.setEntryLabelTextSize(15f)

        // set up the entry data

        var yValues: ArrayList<PieEntry> = arrayListOf()

        for(i in 0..pieSource.size - 1 ){
            yValues.add(PieEntry(pieSource[i].toFloat(),answersList[i]))
        }


        result_piechart.animateY(1000,Easing.EasingOption.EaseInCubic)


        // create pie datasets
        val pieDataset = PieDataSet(yValues,subject)

        pieDataset.sliceSpace = 3f
        pieDataset.selectionShift = 5f
        pieDataset.colors = ColorTemplate.JOYFUL_COLORS.asList()


        // setting individual data
        val data = PieData(pieDataset)

        data.setValueTextSize(20f)
        data.setValueTextColor(Color.WHITE)

        // assign data to pie
        result_piechart.data = data

    }
}
