package com.brianku.project_b.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.sql.Timestamp
import java.util.*

@IgnoreExtraProperties
data class Votes(val subject:String = "",
                 val voteId:String = "",
                 val ownerId:String = "",
                 val timestamp: Long = System.currentTimeMillis() / 1000,
                 val minutes:Int = 0,
                 val pinCode:String = "",
                 var options:HashMap<String,String> = hashMapOf<String,String>(),
                 var results:HashMap<String,Int> = hashMapOf<String,Int>(),
                 var participant:HashMap<String,Boolean> = hashMapOf<String,Boolean>()

) : Serializable {
    @Exclude
    fun toMap():Map<String,Any?>{
        return mapOf(
            "subject" to subject,
            "voteId" to voteId,
            "ownerId" to ownerId,
            "timestamp" to timestamp,
            "options" to options
        )
    }
}