package com.example.quickmatch.content.match


import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quickmatch.BaseFragment

import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentMatchPlayBinding
import com.example.quickmatch.utils.FormatUtils
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class MatchPlayFragmentUI : BaseFragment() {

    private lateinit var viewModelFactory: MatchPlayFragmentViewModelFactory
    private lateinit var viewModel: MatchPlayFragmentViewModel

    private val args: MatchPlayFragmentUIArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentMatchPlayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_match_play, container, false)
        viewModelFactory = MatchPlayFragmentViewModelFactory(args.matchId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchPlayFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /* setting pickers for timer */
        binding.pickerMin.minValue = FormatUtils.MINIMUM_MINUTES
        binding.pickerMin.maxValue = FormatUtils.MAXIMUM_MINUTES
        binding.pickerMin.value = FormatUtils.MINIMUM_MINUTES
        binding.pickerSec.minValue = FormatUtils.MINIMUM_SECONDS
        binding.pickerSec.maxValue = FormatUtils.MAXIMUM_SECONDS
        binding.pickerSec.value = FormatUtils.MINIMUM_SECONDS


        /* start the timer set by the user */
        binding.buttonStart.setOnClickListener {

            Timber.i("Started")
            /* hide elements that set timer */
            binding.pickerSec.visibility = View.INVISIBLE
            binding.pickerMin.visibility = View.INVISIBLE
            it.visibility = View.INVISIBLE

            /* show working timer elements */
            binding.textTime.visibility = View.VISIBLE
            binding.buttonStop.show()

            val time = (binding.pickerMin.value * 60000 + binding.pickerSec.value * 1000).toLong()
            viewModel.startTimer(time)
        }

        /* stop timer and go back to set a new timer */
        binding.buttonStop.setOnClickListener {

            Timber.i("Stopped")

            /* show elements that set timer */
            binding.pickerSec.visibility = View.VISIBLE
            binding.pickerMin.visibility = View.VISIBLE
            binding.buttonStart.show()

            /* hide working timer elements */
            binding.textTime.visibility = View.INVISIBLE
            it.visibility = View.INVISIBLE

            viewModel.stopTimer()
        }

        /* pause and resume the timer */
        binding.buttonPause.setOnClickListener {
            viewModel.pauseResumeTimer()
            Timber.i("Paused")
        }

        binding.buttonResume.setOnClickListener {
            viewModel.pauseResumeTimer()
            Timber.i("Resumed")
        }

        viewModel.eventFinished.observe(viewLifecycleOwner, Observer {
            if(it) {
                viewModel.resetEventFinished()
                Timber.i("Finished")
                //ring
            }
        })

        viewModel.paused.observe(viewLifecycleOwner, Observer {
            when(it) {
                true -> {
                    binding.buttonResume.show()
                    binding.buttonPause.hide()
                }
                false -> {
                    binding.buttonPause.show()
                    binding.buttonResume.hide()
                }
                else -> {
                    binding.buttonPause.hide()
                    binding.buttonResume.hide()
                }
            }

        })

        binding.buttonMeetsheet.setOnClickListener {
            findNavController().navigate(MatchPlayFragmentUIDirections.actionMatchPlayFragmentUIToMatchMeetsheetFragmentUI(args.matchId))
        }

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Jouer un match"
    }
}
