package com.example.quickmatch.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/* Player created by parsing data (moshi) in the JSON response */
@JsonClass(generateAdapter = true)
@Parcelize /* To pass a PlayerObject between activities */
data class PlayerObject(
        val id : Int?,
        val surname : String,
        @Json(name = "first_name") val firstName : String,
        val pseudo : String,
        @Json(name = "mdp") var password : String?,
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
) : Parcelable

