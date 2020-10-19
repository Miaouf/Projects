package com.example.quickmatch.network

import com.squareup.moshi.Json
import java.util.*

/* Club data class */
data class ClubObject(
    val id : Int?,
    @Json(name = "club_name") val name : String,
    @Json(name = "creation_date") val creationDate : String?,
    @Json(name = "private_club") val private : Boolean
)