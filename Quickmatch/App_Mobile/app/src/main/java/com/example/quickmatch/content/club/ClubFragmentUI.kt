package com.example.quickmatch.content.club


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.quickmatch.BaseFragment
import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentClubBinding
import com.example.quickmatch.network.PlayerObject

/**
 * A simple [Fragment] subclass.
 */
class ClubFragmentUI : BaseFragment() {

    private lateinit var player: PlayerObject
    private lateinit var viewModel: ClubFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentClubBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_club, container, false)
        viewModel = ViewModelProviders.of(this).get(ClubFragmentViewModel::class.java)

        player = this.activity!!.intent.getParcelableExtra("player")

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /* adapter for the recycler view */
        val adpater = ClubAdapter(ClubClickListener {
            viewModel.onClubClicked(it!!)
        })

        binding.listClubs.adapter = adpater

        binding.searchButton.setOnClickListener {
            findNavController().navigate(ClubFragmentUIDirections.actionClubFragmentUIToClubListFragmentUI())
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(ClubFragmentUIDirections.actionClubFragmentUIToClubCreationFragmentUI())
        }

        viewModel.clubs.observe(viewLifecycleOwner, Observer {
            if(it.isEmpty()) {
                binding.listClubs.visibility = View.GONE
                binding.textNoClub.visibility = View.VISIBLE
            } else {
                binding.listClubs.visibility = View.VISIBLE
                binding.textNoClub.visibility = View.GONE
                it?.let {
                    adpater.submitList(it)
                }
            }
        })

        viewModel.navigateToClub.observe(this, Observer {
            it?.let {
                this.findNavController().navigate(ClubFragmentUIDirections.actionClubFragmentUIToClubIntefaceFragment(it))
                viewModel.onClubNavigated()
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Clubs"
    }

    override fun onStart() {
        super.onStart()
        viewModel.getPlayerClubs()

    }
}
