package com.brianku.project_b.app_central


import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.brianku.project_b.R
import com.brianku.project_b.model.User
import com.brianku.project_b.model.Votes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_vote.*
import net.steamcrafted.materialiconlib.MaterialIconView


class VoteFragment() : Fragment() {

    companion object {
        var mVoteId:String? = null
    }
    private lateinit var mDatabase:FirebaseDatabase
    private lateinit var timer: CountDownTimer



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

    override fun onStart() {
        super.onStart()
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

        vote_a_cardview.setOnClickListener{
           add_photo_icon_a.visibility = MaterialIconView.VISIBLE
        }
    }

    fun cardClicked (view: View){

    }



    private fun setupVoteInfo(vote:Votes){
        vote_fragment_subject_tv.text = vote.subject
        val options = vote.options
        vote_fragment_option_a_tv.text = options["first"]
        vote_fragment_option_b_tv.text = options["second"]
        vote_fragment_option_c_tv.text = options["third"]
        vote_fragment_option_d_tv.text = options["forth"]

        val entryTimeStamp = System.currentTimeMillis() / 1000
        val minutesForMilllis = (vote.minutes.toLong() * 60 ) + vote.timestamp
        val elapseTime = minutesForMilllis - entryTimeStamp

        Log.d("vic","when user entry:$entryTimeStamp")
        Log.d("vic","time in database:${vote.timestamp}")
        Log.d("vic","expected database + minutes: $minutesForMilllis")
        Log.d("vic","elapsedTime: $elapseTime")

         timer = object : CountDownTimer(elapseTime* 1000,1000) {
            override fun onFinish() {
                Toast.makeText(activity,"Times Up",Toast.LENGTH_LONG).show()
            }

            override fun onTick(millisUntilFinished: Long) {
                val realSeconds = millisUntilFinished / 1000
                val mins = realSeconds / 60
                var seconds = (realSeconds % 60).toString()
                if(seconds.length == 1) {
                    seconds = "0$seconds"
                }

                vote_fragment_timer_tv.text = "$mins : $seconds"

            }
        }
        timer.start()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

}
