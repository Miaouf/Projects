package com.example.quickmatch.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/* HTTP requests*/
/* TODO: changer les requêtes en HTTPS (afin de fit avec la version web */

/* URL of the private database where data is retrieved */
private const val BASE_URL = "https://dbcontrol.quickmatch.fr/"

/* Create Moshi object which will parse the responses */
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/* Retrofit builder with converter for response and base url */
private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()


/* API to communicate with the backend */
interface DatabaseApiService {
    @GET("dbcontrol")
    suspend fun testConnection() : TestObject

    /* PLAYERS */
    /* GET REQUESTS */
    @GET("dbcontrol/api/v1/PlayersRows")
    suspend fun getAllPlayers() : List<PlayerObject>
    @GET("dbcontrol/api/v1/Players/ma{mail_address}")
    suspend fun getPlayerByMail(@Path("mail_address") mailAddress : String) : PlayerObject
    @GET("dbcontrol/api/v1/Players/p{p}")
    suspend fun getPlayerByPseudo(@Path("p") pseudo : String) : PlayerObject
    @GET("dbcontrol/api/v1/Players/n{phone_number}")
    suspend fun getPlayerByPhoneNumber(@Path("phone_number") phoneNumber : String) : PlayerObject
    @GET("dbcontrol/api/v1/Players/id{id}")
    suspend fun getPlayerById(@Path("id") id : Int) : PlayerObject
    @GET("dbcontrol/api/v1/Players/stat{id}")
    suspend fun getPlayerMeetSheetsById(@Path("id") id : Int) : List<MeetsSheetObject>

    /* POST REQUESTS */
    @POST("dbcontrol/api/v1/Players")
    suspend fun addPlayer(@Body player: PlayerObject) : PlayerObject

    /* PUT REQUESTS */
    @PUT("dbcontrol/api/v1/Players/id{id}")
    suspend fun updatePlayerById(@Path("id") id : Int, @Body player: PlayerObject) : PlayerObject

    /* DELETE REQUESTS */
    @DELETE("dbcontrol/api/v1/Players/{mail_address}")
    suspend fun deletePlayerByMail(@Path("mail_address") mailAddress : String)

    /* CLUBS */
    /* GET REQUESTS */
    @GET("dbcontrol/api/v1/ClubsRows")
    suspend fun getAllClubs() : List<ClubObject>
    @GET("dbcontrol/api/v1/Clubs/{id}")
    suspend fun getClubById(@Path("id") id : Int) : ClubObject

    /* DELETE REQUESTS */
    @DELETE("dbcontrol/api/v1/Clubs/{id}")
    suspend fun deleteClubById(@Path("id") id : Int)

    /* POST REQUESTS */
    @POST("dbcontrol/api/v1/Clubs")
    suspend fun addClub(@Body clubObject: ClubObject)

    /* INVITATIONS */
    /* GET REQUESTS */
    @GET("dbcontrol/api/v1/InvitationsRows")
    suspend fun getAllInvitations() : List<InvitationObject>
    @GET("dbcontrol/api/v1/Invitations/{id}")
    suspend fun getInvitationById(@Path("id") id : Int) : InvitationObject

    /* POST REQUESTS */
    @POST("dbcontrol/api/v1/Invitations")
    suspend fun createInvitation(@Body invitationObject: InvitationObject)

    /* DELETE REQUESTS */
    @DELETE("dbcontrol/api/v1/Invitations/{id}")
    suspend fun deleteInvitationById(@Path("id") id : Int)

    /* UPDATE REQUESTS */
    @PUT("dbcontrol/api/v1/Invitations/{id}")
    suspend fun updateInvitation(@Path("id") id : Int, @Body status: InvitationStatusObject)

    /* SLOTS */
    /* GET REQUESTS */
    @GET("dbcontrol/api/v1/SlotsRows")
    suspend fun getAllSlots() : List<SlotObject>
    @GET("dbcontrol/api/v1/Slots/{id}")
    suspend fun getSlotById(@Path("id") id : Int) : SlotObject
    @GET("dbcontrol/api/v1/slots/{start}/{end}/{day}")
    suspend fun getUniqueSlot(@Path("start") start: String, @Path("end") end: String, @Path("day") day: String) : SlotObject

    /* POST REQUESTS */
    @POST("dbcontrol/api/v1/Slots")
    suspend fun createSlot(@Body slot : SlotObject) : SlotObject

    /* DELETE REQUESTS */
    @DELETE("dbcontrol/api/v1/Slots/{id}")
    suspend fun deleteSlotById(@Path("id") id : Int)

    /* TODO: faire pour les tables : meetsheet, meet, player belong club*/

    /* MEET */
    /* GET REQUESTS */
    @GET("dbcontrol/api/v1/MeetsRows")
    suspend fun getAllMeets() : List<MeetObject>
    @GET("dbcontrol/api/v1/Meets/{id}")
    suspend fun getMeetById(@Path("id") id : Int) : MeetObject
    @GET("dbcontrol/api/v1/CalendarBPlayerRows/{id}")
    suspend fun getPlayerInvitations(@Path("id") id : Int) : List<PlayerMeetObject>
    @GET("dbcontrol/api/v1/CalendarBMeetRows/{id}")
    suspend fun getMeetPlayers(@Path("id") id : Int) : List<PlayerMeetObject>

    /* POST REQUESTS */
    @POST("dbcontrol/api/v1/Meets")
    suspend fun createMeet(@Body meetObject : MeetObject) : MeetObject

    /* DELETE REQUESTS */
    @DELETE("dbcontrol/api/v1/Meets/{id}")
    suspend fun deleteMeetById(@Path("id") id : Int)

    /* PUT REQUESTS */
    @PUT("dbcontrol/api/v1/Meets/{id}")
    suspend fun updateMeet(@Path("id") id : Int, @Body meetObject: MeetObject) : MeetObject

    /* MEETSHEETS */
    /* GET REQUESTS */
    /* TODO: verifier les types pour les lists de meetsheets */
    @GET("dbcontrol/api/v1/MeetsSheetRows")
    suspend fun getAllMeetsSheets() : List<List<MeetsSheetObject>>
    /* TODO: verifier le back pour cette route (ne marche pas atm) */
    @GET("dbcontrol/api/v1/MeetsSheet/{mail}")
    suspend fun getMeetsSheetByMail(@Path("mail") mail : String) : List<MeetsSheetObject>

    /* POST REQUESTS */
    @POST("dbcontrol/api/v1/MeetsSheet")
    suspend fun createMeetsSheet(@Body meetsSheetObject: MeetsSheetObject)

    /* DELETE REQUESTS */
    @DELETE("dbcontrol/api/v1/MeetsSheet/{mail}")
    suspend fun deleteMeetsSheetByMail(@Path("mail") mail : String)

    /* PLAYERBELONGCLUB */
    /* GET REQUESTS */
    //TODO without rows
    @GET("dbcontrol/api/v1/PlayerClubsRows/cid{id}")
    suspend fun getClubPlayers(@Path("id") idClub : Int) : List<PlayerAndPlayerBelongClubObject>
    @GET("dbcontrol/api/v1/PlayerClubsRows/pid{id}")
    suspend fun getPlayerClubs(@Path("id") idPlayer : Int) : List<ClubAndPlayerBelongClubObject>
    @GET("dbcontrol/api/v1/PlayerClubsRows/npid{id}")
    suspend fun getPlayerNotJoinedPublicClubs(@Path("id") idPlayer: Int) : List<ClubObject>

    /* POST REQUESTS */
    @POST("dbcontrol/api/v1/PlayerClubs")
    suspend fun addPlayerToClub(@Body playerClub: PlayerBelongClubObject)

    /* DELETE REQUESTS */
    @DELETE("dbcontrol/api/v1/PlayerClubs/{cid}&{pid}")
    suspend fun removePlayerFromClub(@Path("cid") idClub: Int, @Path("pid") idPlayer: Int)

    /* PUT REQUESTS */
    @PUT("dbcontrol/api/v1/PlayerClubsPromoteToAdmin/{pid}&{cid}")
    suspend fun promotePlayerToAdminOfClub(@Path("pid") playerId: Int, @Path("cid") clubId: Int)

}

object DatabaseApi {

    val retrofitService: DatabaseApiService by lazy {
        retrofit.create(DatabaseApiService::class.java)
    }
}