package com.example.quickmatch.content.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.quickmatch.BaseFragment
import com.example.quickmatch.R
import com.example.quickmatch.content.ContentActivity
import com.example.quickmatch.content.player
import com.example.quickmatch.databinding.FragmentHomeBinding
import com.example.quickmatch.network.PlayerObject


/**
 * A simple [Fragment] subclass.
 */
class HomeFragmentUI : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        /* Cast activity into a Content activity to get access to the logged player */
        val contentActivity = (this.activity as ContentActivity)

        binding.player = player

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Quick Match"
    }
}