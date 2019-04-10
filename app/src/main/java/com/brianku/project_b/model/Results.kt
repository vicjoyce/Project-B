package com.brianku.project_b.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Results(var ansA:Int = 0,
                   var ansB:Int = 0,
                   var ansC:Int = 0,
                   var ansD:Int = 0


) : Serializable {

    @Exclude
    fun toMap():Map<String,Any?>{
        return mapOf(
            "ansA" to ansA,
            "ansB" to ansB,
            "ansC" to ansC,
            "ansD" to ansD
        )
    }
}