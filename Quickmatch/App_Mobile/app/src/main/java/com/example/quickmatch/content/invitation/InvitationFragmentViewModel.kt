package com.example.quickmatch.content.invitation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.content.player
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.InvitationStatusObject
import com.example.quickmatch.network.PlayerMeetObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class InvitationFragmentViewModel : ViewModel() {

    /* statuses for requests */

    private val _getNewInvitationsStatus = MutableLiveData<RequestStatus>()
    val getNewInvitationsStatus : LiveData<RequestStatus>
        get() = _getNewInvitationsStatus

    private val _getTreatedInvitationsStatus = MutableLiveData<RequestStatus>()
    val getTreatedInvitationsStatus : LiveData<RequestStatus>
        get() = _getTreatedInvitationsStatus

    private val _acceptInvitationStatus = MutableLiveData<RequestStatus>()
    val acceptInvitationStatus : LiveData<RequestStatus>
        get() = _acceptInvitationStatus

    private val _declineInvitationStatus = MutableLiveData<RequestStatus>()
    val declineInvitationStatus : LiveData<RequestStatus>
        get() = _declineInvitationStatus

    /* results */

    private val _treatedInvitations = MutableLiveData<List<PlayerMeetObject>>()
    val treatedInvitations : LiveData<List<PlayerMeetObject>>
        get() = _treatedInvitations

    private val _newInvitations = MutableLiveData<List<PlayerMeetObject>>()
    val newInvitations : LiveData<List<PlayerMeetObject>>
        get() = _newInvitations

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getNewInvitations()
        getTreatedInvitations()
    }

    fun getNewInvitations() {

        _getNewInvitationsStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _newInvitations.value = DatabaseApi.retrofitService.getPlayerInvitations(player.id!!).filter { invitation -> invitation.status == null }
                _getNewInvitationsStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i("%s / getNewInvitations()", t.message)
                _getNewInvitationsStatus.value = RequestStatus.ERROR

            }
        }
    }

    fun getTreatedInvitations() {

        _getTreatedInvitationsStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _treatedInvitations.value = DatabaseApi.retrofitService.getPlayerInvitations(player.id!!).filter { invitation -> invitation.status != null }
                _getTreatedInvitationsStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i("%s / getTreatedInvitations()", t.message)
                _getTreatedInvitationsStatus.value = RequestStatus.ERROR

            }
        }
    }

    fun acceptInvitation(invitationId: Int) {

        _acceptInvitationStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                val status = InvitationStatusObject(true)
                DatabaseApi.retrofitService.updateInvitation(invitationId, status)
                getTreatedInvitations()
                getNewInvitations()
                _acceptInvitationStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _acceptInvitationStatus.value = RequestStatus.ERROR
                Timber.i("%s / acceptInvitation()", t.message)
            }
        }
    }

    fun declineInvitation(invitationId: Int) {

        _declineInvitationStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                val status = InvitationStatusObject(false)
                DatabaseApi.retrofitService.updateInvitation(invitationId, status)
                getTreatedInvitations()
                getNewInvitations()
                _declineInvitationStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _declineInvitationStatus.value = RequestStatus.ERROR
                Timber.i("%s / declineInvitation()", t.message)

            }
        }
    }

}
