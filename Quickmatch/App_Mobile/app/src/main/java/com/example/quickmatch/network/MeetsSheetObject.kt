package com.example.quickmatch.network

import com.squareup.moshi.Json
import java.util.*

/* Meet created by parsing data (moshi) in the JSON response */
data class MeetsSheetObject(
    @Json(name = "player") val idPlayer : Int,
    @Json(name = "meet") val idMeet : Int,
    @Json(name = "scored_goals") val scoredGoals : Int,
    @Json(name = "conceded_goals") val concededGoals : Int,
    @Json(name = "won") val victory : Boolean
)