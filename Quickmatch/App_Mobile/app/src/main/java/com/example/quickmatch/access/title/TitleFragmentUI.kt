package com.example.quickmatch.access.title

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentTitleBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class TitleFragmentUI : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentTitleBinding  = DataBindingUtil.inflate(inflater, R.layout.fragment_title, container, false)
        val viewModel = ViewModelProviders.of(this).get(TitleFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.buttonTitleSignin.visibility = View.INVISIBLE
        binding.buttonTitleLogin.visibility = View.INVISIBLE

        // Snackbars for connexion test result display to the user

        val snackbarLoading: Snackbar = Snackbar
                .make(activity!!.findViewById(android.R.id.content), "Connexion au serveur en cours", Snackbar.LENGTH_SHORT)

        val snackbarDone: Snackbar = Snackbar
                .make(activity!!.findViewById(android.R.id.content), "Connexion au serveur réussie", Snackbar.LENGTH_LONG)

        val snackbarFailure: Snackbar = Snackbar
                .make(activity!!.findViewById(android.R.id.content), "Le test de connexion a échoué", Snackbar.LENGTH_INDEFINITE)
                .setAction("Réessayer") {
                    viewModel.resetConnectionStatus()
                    viewModel.tryToConnect()
                }

        // Navigation to login or signin fragments
        binding.buttonTitleLogin.setOnClickListener {
            findNavController().navigate(TitleFragmentUIDirections.actionTitleFragmentToLoginFragmentUI())
        }

        binding.buttonTitleSignin.setOnClickListener {
            findNavController().navigate(TitleFragmentUIDirections.actionTitleFragmentToSigninFragmentUI())
        }

        // Allow layout to observe directly view model live datas
        binding.lifecycleOwner = this

        // Show connexion status
        viewModel.status.observe(this, Observer {
            Timber.i("observer%s", it.toString())
            when(it) {
                ConnectionStatus.LOADING -> snackbarLoading.show()
                ConnectionStatus.ERROR -> {
                    Snackbar
                            .make(activity!!.findViewById(android.R.id.content), "La connexion au serveur a échoué", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Réessayer") {
                                viewModel.resetConnectionStatus()
                                viewModel.tryToConnect()
                            }.show()
                }
                ConnectionStatus.DONE -> {
                    snackbarDone.show()
                    binding.buttonTitleSignin.visibility = View.VISIBLE
                    binding.buttonTitleLogin.visibility = View.VISIBLE
                }
                else -> snackbarFailure.show()
            }
        })

        return binding.root
    }

}
