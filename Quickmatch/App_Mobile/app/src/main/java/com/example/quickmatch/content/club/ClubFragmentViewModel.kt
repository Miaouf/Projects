package com.example.quickmatch.content.club

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.player
import com.example.quickmatch.network.ClubAndPlayerBelongClubObject
import com.example.quickmatch.network.ClubObject
import com.example.quickmatch.network.DatabaseApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ClubFragmentViewModel : ViewModel() {

    private val _getStatus = MutableLiveData<RequestStatus>()
    val getStatus : LiveData<RequestStatus>
        get() = _getStatus

    val clubs = MutableLiveData<List<ClubAndPlayerBelongClubObject>>()

    private val _navigateToClub = MutableLiveData<Int>()
    val navigateToClub : LiveData<Int>
        get() = _navigateToClub

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getPlayerClubs()
    }

    fun getPlayerClubs() {

        coroutineScope.launch {

            _getStatus.value = RequestStatus.LOADING

            try {

                clubs.value = DatabaseApi.retrofitService.getPlayerClubs(player.id!!)
                Timber.i(clubs.value.toString())
                _getStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _getStatus.value = RequestStatus.ERROR
                Timber.i(t.message + " / getPlayerClubs()")
            }
        }
    }


    fun onClubClicked(clubId: Int) {
        _navigateToClub.value = clubId
    }

    fun onClubNavigated() {
        _navigateToClub.value = null
    }



}