package com.brianku.project_b.app_central


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brianku.project_b.R
import com.brianku.project_b.model.User
import com.brianku.project_b.model.Votes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
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
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val vote = dataSnapshot.getValue(Votes::class.java)
                    vote?.let{
                        setupVoteInfo(it)

                        mDatabase.getReference("/Users/${it.ownerId}")
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {}

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val user = dataSnapshot.getValue(User::class.java)
                                    user?.let{
                                        Picasso.get().load(it.thumbImage).into(vote_frament_subject_owner_imageview)
                                    }
                                }
                            })
                    }
                }
            })
    }


    private fun setupVoteInfo(vote:Votes){
        vote_fragment_subject_tv.text = vote.subject
        vote_fragment_timer_tv.text = vote.timestamp.toString()
        val options = vote.options
        vote_fragment_option_a_tv.text = options["first"]
        vote_fragment_option_b_tv.text = options["second"]
        vote_fragment_option_c_tv.text = options["third"]
        vote_fragment_option_d_tv.text = options["forth"]
    }

}
