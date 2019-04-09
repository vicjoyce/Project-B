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
                 val pinCode:String = "",
                 var options:MutableList<String> = mutableListOf()

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