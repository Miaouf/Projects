package com.example.quickmatch.access.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.PlayerObject
import com.example.quickmatch.utils.HashUtils.sha512
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class LoginStatus { UNKNOWN, GOOGLE, WRONG_PWD, NETWORK_ERROR, SUCCESS }

class LoginFragmentViewModel : ViewModel() {

    /* Result of the login query */
    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus : LiveData<LoginStatus>
        get() = _loginStatus

    /* Player object received from database */
    private var _player : PlayerObject? = null
    val player : PlayerObject?
        get() = _player

    /* Player object received from database with google mail address */
    private val _playerSilentGoogle = MutableLiveData<PlayerObject>()
    val playerSilentGoogle : LiveData<PlayerObject>
        get() = _playerSilentGoogle

    /* Player object received from database with google mail address */
    private val _playerGoogle = MutableLiveData<PlayerObject>()
    val playerGoogle : LiveData<PlayerObject>
        get() = _playerGoogle

    private val _getSilentGooglePlayer = MutableLiveData<RequestStatus>()
    val getSilentGooglePlayer : LiveData<RequestStatus>
        get() = _getSilentGooglePlayer

    private val _getGooglePlayer = MutableLiveData<RequestStatus>()
    val getGooglePlayer : LiveData<RequestStatus>
        get() = _getGooglePlayer

    private val _createGooglePlayerAccount = MutableLiveData<RequestStatus>()
    val createGooglePlayerAccount : LiveData<RequestStatus>
        get() = _createGooglePlayerAccount


    /* Create coroutine job and  */
    private var viewModelJob = Job()

    /* Uses main thread coz retrofit works itself on background threads */
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun tryToLogin(mailAddress : String, password : String) {

        coroutineScope.launch {

            try {

                _player = DatabaseApi.retrofitService.getPlayerByMail(mailAddress)

                Timber.i(_player.toString())

                checkPassword(_player!!, sha512(password + _player!!.id.toString()))

            } catch (t: Throwable) {

                Timber.i(t.message)
                _loginStatus.value = LoginStatus.NETWORK_ERROR

            }
        }
    }

    fun getGooglePlayerByMail(mailAddress: String) {

        _getSilentGooglePlayer.value  = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playerSilentGoogle.value = DatabaseApi.retrofitService.getPlayerByMail(mailAddress)
                _getSilentGooglePlayer.value  = RequestStatus.DONE

            } catch (t: Throwable) {

                _getSilentGooglePlayer.value  = RequestStatus.ERROR
                Timber.i(t.message + " / getPlayerByMail()")
            }
        }
    }

    fun getGooglePlayerOrCreateAccount(mailAddress: String) {

        _getGooglePlayer.value  = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playerGoogle.value = DatabaseApi.retrofitService.getPlayerByMail(mailAddress)
                _getGooglePlayer.value  = RequestStatus.DONE

            } catch (t: Throwable) {

                _getGooglePlayer.value  = RequestStatus.ERROR
                Timber.i(t.message + " / getPlayerByMail()")

            }
        }
    }

    fun createGooglePlayer(mailAddress: String,
                           familyName: String?,
                           givenName: String?,
                           displayName: String,
                           photoUrl: String?) {


        val newPlayer = PlayerObject(
            null, familyName!!, givenName!!, displayName, "stocked by Google",
            mailAddress, null, 0, 0, 0, 0, photoUrl!!, null,
            null, false)

        _createGooglePlayerAccount.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _playerGoogle.value = DatabaseApi.retrofitService.addPlayer(newPlayer)
                _createGooglePlayerAccount.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _createGooglePlayerAccount.value = RequestStatus.ERROR
                Timber.i(t.message + " / createGooglePlayer()")

            }
        }
    }



    private fun checkPassword(player : PlayerObject, password: String) = when (player.password) {
                                                                                "stocked by Google" -> _loginStatus.value = LoginStatus.GOOGLE
                                                                                password ->  _loginStatus.value = LoginStatus.SUCCESS
                                                                                else -> _loginStatus.value = LoginStatus.WRONG_PWD
                                                                                }

    fun resetGetGooglePlayerStatus() {
        _getGooglePlayer.value = null
    }

    fun resetGetSilentGooglePlayerStatus() {
        _getSilentGooglePlayer.value = null
    }

    fun resetCreateGoogleAccountStatus() {
        _createGooglePlayerAccount.value = null
    }
}