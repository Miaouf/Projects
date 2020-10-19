package com.example.quickmatch.content.stat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.content.player
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.PlayerObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class StatFragmentViewModel : ViewModel() {

    private val _playerObject = MutableLiveData<PlayerObject>()
    val playerObject : LiveData<PlayerObject>
        get() = _playerObject

    private val _getPlayerStatus = MutableLiveData<RequestStatus>()
    val getPlayerStatus : LiveData<RequestStatus>
        get() = _getPlayerStatus

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getPlayer()
    }


    private fun getPlayer() {

        _getPlayerStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playerObject.value = DatabaseApi.retrofitService.getPlayerById(player.id!!)
                _getPlayerStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i(t.message + " / getPlayer()")
                _getPlayerStatus.value = RequestStatus.ERROR

            }
        }
    }
}