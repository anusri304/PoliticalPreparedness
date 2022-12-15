package com.example.android.politicalpreparedness.election.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class ElectionDateAdapter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @FromJson
    fun dateFromJson(dateStr: String): Date? {
        return dateFormat.parse(dateStr)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return dateFormat.format(date)
    }
}