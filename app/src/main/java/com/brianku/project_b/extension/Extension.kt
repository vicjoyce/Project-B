package com.brianku.project_b.extension

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun getTimeString(milisecond:Long):String{
    val calendar = Calendar.getInstance()
    if(DateUtils.isToday(milisecond)){
        val formatter = SimpleDateFormat("hh:mm")
        calendar.timeInMillis = milisecond
        return formatter.format(calendar.time)
    }else{
        val formatter = SimpleDateFormat("MM-dd hh:mm")
        calendar.timeInMillis = milisecond
        return formatter.format(calendar.time)
    }
}