package com.example.quickmatch.network

import com.squareup.moshi.Json
import java.util.*

/* PlayerClubs data class */
data class PlayerBelongClubObject(
    @Json(name = "player") val idPlayer : Int,
    @Json(name = "club") val idClub : Int,
    @Json(name = "is_admin") val admin : Boolean
)