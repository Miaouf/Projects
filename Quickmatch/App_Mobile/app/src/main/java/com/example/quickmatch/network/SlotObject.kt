package com.example.quickmatch.network

import com.squareup.moshi.Json
import java.util.*

/* Club data class */
data class SlotObject(
    val id : Int,
    @Json(name = "start_hour") val start : String,
    @Json(name = "end_hour") val end : String,
    @Json(name = "repeat_day") val day : String
)