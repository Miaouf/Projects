package com.example.quickmatch.access.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.PlayerObject
import com.example.quickmatch.utils.FormatUtils
import com.example.quickmatch.utils.HashUtils
import kotlinx.coroutines.*
import timber.log.Timber

/* enum classes for status check on requests */
enum class SigninCheckStatus { EXISTS, VALID, LOADING }
enum class SigninStatus { NETWORK_ERROR, DONE, LOADING }

class SigninFragmentViewModel : ViewModel() {

    /* Live datas of the viewModel */

    private val _pseudoFormat = MutableLiveData<Boolean>()
    val pseudoFormat : LiveData<Boolean>
    get() = _pseudoFormat

    private val _mailFormat = MutableLiveData<Boolean>()
    val mailFormat : LiveData<Boolean>
        get() = _mailFormat

    private val _passwordFormat = MutableLiveData<Boolean>()
    val passwordFormat : LiveData<Boolean>
        get() = _passwordFormat

    private val _passwordCheckFormat = MutableLiveData<Boolean>()
    val passwordCheckFormat : LiveData<Boolean>
        get() = _passwordCheckFormat

    private val _nameFormat = MutableLiveData<Boolean>()
    val nameFormat : LiveData<Boolean>
        get() = _nameFormat

    private val _firstNameFormat = MutableLiveData<Boolean>()
    val firstNameFormat : LiveData<Boolean>
        get() = _firstNameFormat

    private val _phoneNumberFormat = MutableLiveData<Boolean>()
    val phoneNumberFormat : LiveData<Boolean>
        get() = _phoneNumberFormat

    // Result of the signin query
    private val _signinStatus = MutableLiveData<SigninStatus>()
    val signinStatus : LiveData<SigninStatus>
        get() = _signinStatus

    // Result of the mailCheck query
    private val _mailStatus = MutableLiveData<SigninCheckStatus>()
    val mailStatus : LiveData<SigninCheckStatus>
        get() = _mailStatus

    // Result of the pseudoCheck query
    private val _pseudoStatus = MutableLiveData<SigninCheckStatus>()
    val pseudoStatus : LiveData<SigninCheckStatus>
        get() = _pseudoStatus

    // Result of the phoneCheck query
    private val _phoneNumberStatus = MutableLiveData<SigninCheckStatus>()
    val phoneNumberStatus : LiveData<SigninCheckStatus>
        get() = _phoneNumberStatus

    /* Mutable map containing state of each unicity check */
    val mapUnicityChecks = MutableLiveData<MutableMap<String, Boolean>>()
    //val mapUnicityChecks : LiveData<MutableMap<String, Boolean>>
        //get() = _mapUnicityChecks

    /* Boolean indicating if the infos given allow the user to signin */
    private val _authorizedToSignin = MutableLiveData<Boolean>()
    val authorizedToSignin : LiveData<Boolean>
        get() = _authorizedToSignin

    /* Coroutine needed instances */

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /* Player object received from database */
    private var _player : PlayerObject? = null
    val player : PlayerObject?
        get() = _player

    init {
        _authorizedToSignin.value = false
    }

    /* Methods */

    /* set authorization of signing in method */
    fun checkUnicityState() {
        val status = listOf(_mailStatus.value, _pseudoStatus.value, _phoneNumberStatus.value)
        Timber.i(status.toString())
        _authorizedToSignin.value = SigninCheckStatus.EXISTS !in status &&
                SigninCheckStatus.LOADING !in status
    }

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

    /* Check format for password */
    fun checkFormatPassword(password: String) {
        _passwordFormat.value = password.matches(FormatUtils.passwordPattern)
    }

    /* Check passwords equality */
    fun checkFormatPasswordCheck(password: String, passwordCheck: String) {
        _passwordCheckFormat.value = password == passwordCheck && passwordCheck.matches(FormatUtils.passwordPattern)
    }

    /* Check phone number format (french) */
    fun checkPhoneNumberFormat(phoneNumber: String) {
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

    /* request to check if the mail typed already exists */
    private fun checkMailAddress(mailAddress: String) {

        _mailStatus.value = SigninCheckStatus.LOADING

        coroutineScope.launch {

            try { // Case request gives an existing player

                var result = DatabaseApi.retrofitService.getPlayerByMail(mailAddress)
                _mailStatus.value  = SigninCheckStatus.EXISTS

            } catch (t : Throwable) { // Case we get an error

                Timber.i( t.message + " mail")
                _mailStatus.value = SigninCheckStatus.VALID

            }
        }
    }

    /* request to check if the pseudo typed already exists */
    private fun checkPseudo(pseudo: String) {

        _pseudoStatus.value = SigninCheckStatus.LOADING

        coroutineScope.launch {

            try { // Case request gives an existing player

                var result = DatabaseApi.retrofitService.getPlayerByPseudo(pseudo)
                _pseudoStatus.value  = SigninCheckStatus.EXISTS
            } catch (t : Throwable) { // Case we get an error

                Timber.i(t.message + " pseudo")
                _pseudoStatus.value = SigninCheckStatus.VALID

            }
        }
    }


    /* request to check if the phone number typed already exists */
    private fun checkPhoneNumber(phoneNumber: String) {

        coroutineScope.launch {

            _phoneNumberStatus.value = SigninCheckStatus.LOADING

            try { // Case request gives an existing player

                var result = DatabaseApi.retrofitService.getPlayerByPhoneNumber(phoneNumber)
                _phoneNumberStatus.value  = SigninCheckStatus.EXISTS

            } catch (t : Throwable) { // Case we get an error

                Timber.i(t.message + " phone")
                _phoneNumberStatus.value = SigninCheckStatus.VALID

            }
        }
    }

    fun checkUnicity(mailAddress: String, pseudo: String, phoneNumber: String){

        /* Launch check requests */
        checkMailAddress(mailAddress)
        checkPseudo(pseudo)
        checkPhoneNumber(phoneNumber)
    }

    /* function checking the validity of unique informations and then posting the new player to the remote server */
    fun tryToSignIn(name : String, firstName : String, pseudo : String, mailAddress : String,
                    password : String, passwordCheck: String, phoneNumber : String) {

        /* Parse phone number */
        val finalPhoneNumber = FormatUtils.parsePhoneNumber(phoneNumber)

        /* hash password */
        val hashedPassword = HashUtils.sha512(password)

        /* Player that will be posted to the database */
        var newPlayerObject = PlayerObject(null, name, firstName, pseudo, hashedPassword, mailAddress, finalPhoneNumber,
                0, 0, 0, 0,
                null, null, null, null)

        Timber.i(newPlayerObject.toString())

        coroutineScope.launch {

            _signinStatus.value = SigninStatus.LOADING

            try {

                _player = DatabaseApi.retrofitService.addPlayer(newPlayerObject)
                Timber.i(_player.toString())

            } catch (t: Throwable) {

                Timber.i(t.message + " post1")
                _signinStatus.value = SigninStatus.NETWORK_ERROR
            }

            if (_player == null) return@launch

            _player!!.password = HashUtils.sha512(password + _player!!.id.toString())
            Timber.i(_player.toString())

            try { /* update salted password */

                _player = DatabaseApi.retrofitService.updatePlayerById(_player!!.id!!, _player!!)

                _signinStatus.value = SigninStatus.DONE
                Timber.i(_player.toString())

            } catch (t: Throwable) {

                Timber.i(t.message + " post2")
                _signinStatus.value = SigninStatus.NETWORK_ERROR

            }
        }
    }

    fun resetSigninStatus() {
        _signinStatus.value = null
    }

}
