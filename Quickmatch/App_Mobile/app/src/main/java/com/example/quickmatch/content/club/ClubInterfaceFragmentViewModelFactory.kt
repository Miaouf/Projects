package com.example.quickmatch.content.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/* create a factory in order to pass the club ID to the viewModel */
class ClubInterfaceFragmentViewModelFactory(private val clubId : Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClubInterfaceFragmentViewModel::class.java)) {
            return ClubInterfaceFragmentViewModel(clubId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}