package com.brianku.project_b.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.*

@IgnoreExtraProperties
data class Votes(var subject:String = "",
                 val voteId:String = "",
                 var ownerId:String = "",
                 var createDate:Date = Calendar.getInstance().time,
                 var options:MutableList<String> = mutableListOf()

) : Serializable {
    @Exclude
    fun toMap():Map<String,Any?>{
        return mapOf(
            "subject" to subject,
            "voteId" to voteId,
            "ownerId" to ownerId,
            "createDate" to createDate,
            "options" to options
        )
    }
}