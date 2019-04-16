package com.brianku.project_b.dashboard


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
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
    private lateinit var deleteIcon:Drawable
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_main)

        mDatabase = FirebaseDatabase.getInstance()
        deleteIcon = ContextCompat.getDrawable(this,R.drawable.ic_delete_forever_black_24dp)!!
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
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                mDatabase.getReference("/Votes/$voteId").addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {}
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val vote = dataSnapshot.getValue(Votes::class.java) as Votes
                                        val pinCode = vote.pinCode
                                        mDatabase.getReference("/PinCodes/$pinCode").removeValue().addOnCompleteListener {
                                            if(it.isSuccessful)  mDatabase.getReference("/Votes/$voteId").removeValue()
                                                .addOnCompleteListener{
                                                    if(it.isSuccessful) groupAdapter.removeGroup(viewHolder.adapterPosition)
                                                }
                                        }
                                    }
                                })
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

                    val itemView = viewHolder.itemView
                    val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) /2

                    if( dX > 0){
                        swipeBackground.setBounds(itemView.left ,itemView.top,dX.toInt() + 24,itemView.bottom)
                        deleteIcon.setBounds(itemView.left - 64  + iconMargin,itemView.top + iconMargin,itemView.left + iconMargin + deleteIcon.intrinsicWidth - 64, itemView.bottom - iconMargin)

                    }else{
                        swipeBackground.setBounds(itemView.right + dX.toInt(),itemView.top,itemView.right,itemView.bottom)
                        deleteIcon.setBounds(itemView.right - iconMargin - deleteIcon.intrinsicWidth + 64, itemView.top + iconMargin,itemView.right - iconMargin + 64,itemView.bottom - iconMargin )
                    }


                    swipeBackground.draw(c)
                    deleteIcon.draw(c)

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


