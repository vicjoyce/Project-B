package com.brianku.project_b.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Options(var first:String = "A",
                val second:String = "B",
                var third:String = "C",
                var forth:String = "D"


) : Serializable {

    @Exclude
    fun toMap():Map<String,Any?>{
        return mapOf(
            "first" to first,
            "second" to second,
            "third" to third,
            "forth" to forth
        )
    }
}
