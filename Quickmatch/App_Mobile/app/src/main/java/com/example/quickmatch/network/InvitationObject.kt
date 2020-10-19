package com.example.quickmatch.network

import com.squareup.moshi.Json


/* Club data class */
data class InvitationObject(
    val id : Int,
    @Json(name = "slot_id") val slot : Int,
    @Json(name = "player_id") val player : Int,
    @Json(name = "meet_id") val meet : Int,
    @Json(name = "status") val status : Boolean?,
    @Json(name = "event_type") val event_type : String
)