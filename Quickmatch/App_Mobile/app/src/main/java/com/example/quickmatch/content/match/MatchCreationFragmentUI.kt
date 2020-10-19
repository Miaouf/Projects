package com.example.quickmatch.content.match

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.quickmatch.BaseFragment

import com.example.quickmatch.R
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.databinding.FragmentMatchCreationBinding
import com.example.quickmatch.utils.FormatUtils
import timber.log.Timber
import java.util.*

class MatchCreationFragmentUI : BaseFragment() {

    private lateinit var viewModel: MatchCreationFragmentViewModel
    /* list of boolean to store repeat days chosen by the user */
    var repeatDays : List<Boolean>? = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentMatchCreationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_match_creation, container, false)
        viewModel = ViewModelProviders.of(this).get(MatchCreationFragmentViewModel::class.java)

        binding.viewModel = viewModel

        var clubNames : Array<String> = arrayOf("Pas de club à suggérer")

        /* fill suggestions list with the clubs from the database */
        viewModel.clubsNames.observe(this, Observer {
            clubNames = if(it.isNotEmpty()) it.toTypedArray() else clubNames
            ArrayAdapter(context!!, android.R.layout.simple_list_item_1, clubNames!!).also { adapter ->
                binding.autoCompleteInput.setAdapter(adapter)
            }
            Timber.i(clubNames.toString())
        })


        /* get current date to show it in date picker */
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val currentDay = currentTime.get(Calendar.DAY_OF_MONTH)
        val currentMonth = currentTime.get(Calendar.MONTH)
        val currentYear = currentTime.get(Calendar.YEAR)

        val chosenDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"))


        /* clear the edit text for club name if it's wrong */
        binding.autoCompleteInput.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                if (binding.autoCompleteInput.text.toString() !in clubNames )
                    binding.autoCompleteInput.text.clear()
            }
        }

        /* show time picker when clicking the edit text for start and end hours */

        binding.editStartHour.setOnFocusChangeListener { _, hasFocus ->

            if(hasFocus) {
                /* create the date picker */
                val timePickerStart = TimePickerDialog(context!!, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    binding.editStartHour.setText("$hourOfDay:$minute")
                }, currentHour, currentMinute, true)

                timePickerStart.setTitle(resources.getString(R.string.start_hour_text))
                timePickerStart.show()
            }
        }

        binding.editEndHour.setOnFocusChangeListener { _, hasFocus ->

            if(hasFocus) {
                /* create the date picker */
                val timePickerEnd = TimePickerDialog(context!!, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    binding.editEndHour.setText("$hourOfDay:$minute")
                }, currentHour, currentMinute, true)

                timePickerEnd.setTitle(resources.getString(R.string.end_hour_text))
                timePickerEnd.show()
            }
        }

        binding.editDate.setOnFocusChangeListener { _, hasFocus ->

            if(hasFocus) {
                val datePicker = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    binding.editDate.setText("$dayOfMonth/${month+1}/$year")
                    chosenDate.clear()
                    chosenDate.set(year, month, dayOfMonth)

                    /* offsets just used to make calendar compute day of week */
                    chosenDate.add(Calendar.WEEK_OF_YEAR, 1)
                    chosenDate.add(Calendar.WEEK_OF_YEAR, -1)

                }, currentYear, currentMonth, currentDay)

                datePicker.show()
            }
        }

        /* what happens when user clicks create */
        binding.buttonCreateMatch.setOnClickListener {

            /* create a list of boolean to know which day the user wishes to play */
            this.repeatDays = listOf(
                binding.checkSunday.isChecked,
                binding.checkMonday.isChecked,
                binding.checkTuesday.isChecked,
                binding.checkWednesday.isChecked,
                binding.checkThursday.isChecked,
                binding.checkFriday.isChecked,
                binding.checkSaturday.isChecked
            )

            if(binding.autoCompleteInput.text.toString() == ""
                || binding.editDate.text.toString() == ""
                || binding.editStartHour.text.toString() == ""
                || binding.editEndHour.text.toString() == ""
                || binding.editLocation.text.toString() == ""
                || repeatDays!!.none { day -> day }
            ) {

                Toast.makeText(this.context, resources.getText(R.string.warning_match_creation), Toast.LENGTH_LONG).show()

            } else {

                it.isEnabled = false

                viewModel.processMatchCreation(
                    chosenDate,
                    binding.editStartHour.text.toString(),
                    binding.editEndHour.text.toString(),
                    binding.pickerMin.value,
                    binding.pickerMax.value,
                    binding.editLocation.text.toString(),
                    this.repeatDays!!,
                    binding.pickerRepetitions.value
                )

                viewModel.processCheckSlots(
                    binding.editStartHour.text.toString(),
                    binding.editEndHour.text.toString(),
                    this.repeatDays!!
                )

                viewModel.getSelectedClubPlayers(binding.autoCompleteInput.text.toString())
            }
        }

        /* settings for teams' size pickers */

        binding.pickerMin.minValue = FormatUtils.MINIMUM_TEAM_SIZE
        binding.pickerMin.maxValue = FormatUtils.MAXIMUM_TEAM_SIZE
        binding.pickerMin.value = FormatUtils.MINIMUM_TEAM_SIZE

        binding.pickerMin.setOnValueChangedListener { _, _, newVal ->
            binding.pickerMax.minValue = newVal
        }

        binding.pickerMax.minValue = FormatUtils.MINIMUM_TEAM_SIZE
        binding.pickerMax.maxValue = FormatUtils.MAXIMUM_TEAM_SIZE
        binding.pickerMin.value = FormatUtils.MINIMUM_TEAM_SIZE

        binding.pickerMax.setOnValueChangedListener { _, _, newVal ->
            binding.pickerMin.maxValue = newVal
        }

        /* settings for repetitions picker */

        binding.pickerRepetitions.minValue = FormatUtils.MINIMUM_REPETITIONS
        binding.pickerRepetitions.maxValue = FormatUtils.MAXIMUM_REPETITIONS
        binding.pickerRepetitions.value = binding.pickerRepetitions.minValue

        /* observer for the status of the creation of 1 match */
        viewModel.matchCreationStatus.observe(this, Observer {
            it?.let {
                when(it) {
                    RequestStatus.ERROR -> {
                        Timber.i("Failed to create match")
                        viewModel.resetGetSlotStatus()
                    }
                    RequestStatus.LOADING -> Timber.i("Creating match")
                    else -> {
                        Timber.i("Match created")
                        viewModel.resetCreateMatchStatus()
                    }
                }
            }
        })

        /* observer for the status for getting 1 slot */
        viewModel.getSlotStatus.observe(this, Observer {
            it?.let {
                when(it) {
                    RequestStatus.ERROR -> {
                        Timber.i("Slot doesn't exist")
                        viewModel.resetGetSlotStatus()
                        viewModel.createSlot(
                            binding.editStartHour.text.toString(),
                            binding.editEndHour.text.toString(),
                            viewModel.currentDayForSlot.value!!)

                    }
                    RequestStatus.LOADING -> Timber.i("Getting slot")
                    else -> {
                        Timber.i("Slot exists")
                        viewModel.resetGetSlotStatus()
                    }
                }
            }
        })

        /* observer for creation of 1 slot */
        viewModel.createSlotStatus.observe(this, Observer {
            it?.let {
                when(it) {
                    RequestStatus.ERROR -> {
                        Timber.i("Error creating slot")
                        viewModel.resetCreateSlotStatus()
                    }
                    RequestStatus.LOADING -> Timber.i("Creating slot")
                    else -> {
                        Timber.i("Slot created")
                        viewModel.resetCreateSlotStatus()
                    }
                }
            }
        })

        /* observer for getting selected club players */
        viewModel.getClubPlayersStatus.observe(this, Observer {
            it?.let {
                when(it) {
                    RequestStatus.DONE -> {
                        Timber.i("Got players")
                        viewModel.finishAProcess()
                        viewModel.resetGetPlayersStatus()
                    }
                    RequestStatus.ERROR -> {
                        Timber.i("Error getting players")
                        viewModel.resetGetPlayersStatus()
                    }
                    else -> Timber.i("Getting players")
                }
            }
        })

        /* observers for the mutable lists in view model */
        /* needed to know when the process for meets and slots is done */

        viewModel.countMeets.observe(this, Observer {

            /* Total meets to be created in this match creation */
            /* equals to the number of repetition days selected times the number of repetitions + 1 */
            val totalToBeCreated: Int = repeatDays!!.filter { day -> day }.size * (binding.pickerRepetitions.value + 1)

            if(it > 0 && it == totalToBeCreated) {
                viewModel.finishAProcess()
                Timber.i("Match process finished")
            }
        })

        viewModel.countSlots.observe(this, Observer {

            /* Total of slots needed is equal to the number of repeat days selected */
            val totalNeeded = repeatDays!!.filter { day -> day }.size

            if(it > 0 && it == totalNeeded) {
                viewModel.finishAProcess()
                Timber.i("Slot process finished")
            }
        })

        /* Observer to launch creation of invitations when previous processes ended */

        viewModel.countFinishedProcesses.observe(this, Observer {
            if (it == 3) {
                viewModel.resetCounts()
                viewModel.sendInvitations()
            }
        })

        /* Observer for invitations creation */

        viewModel.invitationCreationStatus.observe(this, Observer {
            it?.let {
                when(it) {
                    RequestStatus.DONE -> {
                        Timber.i("Invitation created")
                        viewModel.resetCreateInvitationStatus()
                    }
                    RequestStatus.ERROR -> {
                        Timber.i("Failed to create invitation")
                        viewModel.resetCreateInvitationStatus()
                    }
                    else -> Timber.i("Creating invitation")
                }
            }
        })

        viewModel.countInvitations.observe(this, Observer {
            it?.let {
                val numberToCreate = viewModel.players.value!!.size * repeatDays!!.filter { day -> day }.size * (binding.pickerRepetitions.value + 1)
                if(it > 0 && it == numberToCreate) {
                    viewModel.resetCounts()
                    Toast.makeText(this.context, resources.getString(R.string.success_match_creation), Toast.LENGTH_SHORT).show()
                    this.findNavController().navigate(MatchCreationFragmentUIDirections.actionMatchCreationFragmentUIToMatchFragmentUI())
                }
            }
        })


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Créer un match"
    }
}
