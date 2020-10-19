package com.example.quickmatch.content.match

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.network.MeetObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MatchPlayFragmentViewModel(private val matchId: Int) : ViewModel() {

    companion object {
        const val DEFAULT_TIME = 0L
        const val INTERVAL = 1000L
    }

    // timer variable
    private var timer : CountDownTimer?

    // meet got with id given by navigation
    private val _meet = MutableLiveData<MeetObject>()
    val meet : LiveData<MeetObject>
        get() = _meet

    private val _getMeetStatus = MutableLiveData<RequestStatus>()
    val getMeetStatus : LiveData<RequestStatus>
        get() = _getMeetStatus

    // The current time
    private val _currentTime = MutableLiveData<Long>()
    val currentTime : LiveData<Long>
        get() = _currentTime

    // Event at the end of timer
    private val _eventFinished = MutableLiveData<Boolean>()
    val eventFinished : LiveData<Boolean>
        get() = _eventFinished

    // State of timer
    private val _paused = MutableLiveData<Boolean>()
    val paused : LiveData<Boolean>
        get() = _paused

    // The current time formatted
    val currentTimeString = Transformations.map(currentTime) { time: Long -> DateUtils.formatElapsedTime(time / INTERVAL) }

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {

        getMeet()

        _currentTime.value = DEFAULT_TIME
        _eventFinished.value = false
        timer = null
    }

    /* Get the meet that is played */
    private fun getMeet() {

        _getMeetStatus.value = RequestStatus.LOADING

        coroutineScope.launch {

            try {

                _meet.value = DatabaseApi.retrofitService.getMeetById(matchId)
                _getMeetStatus.value = RequestStatus.DONE

            } catch (t: Throwable) {

                _getMeetStatus.value = RequestStatus.ERROR
                Timber.i("%s / getMeet()", t.message)

            }
        }
    }


    /* launch timer */
    fun startTimer(time: Long) {

        _currentTime.value = time
        _paused.value = false

        timer = object : CountDownTimer(time, INTERVAL) {
            override fun onFinish() {
                _eventFinished.value = true
            }

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = currentTime.value?.minus(INTERVAL)
            }
        }

        (timer as CountDownTimer).start()
    }

    /* pause and resume timer once launched */
    fun pauseResumeTimer() {

        _paused.value = !paused.value!!

        if(paused.value!!) {

            timer!!.cancel()

        } else {

            timer = object : CountDownTimer(currentTime.value!!, INTERVAL) {

                override fun onFinish() {
                    _eventFinished.value = true                }

                override fun onTick(millisUntilFinished: Long) {
                    _currentTime.value = currentTime.value?.minus(INTERVAL)
                }
            }

            (timer as CountDownTimer).start()

        }

    }

    /* stop timer method */
    fun stopTimer() {
        timer!!.cancel()
        _eventFinished.value = false
        _paused.value = null
    }


    /* reset finish event */
    fun resetEventFinished() {
        _eventFinished.value = false
        timer!!.cancel() // cancel previous timer
    }
}