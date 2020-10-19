package com.example.quickmatch.content.invitation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentInvitationNewBinding
import timber.log.Timber

class InvitationNewFragment() : InvitationFragmentUI() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentInvitationNewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invitation_new, container, false)
        val viewModel = ViewModelProviders.of(this).get(InvitationFragmentViewModel::class.java)

        val adapterNew = InvitationNewAdapter(
            InvitationNewClickListenerAccept {
                val acceptDialog = AlertDialog.Builder(context)
                acceptDialog.setTitle("Invitation")
                acceptDialog.setMessage("Accepter cette invitation ?")
                acceptDialog.setPositiveButton("Oui") { _, _ ->
                    viewModel.acceptInvitation(it!!)
                }
                acceptDialog.setNegativeButton("Annuler") { dialog, _ ->
                    dialog.cancel()
                }
                acceptDialog.show()
            },
            InvitationNewClickListenerDecline {
                val declineDialog = AlertDialog.Builder(context)
                declineDialog.setTitle("Invitation")
                declineDialog.setMessage("Refuser cette invitation ?")
                declineDialog.setPositiveButton("Oui") { _, _ ->
                    viewModel.declineInvitation(it!!)
                }
                declineDialog.setNegativeButton("Annuler") { dialog, _ ->
                    dialog.cancel()
                }
                declineDialog.show()
            }
        )

        binding.recyclerView.adapter = adapterNew
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.newInvitations.observe(this, Observer {
            it?.let {
                adapterNew.submitList(it)
            }
        })

        return binding.root
    }
}