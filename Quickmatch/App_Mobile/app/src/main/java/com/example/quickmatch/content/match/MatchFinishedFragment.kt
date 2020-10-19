package com.example.quickmatch.content.match


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentMatchFinishedBinding


class MatchFinishedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentMatchFinishedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_match_finished, container, false)
        val viewModel : MatchFragmentViewModel = ViewModelProviders.of(this).get(MatchFragmentViewModel::class.java)

        val adapter = MatchFinishedAdapter(MatchFinishedClickListener {
            Toast.makeText(context, "Match $it clicked", Toast.LENGTH_SHORT).show()
        })

        binding.listFinishedMatches.adapter = adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.playedMatches.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }


}
