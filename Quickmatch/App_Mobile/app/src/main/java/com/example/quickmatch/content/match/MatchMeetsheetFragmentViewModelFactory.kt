package com.example.quickmatch.content.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MatchMeetsheetFragmentViewModelFactory(private val matchId: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchMeetsheetFragmentViewModel::class.java)) {
            return MatchMeetsheetFragmentViewModel(matchId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}