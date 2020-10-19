package com.example.quickmatch.content.club


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
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
import com.example.quickmatch.databinding.FragmentClubCreationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * A simple [Fragment] subclass.
 */
class ClubCreationFragmentUI : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentClubCreationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_club_creation, container, false)
        val viewModel = ViewModelProviders.of(this).get(ClubCreationFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /* check format of the name given in input when lose focus */
        binding.editClubName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.checkClubNameFormat(binding.editClubName.text.toString())
            }
        })

        /* launch request for club creation */
        binding.createClubButton.setOnClickListener {
            viewModel.createClub(binding.editClubName.text.toString(),
                when(binding.checkPrivacyBox.isChecked) {
                    true -> true
                    false -> false
                })

        }

        viewModel.clubCreationStatus.observe(this, Observer {
            when(it) {
                ClubCreationStatus.DONE -> {
                    viewModel.doneCreatingClub() //nullify status when done
                    findNavController().navigate(ClubCreationFragmentUIDirections.actionClubCreationFragmentUIToClubFragmentUI())
                    val successToast = Toast.makeText(context, "Club créé avec succès", Toast.LENGTH_LONG)
                    successToast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, 200)
                    successToast.show()
                }
                ClubCreationStatus.ERROR -> {
                    //Text view with error
                }
                ClubCreationStatus.LOADING -> {
                    //loading icon
                }

            }
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Créér un club"
    }
}
