package com.brianku.project_b.app_central


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brianku.project_b.R
import com.brianku.project_b.model.Votes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_vote.*


class VoteFragment() : Fragment() {

    companion object {
        var mVoteId:String? = null
    }
    private lateinit var mDatabase:FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDatabase = FirebaseDatabase.getInstance()


        return inflater.inflate(R.layout.fragment_vote, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val voteId = arguments?.getString("VoteId")
        voteId?.let{
            mVoteId = it
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initComponents()
    }


    private fun initComponents(){
        mDatabase.getReference("/Votes/$mVoteId")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val vote = dataSnapshot.getValue(Votes::class.java)
                    vote?.let{
                        vote_fragment_subject_tv.text = it.subject
                    }
                }

            })
    }

}
