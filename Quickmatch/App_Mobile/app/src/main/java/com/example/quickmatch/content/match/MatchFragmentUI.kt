package com.example.quickmatch.content.match


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.quickmatch.BaseFragment
import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentMatchsBinding

private lateinit var viewPager: ViewPager
private lateinit var pagerAdapter: MatchPagerAdapter

class MatchFragmentUI : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentMatchsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_matchs, container, false)

        /* link view pager with the adapter */
        viewPager = binding.pager
        pagerAdapter = MatchPagerAdapter(childFragmentManager)
        viewPager.adapter = pagerAdapter


        /* link view pager with tab layout */
        binding.tabLayout.setupWithViewPager(viewPager)

        binding.createMatchButton.setOnClickListener {
            findNavController().navigate(MatchFragmentUIDirections.actionMatchFragmentUIToMatchCreationFragmentUI())
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Matchs"
    }
}
