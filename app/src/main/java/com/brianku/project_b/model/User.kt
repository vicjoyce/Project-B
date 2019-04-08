package com.brianku.project_b.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(var displayName:String = "",
                val userId:String = "",
                var email:String = "",
                var image:String = "default",
                var thumbImage:String = "default",
                var history:MutableList<String> = mutableListOf()

) : Serializable {
    @Exclude
    fun toMap():Map<String,Any?>{
        return mapOf(
            "displayName" to displayName,
            "userId" to userId,
            "email" to email,
            "image" to image,
            "thumbImage" to thumbImage,
            "history" to history
        )
    }
}