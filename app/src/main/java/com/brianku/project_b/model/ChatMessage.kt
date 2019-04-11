package com.brianku.project_b.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.sql.Timestamp

@IgnoreExtraProperties
data class ChatMessage(val messageId:String = "",
                       val userId:String = "",
                       val text:String = "default",
                       var timestamp: Long = System.currentTimeMillis() / 1000
) : Serializable {

    @Exclude
    fun toMap():Map<String,Any?>{
        return mapOf(
            "messageId" to messageId,
            "userId" to userId,
            "text" to text,
            "timestamp" to timestamp
        )
    }
}