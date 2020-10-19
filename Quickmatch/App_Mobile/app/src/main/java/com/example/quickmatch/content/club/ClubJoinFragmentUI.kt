package com.example.quickmatch.content.club


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.quickmatch.BaseFragment
import androidx.lifecycle.Observer
import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentClubJoinBinding
import com.example.quickmatch.network.PlayerObject

/**
 * A simple [Fragment] subclass.
 */
class ClubJoinFragmentUI : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentClubJoinBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_club_join, container, false)
        val viewModel = ViewModelProviders.of(this).get(ClubJoinFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val player: PlayerObject = this.activity!!.intent.getParcelableExtra("player")

        /* give the adapter to the recycler view */
        val adapter = ClubJoinAdapter(ClubJoinClickListener {
            viewModel.onJoinClubClicked(it)
        })

        binding.listClubs.adapter = adapter

        /* change the recycler view's data if datas changes */
        viewModel.clubs.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                binding.listClubs.visibility = View.GONE
                binding.textEmptyList.visibility = View.VISIBLE
            } else {
                binding.listClubs.visibility = View.VISIBLE
                binding.textEmptyList.visibility = View.GONE
                it?.let {
                    adapter.submitList(it)
                }
            }
        })

        viewModel.joinStatus.observe(this, Observer {
            when (it) {
                RequestStatus.DONE -> Toast.makeText(
                    context,
                    "Club rejoint avec succès !",
                    Toast.LENGTH_SHORT
                ).show()
                RequestStatus.ERROR -> Toast.makeText(
                    context,
                    "Impossible de rejoindre le club...",
                    Toast.LENGTH_SHORT
                ).show()
                //RequestStatus.LOADING ->
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Clubs disponibles"
    }
}
