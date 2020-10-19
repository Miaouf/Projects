package com.example.quickmatch.content.invitation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.quickmatch.R
import com.example.quickmatch.databinding.FragmentInvitationTreatedBinding
import timber.log.Timber

class InvitationTreatedFragment : InvitationFragmentUI() {

    private lateinit var viewModel: InvitationFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentInvitationTreatedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invitation_treated, container, false)
        viewModel = ViewModelProviders.of(this).get(InvitationFragmentViewModel::class.java)

        val adapterTreated = InvitationTreatedAdapter(InvitationTreatedClickListener {
            val changeDialog = AlertDialog.Builder(context)
            changeDialog.setTitle("Modifier mon choix")
            changeDialog.setPositiveButton("Accepter") { _, _ ->
                viewModel.acceptInvitation(it!!)
            }
            changeDialog.setNegativeButton("Refuser") { _, _ ->
                viewModel.declineInvitation(it!!)
            }
            changeDialog.setNeutralButton("Annuler") { dialog, _ ->
                dialog.cancel()
            }
            changeDialog.show()
        })

        binding.recyclerView.adapter = adapterTreated
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.treatedInvitations.observe(this, Observer {
            it?.let {
                adapterTreated.submitList(it)
            }
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTreatedInvitations()
    }
}