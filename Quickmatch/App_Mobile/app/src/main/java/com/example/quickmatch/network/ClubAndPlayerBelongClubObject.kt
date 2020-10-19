package com.example.quickmatch.network

import com.squareup.moshi.Json

data class ClubAndPlayerBelongClubObject(
    @Json(name = "player") val idPlayer : Int,
    @Json(name = "club") val idClub : Int,
    @Json(name = "is_admin") val admin : Boolean,
    @Json(name = "inscription_date") val inscriptionDate : String,
    @Json(name = "scored_goals_club") val scoredGoals : Int,
    @Json(name = "conceded_goals_club") val concededGoals : Int,
    @Json(name = "matches_played_club") val matchesPlayed : Int,
    @Json(name = "victories_club") val victories : Int,
    val id : Int?,
    @Json(name = "club_name") val name : String,
    @Json(name = "creation_date") val creationDate : String?,
    @Json(name = "private_club") val private : Boolean
)