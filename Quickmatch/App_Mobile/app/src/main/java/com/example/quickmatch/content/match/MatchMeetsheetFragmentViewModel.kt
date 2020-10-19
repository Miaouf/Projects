package com.example.quickmatch.content.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MatchMeetsheetFragmentViewModel(private val matchId: Int) : ViewModel() {

    private val _getPlayersMeetStatus = MutableLiveData<RequestStatus>()
    val getPlayersMeetStatus : LiveData<RequestStatus>
        get() = _getPlayersMeetStatus

    private val _playersMeet = MutableLiveData<List<PlayerMeetObject>>()
    val playersMeet : LiveData<List<PlayerMeetObject>>
        get() = _playersMeet

    private val _players = MutableLiveData<MutableList<PlayerObject>>(mutableListOf())
    val players : LiveData<MutableList<PlayerObject>>
        get() = _players

    private val _getMeetStatus = MutableLiveData<RequestStatus>()
    val getMeetStatus : LiveData<RequestStatus>
        get() = _getMeetStatus

    private val _meet = MutableLiveData<MeetObject>()
    val meet : LiveData<MeetObject>
        get() = _meet

    private val _updateMeetStatus = MutableLiveData<RequestStatus>()
    val updateMeetStatus : LiveData<RequestStatus>
        get() = _updateMeetStatus

    private val _createMeetsheetStatus = MutableLiveData<RequestStatus>()
    val createMeetsheetStatus : LiveData<RequestStatus>
        get() = _createMeetsheetStatus

    private val _getPlayerStatus = MutableLiveData<RequestStatus>()
    val getPlayerStatus : LiveData<RequestStatus>
        get() = _getPlayerStatus

    private val _updatePlayerStatus = MutableLiveData<RequestStatus>()
    val updatePlayerStatus : LiveData<RequestStatus>
        get() = _updatePlayerStatus

    private val _playersUpdated = MutableLiveData<Int>(0)
    val playersUpdated : LiveData<Int>
        get() = _playersUpdated

    private val _sheetsCreated = MutableLiveData<Int>(0)
    val sheetsCreated : LiveData<Int>
        get() = _sheetsCreated


    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getMeet()
        getMeetPlayers()
    }

    /* get all players of the meet */
    private fun getMeet() {

        _getMeetStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _meet.value = DatabaseApi.retrofitService.getMeetById(matchId)
                Timber.i(meet.value.toString())
                _getMeetStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i(t.message + " / getMeet()")
                _getMeetStatus.value = RequestStatus.ERROR

            }
        }
    }

    /* get all players of the meet */
    private fun getMeetPlayers() {

        _getPlayersMeetStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                // Only players that accepted invite
                _playersMeet.value = DatabaseApi.retrofitService.getMeetPlayers(matchId).filter { player -> player.status == true }
                Timber.i(playersMeet.value.toString())
                _getPlayersMeetStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i(t.message + " / getMeetPlayers()")
                _getPlayersMeetStatus.value = RequestStatus.ERROR

            }
        }
    }

    /* get all players objects */
    fun getPlayers() {
        for (p in playersMeet.value!!) {
            getPlayer(p.playerId)
        }
    }

    /* get one player */
    private fun getPlayer(playerId: Int) {

        _getPlayerStatus.value  = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                val result = DatabaseApi.retrofitService.getPlayerById(playerId)
                _players.value!!.add(result)
                Timber.i(players.value.toString())
                _getPlayerStatus.value  = RequestStatus.DONE

            } catch (t: Throwable) {

                _getPlayerStatus.value  = RequestStatus.ERROR
                Timber.i(t.message + " / updateMeet()")
            }
        }

    }

    /* process the update for all players of the meet */
    fun makeMeetsheet(team1Won: Boolean, team2Won: Boolean, team1Score: Int, team2Score: Int, arrayMeetSheetRows: ArrayList<MeetSheetRow>) {

        for (p in players.value!!) {

            /* get database future datas from UI datas */

            val row = getPlayerMeetsheetRow(arrayMeetSheetRows, p) //Player meetsheet row with recycler view corresponding item data
            val won = if (row.team == 1) team1Won else team2Won //Player won or not according to his team
            val oppositeTeamScore = if (row.team == 1) team2Score else team1Score //conceded goals = enemy team goals

            /* store informations in database */

            updatePlayerStats(p, row.goals, oppositeTeamScore, won)
            createMeetsheet(p, row.goals, oppositeTeamScore, won)

        }
    }

    /* update stats of a player given match result */
    private fun updatePlayerStats(player: PlayerObject, scored: Int, conceded: Int, won: Boolean) {

        val newPlayer = PlayerObject(player.id, player.surname, player.firstName,
            player.pseudo, player.password, player.mailAddress, player.phoneNumber,
            player.scoredGoals + scored,
            player.concededGoals + conceded,
            player.matchesPlayed + 1,
            player.victories + if (won) 1 else 0,
            player.avatar, player.bio, player.isValid, player.isPrivate
        )

        Timber.i(newPlayer.toString())

        _updatePlayerStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                DatabaseApi.retrofitService.updatePlayerById(player.id!!, newPlayer)
                _updatePlayerStatus.value = RequestStatus.DONE
                _playersUpdated.value = playersUpdated.value!!.plus(1)

            } catch (t: Throwable) {

                Timber.i(t.message + " / udpatePlayerStats()")
                _updatePlayerStatus.value = RequestStatus.ERROR
            }
        }
    }

    /* create a meetsheet for given player for the match */
    private fun createMeetsheet(player: PlayerObject, scored: Int, conceded: Int, won: Boolean) {

        val newMeetsheet = MeetsSheetObject(player.id!!, matchId, scored, conceded, won)

        _createMeetsheetStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

          try {

              DatabaseApi.retrofitService.createMeetsSheet(newMeetsheet)
              _createMeetsheetStatus.value = RequestStatus.DONE
              _sheetsCreated.value = sheetsCreated.value!!.plus(1)


          }   catch (t: Throwable) {

              Timber.i(t.message + " / createMeetsheet()")
              _createMeetsheetStatus.value = RequestStatus.ERROR

          }
        }
    }

    /* change meet to played status */
    fun updateMeetStatus() {

        var newMeet = MeetObject(matchId, null, null, null, null, null, true)
        _updateMeetStatus.value  = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                newMeet = DatabaseApi.retrofitService.updateMeet(matchId, newMeet)
                _updateMeetStatus.value  = RequestStatus.DONE

            } catch (t: Throwable) {

                _updateMeetStatus.value  = RequestStatus.ERROR
                Timber.i(t.message + " / updateMeet()")
            }
        }
    }

    private fun getPlayerMeetsheetRow(arrayMeetSheetRows: ArrayList<MeetSheetRow>, player: PlayerObject) : MeetSheetRow {
        return arrayMeetSheetRows.find { row -> row.id == player.id }!!
    }

    fun resetCount() {
        _playersUpdated.value = 0
        _sheetsCreated.value = 0
    }
}
