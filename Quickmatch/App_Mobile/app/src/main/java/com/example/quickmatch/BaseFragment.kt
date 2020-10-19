package com.example.quickmatch

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/* Class to allow changing title of a fragment in the app bar */
abstract class BaseFragment : Fragment() {
    fun getActionBar() : androidx.appcompat.app.ActionBar? {
        return (activity as AppCompatActivity).supportActionBar
    }
}