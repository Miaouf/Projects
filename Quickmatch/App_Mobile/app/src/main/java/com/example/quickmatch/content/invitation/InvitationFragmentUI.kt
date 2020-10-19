package com.example.quickmatch.content.invitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.quickmatch.BaseFragment
import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentInvitationBinding


open class InvitationFragmentUI : BaseFragment() {

    private lateinit var viewModel: InvitationFragmentViewModel
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentInvitationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invitation, container, false)
        viewModel = ViewModelProviders.of(this).get(InvitationFragmentViewModel::class.java)

        binding.viewModel = viewModel
        viewPager = binding.pager

        val pagerAdapter = InvitationPagerAdapter(this.childFragmentManager)
        viewPager.adapter = pagerAdapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val fragment = pagerAdapter.getFragment(position)
                fragment.onResume()
            }
        })



        /* link view pager with tab layout */
        binding.tabLayout.setupWithViewPager(viewPager)

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Invitations"
    }
}
