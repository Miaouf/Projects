package com.example.quickmatch.content.club

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.player
import com.example.quickmatch.network.ClubObject
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.PlayerBelongClubObject
import com.example.quickmatch.network.PlayerObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class RequestStatus { DONE, ERROR, LOADING }

class ClubJoinFragmentViewModel : ViewModel() {

    private val _getAllStatus = MutableLiveData<RequestStatus>()
    val getAllStatus : LiveData<RequestStatus>
        get() = _getAllStatus

    private val _joinStatus = MutableLiveData<RequestStatus>()
    val joinStatus : LiveData<RequestStatus>
        get() = _joinStatus

   val clubs = MutableLiveData<List<ClubObject>>()

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getPublicNewClubs()
    }

    private fun getPublicNewClubs() {

        _getAllStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                clubs.value = DatabaseApi.retrofitService.getPlayerNotJoinedPublicClubs(player.id!!)
                _getAllStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i(t.message + " / getClubs()")
                _getAllStatus.value = RequestStatus.ERROR

            }
        }
    }

    fun onJoinClubClicked(clubId: Int?) {

        _joinStatus.value = RequestStatus.LOADING

        val newPlayerClub = PlayerBelongClubObject(player.id!!, clubId!!, false)
        Timber.i(newPlayerClub.toString())

        coroutineScope.launch {

            try {

                val result = DatabaseApi.retrofitService.addPlayerToClub(newPlayerClub)
                _joinStatus.value = RequestStatus.DONE
                getPublicNewClubs()

            } catch (t: Throwable) {

                Timber.i(t.message + " / onJoinClubClicked()")
                _joinStatus.value = RequestStatus.ERROR
            }
        }
    }

}