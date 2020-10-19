package com.example.quickmatch.access.title


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.PlayerObject
import kotlinx.coroutines.*
import timber.log.Timber

enum class ConnectionStatus { DONE, ERROR, LOADING, RESET }

class TitleFragmentViewModel : ViewModel() {

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // Connection status property (private mutable and public)
    private val _status = MutableLiveData<ConnectionStatus>()
    val status : LiveData<ConnectionStatus>
        get() = _status

    private val _listPlayers = MutableLiveData<List<PlayerObject>>()
    val listPlayers : LiveData<List<PlayerObject>>
        get() = _listPlayers

    init {
        tryToConnect()
    }

    fun tryToConnect() {

        coroutineScope.launch {

            try {

                _status.value = ConnectionStatus.LOADING
                var result = DatabaseApi.retrofitService.testConnection() // work on a background thread
                Timber.i(result.message)
                _status.value = ConnectionStatus.DONE

            } catch (t: Throwable) {

                Timber.i(t.message)
                _status.value = ConnectionStatus.ERROR

            }

            Timber.i(_status.value.toString())

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun resetConnectionStatus() {
        _status.value = ConnectionStatus.RESET
    }
}
