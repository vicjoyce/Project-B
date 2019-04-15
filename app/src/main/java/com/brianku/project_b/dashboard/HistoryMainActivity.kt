package com.brianku.project_b.dashboard


import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.brianku.project_b.R
import com.brianku.project_b.model.Votes
import com.brianku.project_b.model.VotesItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_history_main.*


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
                    val voteId = dataPositions.removeAt(viewHolder.adapterPosition)
                    mDatabase.getReference("/Users/${DashboardActivity.currentUser!!.userId}/history/$voteId")
                        .removeValue()
                        .addOnSuccessListener {
                            mDatabase.getReference("/Votes/$voteId").removeValue()
                        }.addOnCompleteListener {
                            if(it.isSuccessful)  {
                                groupAdapter.removeGroup(viewHolder.adapterPosition)
                            }
                        }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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


