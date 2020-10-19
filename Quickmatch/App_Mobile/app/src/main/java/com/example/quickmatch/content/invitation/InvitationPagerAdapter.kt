package com.example.quickmatch.content.invitation


import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class InvitationPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val NB_ITEMS = 2
    private var mFragmentTags : MutableMap<Int, String> = mutableMapOf()
    private val fragmentManager = fm

    override fun getItem(position: Int): Fragment {
        return when(position) {
            1 -> InvitationTreatedFragment()
            else -> InvitationNewFragment()
        }
    }

    override fun getCount(): Int {
        return NB_ITEMS
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when(position) {
            1 -> "Invitations traitées"
            else -> "Nouvelles invitations"
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val obj = super.instantiateItem(container, position)
        if(obj is Fragment) {
            val fragment = obj
            val tag = fragment.tag
            mFragmentTags[position] = tag!!
        }
        return obj
    }

    fun getFragment(position: Int) : Fragment {
        return fragmentManager.findFragmentByTag(mFragmentTags[position])!!
    }
}