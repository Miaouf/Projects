package com.example.quickmatch.content.stat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quickmatch.BaseFragment
import com.example.quickmatch.R
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.databinding.FragmentStatsBinding
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class StatFragmentUI : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentStatsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stats, container, false)

        val viewModel = ViewModelProvider(this).get(StatFragmentViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.getPlayerStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it) {
                    RequestStatus.ERROR -> Toast.makeText(context, "Erreur de récupération des statistiques du joueur...", Toast.LENGTH_LONG).show()
                    RequestStatus.LOADING -> Timber.i("Retrieving stats")
                    else -> Timber.i("Successfully retrieved stats")
                }
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Statistiques"
    }
}
