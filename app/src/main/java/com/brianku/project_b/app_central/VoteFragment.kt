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
import com.brianku.project_b.dashboard.DashboardActivity
import com.brianku.project_b.model.User
import com.brianku.project_b.model.Votes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_vote.*
import net.steamcrafted.materialiconlib.MaterialIconView


class VoteFragment() : Fragment() {

    companion object {
        var mVoteId:String? = null
        var currentVote:Votes? = null
    }
    private lateinit var mDatabase:FirebaseDatabase
    private  var timer : CountDownTimer? = null
    private var answer:String = ""
    private lateinit var mParticipant:HashMap<String,Boolean>
    private var hasVoted:Boolean = false
    private var isTimeUp:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

// Fragment lifecycle
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

    override fun onStop() {
        super.onStop()
        if(timer != null) {
            timer?.cancel()
        }
    }


    // init the views

    private fun initComponents(){
        mDatabase.getReference("/Votes/$mVoteId")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val vote = dataSnapshot.getValue(Votes::class.java)
                    vote?.let{
                        if(vote_fragment_subject_tv != null) {
                            setupVoteInfo(it)
                        }
                        mDatabase.getReference("/Users/${it.ownerId}")
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {}

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val user = dataSnapshot.getValue(User::class.java)
                                    user?.let{
                                        if(vote_frament_subject_owner_imageview != null) {
                                            Picasso.get().load(it.thumbImage).into(vote_frament_subject_owner_imageview)
                                        }
                                    }
                                }
                            })
                    }
                }
            })

        fetchCurrentVote()

        vote_a_cardview.setOnClickListener{
            cardClicked(0)
        }
        vote_b_cardview.setOnClickListener{
            cardClicked(1)
        }
        vote_c_cardview.setOnClickListener {
            cardClicked(2)
        }
        vote_d_cardview.setOnClickListener {
            cardClicked(3)
        }
        vote_fragment_vote_btn.setOnClickListener {
            submitAnswer()
        }

    }


    private fun fetchCurrentVote(){
        if (mVoteId == null) return
        mDatabase.getReference("Votes/${VoteFragment.mVoteId}").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentVote = dataSnapshot.getValue(Votes::class.java) as Votes

            }

        })
    }

    private fun cardClicked (card:Int){

        val icons = listOf<Int>(R.id.add_photo_icon_a,R.id.add_photo_icon_b,R.id.add_photo_icon_c,R.id.add_photo_icon_d)
        for (icon in icons){
            val view = activity!!.findViewById<MaterialIconView>(icon)
            view.visibility = MaterialIconView.INVISIBLE
        }
        when(card) {
            0-> {
                add_photo_icon_a.visibility = MaterialIconView.VISIBLE
                answer = "A"
            }
            1 -> {
                add_photo_icon_b.visibility = MaterialIconView.VISIBLE
                answer = "B"
            }
            2 -> {
                add_photo_icon_c.visibility = MaterialIconView.VISIBLE
                answer = "C"
            }
            3-> {
                add_photo_icon_d.visibility = MaterialIconView.VISIBLE
                answer = "D"
            }
        }
    }

    private fun setupVoteInfo(vote:Votes){
        vote_fragment_subject_tv.text = vote.subject
        mParticipant = vote.participant
        val options = vote.options
        vote_fragment_option_a_tv.text = options["first"]
        vote_fragment_option_b_tv.text = options["second"]
        vote_fragment_option_c_tv.text = options["third"]
        vote_fragment_option_d_tv.text = options["forth"]

        val entryTimeStamp = System.currentTimeMillis() / 1000
        val minutesForMilllis = (vote.minutes.toLong() * 60 ) + vote.timestamp
        val elapseTime = minutesForMilllis - entryTimeStamp


         timer = object : CountDownTimer(elapseTime* 1000,1000) {
            override fun onFinish() {
                Toast.makeText(activity,"Time's Up",Toast.LENGTH_LONG).show()
                vote_fragment_flag_tv.text = "Time's Up"
                isTimeUp = true

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
        timer?.start()
    }

    private fun submitAnswer(){
        if(isTimeUp){
            Toast.makeText(activity,"Woop!!, The Vote Activity has been expired",Toast.LENGTH_LONG).show()
        }

        if(answer.isEmpty()) return
        val userId = DashboardActivity.currentUser?.userId
        userId?.let{
            if(mParticipant.containsKey(it)) {
                hasVoted = true
            }
          if(hasVoted){
              Toast.makeText(activity,"You already Vote...",Toast.LENGTH_LONG).show()
          }else{
              val reference = mDatabase.getReference("/Votes/$mVoteId")
              reference.child("participant").child(it).setValue(true)
                  .addOnSuccessListener {
                      Toast.makeText(activity,"Thanks for Your Voting",Toast.LENGTH_LONG).show()
                      hasVoted = true
                      val resultRef = reference.child("results").child("ans$answer")
                      resultRef.addListenerForSingleValueEvent(object :ValueEventListener{
                          override fun onCancelled(p0: DatabaseError) {}

                          override fun onDataChange(dataSnapshot: DataSnapshot) {
                              val oldValue = dataSnapshot.getValue(Int::class.java)
                              oldValue?.let{
                                  val newValue = oldValue + 1
                                  resultRef.setValue(newValue).addOnSuccessListener {
                                      Toast.makeText(activity,"Check the result?",Toast.LENGTH_LONG).show()
                                  }.addOnFailureListener{
                                      Log.d("vic","Error Message:${it.message}")
                                  }
                              }

                          }
                      })
                  }
             }
        }
    }
}
