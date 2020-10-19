package com.example.quickmatch.content.club

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickmatch.network.ClubObject
import com.example.quickmatch.network.DatabaseApi
import com.example.quickmatch.utils.FormatUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class ClubNameUnicityStatus { EXISTS, LOADING, AVAILABLE }
enum class ClubCreationStatus { DONE, LOADING, ERROR }

class ClubCreationFragmentViewModel : ViewModel() {

    private val _clubNameFormatStatus = MutableLiveData<Boolean>()
    val clubNameFormatStatus : LiveData<Boolean>
        get() = _clubNameFormatStatus

    private val _clubNameExistsStatus = MutableLiveData<ClubNameUnicityStatus>()
    val clubNameExistsStatus : LiveData<ClubNameUnicityStatus>
        get() = _clubNameExistsStatus

    private val _clubCreationStatus = MutableLiveData<ClubCreationStatus>()
    val clubCreationStatus : LiveData<ClubCreationStatus>
        get() = _clubCreationStatus

    // Create coroutine job and scope
    private var viewModelJob = Job()

    // Uses main thread coz retrofit works itself on background threads
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun checkClubNameFormat(clubName: String) {

        val l = clubName.length
        _clubNameFormatStatus.value = l in 1..FormatUtils.CLUB_NAME_SIZE

    }

/*    private fun checkClubNameUnicity(clubName: String) {

        _clubNameExistsStatus.value = ClubNameUnicityStatus.LOADING

        coroutineScope.launch {

          try {

              val result = DatabaseApi.retrofitService.getClubByName(name)
              _clubNameExistsStatus.value = ClubNameUnicityStatus.EXISTS

          }  catch (t: Throwable) {

              Timber.i(t.message + " / checkClubNameUnicity()")
              _clubNameExistsStatus.value = ClubNameUnicityStatus.AVAILABLE

          }
        }
    }*/


    fun createClub(clubName : String, privacy : Boolean) {

        //checkClubNameUnicity(clubName)

        val newClub = ClubObject(null, clubName, null, privacy)

        _clubCreationStatus.value = ClubCreationStatus.LOADING

        coroutineScope.launch {

            try {

                val result = DatabaseApi.retrofitService.addClub(newClub)
                _clubCreationStatus.value = ClubCreationStatus.DONE

            } catch (t: Throwable) {

                Timber.i(t.message + " / createClub()")
                _clubCreationStatus.value = ClubCreationStatus.ERROR

            }
        }
    }

    fun doneCreatingClub() {
        _clubCreationStatus.value = null
    }
}