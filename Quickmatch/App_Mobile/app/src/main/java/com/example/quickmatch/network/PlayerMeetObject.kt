package com.example.quickmatch.network

import com.squareup.moshi.Json

data class PlayerMeetObject(
    @Json(name = "meet") val meetId : Int,
    @Json(name = "player") val playerId : Int,
    @Json(name = "invitation") val invitationId: Int,
    val played : Boolean?,
    val status : Boolean?,
    val won : Boolean?,
    @Json(name = "precise_date") val date : String?,
    @Json(name = "start_hour") val start : String,
    @Json(name = "end_hour") val end : String,
    @Json(name = "repeat_day") val day : String?,
    val location : String?,
    @Json(name = "minimal_team_size") val minimum : Int?,
    @Json(name = "maximal_team_size") val maximum : Int?
)