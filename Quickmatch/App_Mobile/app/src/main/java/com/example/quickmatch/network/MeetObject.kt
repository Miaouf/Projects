package com.example.quickmatch.network

import com.squareup.moshi.Json

/* Meet data class */
data class MeetObject(
    val id : Int,
    val location : String?,
    @Json(name = "precise_date") val date : String?,
    @Json(name = "minimal_team_size") val minimum : Int?,
    @Json(name = "maximal_team_size") val maximum : Int?,
    @Json(name = "deletion_date") val date_deletion : String?,
    val played : Boolean?
)