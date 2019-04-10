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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_result.*


class ResultFragment : Fragment() {

    private lateinit var mDatabase: FirebaseDatabase
    private var pieSource:List<Int> = listOf(0,0,0,0)

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
                        for ( data in pieSource) Log.d("vic","$data")
                        Log.d("vic","Listener onDataChange is invoked")
                        if(result_result_a_tv != null && result_result_b_tv != null &&
                                result_result_c_tv != null && result_result_d_tv != null){
                            setViewToMatchData()
                        }
                    }
                }
            })
        Log.d("vic","onCreate is invoked")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("vic","onCreateView is invoked")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onStart() {
        super.onStart()
        setViewToMatchData()
        Log.d("vic","onStart is invoked")

    }

    private fun setViewToMatchData(){
        result_result_a_tv.text = pieSource[0].toString()
        result_result_b_tv.text = pieSource[1].toString()
        result_result_c_tv.text = pieSource[2].toString()
        result_result_d_tv.text = pieSource[3].toString()
    }
}
