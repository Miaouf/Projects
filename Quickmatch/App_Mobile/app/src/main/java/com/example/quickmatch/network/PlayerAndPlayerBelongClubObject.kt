package com.example.quickmatch.network

import com.squareup.moshi.Json

data class PlayerAndPlayerBelongClubObject(
    @Json(name = "player") val idPlayer : Int,
    @Json(name = "club") val idClub : Int,
    @Json(name = "is_admin") val admin : Boolean,
    @Json(name = "inscription_date") val inscriptionDate : String,
    @Json(name = "scored_goals_club") val scoredGoalsClub : Int,
    @Json(name = "conceded_goals_club") val concededGoalsClub : Int,
    @Json(name = "matches_played_club") val matchesPlayedClub : Int,
    @Json(name = "victories_club") val victoriesClub : Int,
    val id : Int?,
    val surname : String,
    @Json(name = "first_name") val firstName : String,
    val pseudo : String,
    @Json(name = "mdp") var password : String,
    @Json(name = "mail_address") val mailAddress : String,
    @Json(name = "phone_number") val phoneNumber : String?,
    @Json(name = "scored_goals") val scoredGoals : Int,
    @Json(name = "conceded_goals") val concededGoals : Int,
    @Json(name = "matches_played") val matchesPlayed : Int,
    val victories : Int,
    val avatar : String?,
    val bio : String?,
    @Json(name = "is_valid") val isValid : Boolean?,
    @Json(name = "private_profil") val isPrivate : Boolean?
)