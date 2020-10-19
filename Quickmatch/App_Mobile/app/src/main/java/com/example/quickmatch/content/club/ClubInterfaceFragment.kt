package com.example.quickmatch.content.club

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quickmatch.BaseFragment
import com.example.quickmatch.R
import com.example.quickmatch.content.player
import com.example.quickmatch.databinding.FragmentClubInterfaceBinding
import com.example.quickmatch.network.PlayerAndPlayerBelongClubObject
import timber.log.Timber

class ClubInterfaceFragment : BaseFragment() {

    private lateinit var viewModelFactory: ClubInterfaceFragmentViewModelFactory
    private lateinit var viewModel: ClubInterfaceFragmentViewModel
    lateinit var loggedPlayerForThisClub: PlayerAndPlayerBelongClubObject

    private val args: ClubInterfaceFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentClubInterfaceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_club_interface, container, false)

        viewModelFactory = ClubInterfaceFragmentViewModelFactory(args.clubId)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ClubInterfaceFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /* Dialogs used later */

        val promoteDialog = AlertDialog.Builder(context!!)
        promoteDialog.setTitle("Promotion de joueur")
        promoteDialog.setMessage("Promouvoir au rang d'administrateur du club ?")
        promoteDialog.setNegativeButton("Non") { dialog, which ->
            dialog.cancel()
        }

        val excludeDialog = AlertDialog.Builder(context!!)
        excludeDialog.setTitle("Exlusion de joueur")
        excludeDialog.setMessage("Etes-vous sûr de vouloir exclure ce joueur ?")
        excludeDialog.setNegativeButton("Non") { dialog, which ->
            dialog.cancel()
        }

        val playerClickedDialog = AlertDialog.Builder(this.context!!)
        playerClickedDialog.setTitle("Choisir une action")
        playerClickedDialog.setNegativeButton("Annuler") { dialog, _ ->
            dialog.cancel()
        }

        /* adapter for player recycler view with click listener */

        val adapter = PlayerAdapter(PlayerClickListener { idClickedPlayer ->

            /* Set positive buttons which need clicked player id */

            promoteDialog.setPositiveButton("Oui") { dialog, _ ->
                viewModel.promotePlayer(idClickedPlayer!!)
                dialog.cancel()
            }
            excludeDialog.setPositiveButton("Oui") { dialog, _ ->
                viewModel.excludePlayer(idClickedPlayer!!)
                dialog.cancel()
            }

            /* Set items for actions popup according to logged player privileges */
            playerClickedDialog.setItems(R.array.options_player_array_admin
            ) { _, which ->
                when (which) {
                    //0 -> navigate to profile fragment
                    1 -> promoteDialog.show()
                    2 -> excludeDialog.show()
                }
            }
            playerClickedDialog.show()
        })

        binding.listPlayers.adapter = adapter

        viewModel.players.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.buttonLeaveClub.setOnClickListener {
            val leaveAlertDialog = AlertDialog.Builder(this.context!!)

            leaveAlertDialog.setTitle("Quitter")
            leaveAlertDialog.setMessage("Etes-vous sûr de vouloir quitter le club ?")

            /* buttons YES/NO actions */

            leaveAlertDialog.setPositiveButton("Oui") { dialog, _ ->
                viewModel.leaveClub()
                dialog.cancel()
            }

            leaveAlertDialog.setNegativeButton("Non") { dialog, _ ->
                dialog.cancel()
            }

            leaveAlertDialog.show()
        }

        viewModel.leaveClubStatus.observe(this, Observer {
            when (it) {
                RequestStatus.ERROR -> Timber.i("error leaving club")
                RequestStatus.DONE -> {
                    viewModel.onClubLeft()
                    findNavController().navigate(ClubInterfaceFragmentDirections.actionClubIntefaceFragmentToClubFragmentUI())
                }
            }
        })


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getActionBar()?.title = "Détails du club"
    }
}
