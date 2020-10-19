package com.example.quickmatch.content.match

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MatchPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> MatchFinishedFragment()
            1 -> MatchUpcomingFragment()
            else -> MatchFinishedFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when(position) {
            1 -> "Matchs à venir"
            else -> "Matchs terminés"
        }
    }
}