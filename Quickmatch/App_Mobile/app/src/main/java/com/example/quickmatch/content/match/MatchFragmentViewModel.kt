package com.example.quickmatch.content.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.content.player
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.PlayerMeetObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MatchFragmentViewModel : ViewModel() {

    private val _playedMatches = MutableLiveData<List<PlayerMeetObject>>()
    val playedMatches : LiveData<List<PlayerMeetObject>>
        get() = _playedMatches

    private val _upcomingMatches = MutableLiveData<List<PlayerMeetObject>>()
    val upcomingMatches : LiveData<List<PlayerMeetObject>>
        get() = _upcomingMatches

    private val _getPlayedStatus = MutableLiveData<RequestStatus>()
    val getPlayedStatus : LiveData<RequestStatus>
        get() = _getPlayedStatus

    private val _getUpcomingStatus = MutableLiveData<RequestStatus>()
    val getUpcomingStatus : LiveData<RequestStatus>
        get() = _getUpcomingStatus

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getPlayerPlayedMatches()
        getPlayerUpcomingMatches()
    }

    fun getPlayerPlayedMatches() {

        _getPlayedStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playedMatches.value = DatabaseApi.retrofitService.getPlayerInvitations(player.id!!).filter { meet -> meet.status == true && meet.played == true }
                Timber.i(_playedMatches.value.toString())
                _getPlayedStatus.value = RequestStatus.DONE


            } catch (t: Throwable) {

                Timber.i(t.message + " / getPlayerPlayedMatches()")
                _getPlayedStatus.value = RequestStatus.ERROR

            }
        }
    }

    fun getPlayerUpcomingMatches() {

        _getUpcomingStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _upcomingMatches.value = DatabaseApi.retrofitService.getPlayerInvitations(player.id!!).filter { meet -> meet.status == true && meet.played != true }
                Timber.i(_upcomingMatches.value.toString())
                _getUpcomingStatus.value = RequestStatus.DONE


            } catch (t: Throwable) {

                Timber.i(t.message + " / getPlayerUpcomingMatches()")
                _getUpcomingStatus.value = RequestStatus.ERROR

            }
        }
    }

}