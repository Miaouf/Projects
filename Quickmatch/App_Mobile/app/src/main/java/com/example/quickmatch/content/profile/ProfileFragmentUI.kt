package com.example.quickmatch.content.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.quickmatch.BaseFragment

import com.example.quickmatch.R
import com.example.quickmatch.content.ContentActivity
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.databinding.FragmentProfileBinding
import com.example.quickmatch.network.PlayerObject
import timber.log.Timber

class ProfileFragmentUI : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding : FragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        val viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getPlayerStatus.observe(viewLifecycleOwner, Observer {
            when(it) {
                RequestStatus.ERROR -> Toast.makeText(context, "Erreur de récupération des informations du joueur...", Toast.LENGTH_LONG).show()
                RequestStatus.DONE -> Timber.i(viewModel.playerObject.value.toString())
                else -> Timber.i("Retrieving player...")
            }
        })

        binding.editModeButton.setOnClickListener { findNavController().navigate(ProfileFragmentUIDirections.actionProfileFragmentUIToProfileEditFragmentUI()) }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Mon profil"
    }
}
