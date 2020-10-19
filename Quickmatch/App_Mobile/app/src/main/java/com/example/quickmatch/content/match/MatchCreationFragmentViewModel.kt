package com.example.quickmatch.content.match

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.content.player
import com.example.quickmatch.network.*
import com.example.quickmatch.utils.FormatUtils
import com.example.quickmatch.utils.FormatUtils.toStringDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MatchCreationFragmentViewModel : ViewModel() {

    /* counts for meets and slots to know progress of the process */

    private val _countMeets = MutableLiveData(0)
    val countMeets : LiveData<Int>
        get() = _countMeets

    private val _countSlots = MutableLiveData(0)
    val countSlots : LiveData<Int>
        get() = _countSlots

    private val _countFinishedProcesses = MutableLiveData(0)
    val countFinishedProcesses : LiveData<Int>
        get() = _countFinishedProcesses

    private val _countInvitations = MutableLiveData(0)
    val countInvitations : LiveData<Int>
        get() = _countInvitations

    /* Live datas storing database datas as objects */

    private val _clubs = MutableLiveData<List<ClubAndPlayerBelongClubObject>>()
    val clubs : LiveData<List<ClubAndPlayerBelongClubObject>>
        get() = _clubs

    private val _clubsNames = MutableLiveData<List<String>>()
    val clubsNames : LiveData<List<String>>
        get() = _clubsNames

    private val _slots = MutableLiveData<MutableList<SlotObject>>(mutableListOf())
    val slots : LiveData<MutableList<SlotObject>>
        get() = _slots

    private val _newMatches = MutableLiveData<MutableList<MeetObject>>(mutableListOf())
    val newMatches : LiveData<MutableList<MeetObject>>
        get() = _newMatches

    private val _players = MutableLiveData<List<PlayerAndPlayerBelongClubObject>>(listOf())
    val players : LiveData<List<PlayerAndPlayerBelongClubObject>>
        get() = _players

    val currentDayForSlot = MutableLiveData<String>()

    /* Statuses for various requests of this view model */

    private val _getSlotStatus = MutableLiveData<RequestStatus>()
    val getSlotStatus : LiveData<RequestStatus>
        get() = _getSlotStatus

    private val _createSlotStatus = MutableLiveData<RequestStatus>()
    val createSlotStatus : LiveData<RequestStatus>
        get() = _createSlotStatus

    private val _getClubsStatus = MutableLiveData<RequestStatus>()
    val getClubsStatus : LiveData<RequestStatus>
        get() =_getClubsStatus

    private val _getClubsNamesStatus = MutableLiveData<RequestStatus>()
    val getClubsNamesStatus : LiveData<RequestStatus>
        get() =_getClubsNamesStatus

    private val _matchCreationStatus = MutableLiveData<RequestStatus>()
    val matchCreationStatus : LiveData<RequestStatus>
    get() = _matchCreationStatus

    private val _invitationCreationStatus = MutableLiveData<RequestStatus>()
    val invitationCreationStatus : LiveData<RequestStatus>
        get() = _invitationCreationStatus

    private val _getClubPlayersStatus = MutableLiveData<RequestStatus>()
    val getClubPlayersStatus : LiveData<RequestStatus>
        get() = _getClubPlayersStatus

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getPossibleClubsNames()
    }


    fun getSelectedClubPlayers(clubName: String) {

        val idClub = _clubs.value!!.find { club -> club.name == clubName }!!.idClub

        _getClubPlayersStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _players.value = DatabaseApi.retrofitService.getClubPlayers(idClub)
                _getClubPlayersStatus.value = RequestStatus.DONE


            } catch (t: Throwable) {

                Timber.i(t.message + " / getSelectedClubPlayers()")
                _getClubPlayersStatus.value = RequestStatus.ERROR
            }
        }


    }

    /* get the players' clubs from database (only the names) */
    private fun getPossibleClubsNames() {

        _getClubsNamesStatus.value = RequestStatus.LOADING
        coroutineScope.launch {

            try {

                _clubs.value = DatabaseApi.retrofitService.getPlayerClubs(player.id!!)
                _clubsNames.value = DatabaseApi.retrofitService.getPlayerClubs(player.id!!).map { club -> club.name }
                _getClubsNamesStatus.value = RequestStatus.DONE

            } catch(t: Throwable) {

                Timber.i(t.message + " / getPossibleClubsNames()")
                _getClubsNamesStatus.value = RequestStatus.ERROR

            }
        }

    }

    /* procedure creating matches for each repetition day and for each repetition */
    fun processMatchCreation(date: Calendar, startHour: String, endHour: String,
                             minSize: Int, maxSize: Int, location: String,
                             repeatDays: List<Boolean>, repetitions: Int) {

       for (i in 1..repeatDays.size) { // For each day of the week

            if(repeatDays[i-1]) { // If the user wants to repeat the match

                for (r in 0..repetitions) { // Repeats it until number of repetitions

                    val repetitionDate = date.clone() as Calendar
                    repetitionDate.add(Calendar.DAY_OF_YEAR, r * 7 + i - date.get(Calendar.DAY_OF_WEEK))
                    createMatch(location, repetitionDate, minSize, maxSize)
                }
            }
        }
    }

    /* procedure launching slot check for each repeat day */
    fun processCheckSlots(startHour: String, endHour: String, repeatDays: List<Boolean>) {

        for(i in 1..repeatDays.size) {
            if(repeatDays[i-1]) {
                checkSlot(startHour, endHour, toStringDay(i))
            }
        }
    }

    /* check if the slot with given informations exists */
    private fun checkSlot(startHour: String, endHour: String, day: String) {

        _getSlotStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                val result = DatabaseApi.retrofitService.getUniqueSlot(startHour, endHour, day)
                _slots.value?.add(result)
                _countSlots.value = _countSlots.value?.plus(1)
                _getSlotStatus.value = RequestStatus.DONE
                Timber.i(_slots.value.toString())


            } catch (t: Throwable) {

                Timber.i("%s / checkSlot()", t.message)
                currentDayForSlot.value = day
                _getSlotStatus.value = RequestStatus.ERROR


            }
        }
    }

    /* creates 1 meet in the database */
    private fun createMatch(location: String, date: Calendar, minSize: Int, maxSize: Int) {

        val formattedDate = Timestamp(date.timeInMillis).toString()
        val newMatch = MeetObject(0, location, formattedDate, minSize, maxSize, null, false)

        _matchCreationStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                val result = DatabaseApi.retrofitService.createMeet(newMatch)
                _newMatches.value?.add(result)
                _countMeets.value = _countMeets.value?.plus(1)
                _matchCreationStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _matchCreationStatus.value = RequestStatus.ERROR
                Timber.i("%s / createMatch()", t.message)

            }
        }
    }

    /* creates a new slot in the database with given informations */
    fun createSlot(startHour: String, endHour: String, day: String) {

        val newSlot = SlotObject(0, startHour, endHour, day)

        _createSlotStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                val result = DatabaseApi.retrofitService.createSlot(newSlot)
                _slots.value?.add(result)
                _countSlots.value = _countSlots.value?.plus(1)
                _createSlotStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i("%s / createSlot()", t.message)
                _createSlotStatus.value = RequestStatus.ERROR

            }
        }

    }

    fun sendInvitations() {

        for(player in _players.value!!) {

            for (meet in _newMatches.value!!) {

                /* get the day of the week for the meet day in order to get the right slot */
                val calendarDate = Calendar.getInstance()
                val dateFormatter = SimpleDateFormat(FormatUtils.DATE_FORMAT)

                Timber.i(meet.date)

                var parseableDate = meet.date!!.replace("T", " ")
                parseableDate = parseableDate.replace("Z", "")

                try {

                    calendarDate.time = dateFormatter.parse(parseableDate)
                    val dayString = toStringDay(calendarDate.get(Calendar.DAY_OF_WEEK))
                    val slot = _slots.value!!.find { slot -> slot.day == dayString }

                    createInvitation(slot!!.id, player.idPlayer, meet.id)

                } catch (t: Throwable) {

                    Timber.i("%s / parse meet date", t.message)

                }
            }
        }
    }

    /* function creating 1 invitation */
    private fun createInvitation(idSlot: Int, idPlayer: Int, idMeet: Int) {

        val newInvitation = InvitationObject(0, idSlot, idPlayer, idMeet, null, "Match")

        _invitationCreationStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                DatabaseApi.retrofitService.createInvitation(newInvitation)
                _invitationCreationStatus.value = RequestStatus.DONE
                _countInvitations.value = countInvitations.value?.plus(1)

            } catch (t: Throwable) {

                Timber.i("%s / createInvitation()", t.message)
                _invitationCreationStatus.value = RequestStatus.ERROR

            }
        }
    }

    /* Increment number of finished porcesses */

    fun finishAProcess() {
        _countFinishedProcesses.value = _countFinishedProcesses.value?.plus(1)
    }

    /* reset functions for requests status */

    fun resetGetSlotStatus() {
        _getSlotStatus.value = null
    }

    fun resetCreateMatchStatus() {
        _matchCreationStatus.value = null
    }

    fun resetCreateSlotStatus() {
        _createSlotStatus.value = null
    }

    fun resetGetPlayersStatus() {
        _getClubPlayersStatus.value = null
    }

    fun resetCreateInvitationStatus() {
        _invitationCreationStatus.value = null
    }

    fun resetCounts() {
        _countMeets.value = 0
        _countSlots.value = 0
        _countFinishedProcesses.value = 0
        _countInvitations.value = 0
    }


}
