package com.example.quickmatch.content.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.content.player
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.PlayerObject
import com.example.quickmatch.utils.FormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class PhoneRequestStatus { DONE, LOADING, ERROR, EMPTY }

class ProfileFragmentViewModel : ViewModel() {


    private val _playerObject = MutableLiveData<PlayerObject>()
    val playerObject : LiveData<PlayerObject>
        get() = _playerObject

    private val _playerMail = MutableLiveData<PlayerObject>()
    val playerMail : LiveData<PlayerObject>
        get() = _playerMail

    private val _playerPseudo = MutableLiveData<PlayerObject>()
    val playerPseudo : LiveData<PlayerObject>
        get() = _playerPseudo

    private val _playerPhone = MutableLiveData<PlayerObject>()
    val playerPhone : LiveData<PlayerObject>
        get() = _playerPhone

    /* Status for database requests */

    private val _mailStatus = MutableLiveData<RequestStatus>()
    val mailStatus : LiveData<RequestStatus>
        get() = _mailStatus

    private val _getPlayerStatus = MutableLiveData<RequestStatus>()
    val getPlayerStatus : LiveData<RequestStatus>
        get() = _getPlayerStatus

    private val _pseudoStatus = MutableLiveData<RequestStatus>()
    val pseudoStatus : LiveData<RequestStatus>
        get() = _pseudoStatus

    private val _phoneStatus = MutableLiveData<PhoneRequestStatus>()
    val phoneStatus : LiveData<PhoneRequestStatus>
        get() = _phoneStatus

    private val _updateStatus = MutableLiveData<RequestStatus>()
    val updateStatus : LiveData<RequestStatus>
        get() = _updateStatus

    /* Status for fields format */

    private val _pseudoFormat = MutableLiveData<Boolean>()
    val pseudoFormat : LiveData<Boolean>
        get() = _pseudoFormat

    private val _mailFormat = MutableLiveData<Boolean>()
    val mailFormat : LiveData<Boolean>
        get() = _mailFormat

    private val _nameFormat = MutableLiveData<Boolean>()
    val nameFormat : LiveData<Boolean>
        get() = _nameFormat

    private val _firstNameFormat = MutableLiveData<Boolean>()
    val firstNameFormat : LiveData<Boolean>
        get() = _firstNameFormat

    private val _phoneNumberFormat = MutableLiveData<Boolean>()
    val phoneNumberFormat : LiveData<Boolean>
        get() = _phoneNumberFormat

    private val _bioFormat = MutableLiveData<Boolean>()
    val bioFormat : LiveData<Boolean>
        get() = _bioFormat

    private val _avatarFormat = MutableLiveData<Boolean>()
    val avatarFormat : LiveData<Boolean>
        get() = _avatarFormat

    /* Coroutine needed instances */

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

    /* Method checking unicity of certain fields before saving changes */

    fun checkMail(mailAddress : String) {

        _mailStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playerMail.value = DatabaseApi.retrofitService.getPlayerByMail(mailAddress)
                _mailStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _mailStatus.value = RequestStatus.ERROR
                Timber.i(t.message + " / checkMail()")

            }
        }
    }

    fun checkPseudo(pseudo : String) {

        _pseudoStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playerPseudo.value = DatabaseApi.retrofitService.getPlayerByPseudo(pseudo)
                _pseudoStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _pseudoStatus.value = RequestStatus.ERROR
                Timber.i(t.message + " / checkPseudo()")

            }
        }
    }

    fun checkPhone(phoneNumber : String) {

        _phoneStatus.value = PhoneRequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playerPhone.value = DatabaseApi.retrofitService.getPlayerByPhoneNumber(phoneNumber)
                _phoneStatus.value = PhoneRequestStatus.DONE

            } catch (t: Throwable) {

                _phoneStatus.value = PhoneRequestStatus.ERROR
                Timber.i(t.message + " / checkPhone()")

            }
        }
    }

    /* Format check for each field */

    /* Check format for pseudo */
    fun checkFormatPseudo(pseudo: String) {
        val l = pseudo.length
        _pseudoFormat.value = l in 1..FormatUtils.BASIC_SIZE
    }

    /* Check format for mail address */
    fun checkFormatMail(mailAddress: String) {
        val l = mailAddress.length
        _mailFormat.value = l in 1..FormatUtils.MAIL_SIZE && mailAddress.matches(FormatUtils.mailPattern)
    }

    /* Check phone number format (french) */
    fun checkFormatPhoneNumber(phoneNumber: String) {
        _phoneNumberFormat.value = phoneNumber.matches(FormatUtils.phoneNumberPattern)
    }

    /* Check format for name */
    fun checkFormatName(name: String) {
        val l = name.length
        _nameFormat.value = l in 1..FormatUtils.BASIC_SIZE
    }

    /* Check format for 1st name */
    fun checkFormatFirstName(firstName: String) {
        val l = firstName.length
        _firstNameFormat.value = l in 1..FormatUtils.BASIC_SIZE
    }


    /* Saves all changes done on the player profile */
    fun checkChanges(pseudo: String, mailAddress: String, phoneNumber: String) {

        checkMail(mailAddress)
        checkPseudo(pseudo)
        if (!phoneNumber.matches(FormatUtils.emptyFieldPattern))
            checkPhone(phoneNumber)
        else _phoneStatus.value = PhoneRequestStatus.EMPTY

    }

    fun updatePlayer(name: String, firstName: String, pseudo: String, mailAddress: String, phoneNumber: String?, avatarURL: String, bio: String, privacy: Boolean) {

        Timber.i(bio)

        val updatedPlayer = PlayerObject(null,
            name, firstName, pseudo, null,
            mailAddress, phoneNumber,
            0, 0, 0, 0,
            avatarURL, bio, null, privacy)


        coroutineScope.launch {

            _updateStatus.value = RequestStatus.LOADING

            try {

                var result = DatabaseApi.retrofitService.updatePlayerById(player.id!!, updatedPlayer)
                Timber.i(result.toString())
                _updateStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                Timber.i(t.message + " / updatePlayer()")
                _updateStatus.value = RequestStatus.ERROR

            }
        }

    }

    fun resetCheckStatus() {
        _mailStatus.value = null
        _pseudoStatus.value = null
        _phoneStatus.value = null
    }
}
