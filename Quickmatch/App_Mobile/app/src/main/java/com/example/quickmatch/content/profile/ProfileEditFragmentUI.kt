package com.example.quickmatch.content.profile


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.quickmatch.BaseFragment

import com.example.quickmatch.R
import com.example.quickmatch.content.bindImage
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.content.player
import com.example.quickmatch.databinding.FragmentProfileEditBinding
import com.example.quickmatch.network.PlayerObject
import com.example.quickmatch.utils.FormatUtils
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */


class ProfileEditFragmentUI : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding : FragmentProfileEditBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_edit, container, false)
        val viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.getPlayerStatus.observe(viewLifecycleOwner, Observer {
            when(it) {
                RequestStatus.ERROR -> Toast.makeText(context, "Erreur de récupération des informations du joueur...", Toast.LENGTH_LONG).show()
                RequestStatus.DONE -> {
                    Timber.i(viewModel.playerObject.value.toString())
                    binding.checkPrivacyBox.isChecked = viewModel.playerObject.value!!.isPrivate == true
                    binding.checkPrivacyBox.text = if(binding.checkPrivacyBox.isChecked) context!!.getString(R.string.private_text) else context!!.getString(R.string.public_text)
                    binding.previewImage.bindImage(viewModel.playerObject.value!!.avatar)
                }
                else -> Timber.i("Retrieving player...")
            }
        })


        /* set format check methods to each edit */

        binding.editName.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                viewModel.checkFormatName(binding.editName.text.toString())
        }

        viewModel.nameFormat.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(!it) {
                    binding.editName.text.clear()
                    Toast.makeText(context, "Le nom doit faire entre 1 et 20 caractères", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.editFirstName.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                viewModel.checkFormatFirstName(binding.editFirstName.text.toString())
        }

        viewModel.firstNameFormat.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(!it) {
                    binding.editFirstName.text.clear()
                    Toast.makeText(context, "Le prénom doit faire entre 1 et 20 caractères", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.editPseudo.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                viewModel.checkFormatPseudo(binding.editPseudo.text.toString())
        }

        viewModel.pseudoFormat.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(!it) {
                    binding.editPseudo.text.clear()
                    Toast.makeText(context, "Le pseudo doit faire entre 1 et 20 caractères", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.editMail.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                viewModel.checkFormatMail(binding.editMail.text.toString())
        }

        viewModel.mailFormat.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(!it) {
                    binding.editMail.text.clear()
                    Toast.makeText(context, "L'adresse mail doit faire entre 1 et 50 caractères et être valide", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.editPhone.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                viewModel.checkFormatPhoneNumber(binding.editPhone.text.toString())
        }

        viewModel.phoneNumberFormat.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(!it) {
                    binding.editPhone.text.clear()
                    Toast.makeText(context, "Le numéro de téléphone doit être au format français", Toast.LENGTH_SHORT).show()
                }
            }
        })


        binding.checkPrivacyBox.setOnCheckedChangeListener { checkBox, isChecked ->
            checkBox.text = if(isChecked) context!!.getString(R.string.private_text) else context!!.getString(R.string.public_text) }

        binding.editAvatar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.previewImage.bindImage(s.toString())
            }
        })

        binding.buttonSave.setOnClickListener {

            if(binding.editFirstName.text.toString().matches(FormatUtils.emptyFieldPattern)
                || binding.editName.text.toString().matches(FormatUtils.emptyFieldPattern)
                || binding.editPseudo.text.toString().matches(FormatUtils.emptyFieldPattern)
                || binding.editMail.text.toString().matches(FormatUtils.emptyFieldPattern))

                Toast.makeText(context, "Au moins un des champs obligatoires (nom, prénom, pseudo, mail) est vide...", Toast.LENGTH_LONG).show()

            else {
                viewModel.checkChanges(binding.editPseudo.text.toString(), binding.editMail.text.toString(), binding.editPhone.text.toString())
            }
        }

        /* observers for unique fields check before updating the player */

        viewModel.mailStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it) {
                    RequestStatus.DONE -> { //case we got the logged player because the mail stayed unchanged
                        if(viewModel.playerMail.value!!.id == player.id) {
                            if(((viewModel.phoneStatus.value == PhoneRequestStatus.DONE && viewModel.playerPhone.value!!.id == player.id)
                                        || viewModel.phoneStatus.value == PhoneRequestStatus.ERROR
                                        || viewModel.phoneStatus.value == PhoneRequestStatus.EMPTY)
                                && ((viewModel.pseudoStatus.value == RequestStatus.DONE && viewModel.playerPseudo.value!!.id == player.id)
                                        || viewModel.pseudoStatus.value == RequestStatus.ERROR)
                            ) {

                                viewModel.updatePlayer(
                                    binding.editName.text.toString(),
                                    binding.editFirstName.text.toString(),
                                    binding.editPseudo.text.toString(),
                                    binding.editMail.text.toString(),
                                    binding.editPhone.text.toString(),
                                    binding.editAvatar.text.toString(),
                                    binding.editBio.text.toString(),
                                    binding.checkPrivacyBox.isChecked
                                )
                                viewModel.resetCheckStatus()
                            }
                        } else {
                            Toast.makeText(context, "L'adresse mail choisie est déja utilisé par un autre joueur", Toast.LENGTH_SHORT).show()
                        }
                    }
                    RequestStatus.LOADING -> Timber.i("Checking mail address...")
                    else -> {
                        if (((viewModel.phoneStatus.value == PhoneRequestStatus.DONE && viewModel.playerPhone.value!!.id == player.id)
                                    || viewModel.phoneStatus.value == PhoneRequestStatus.ERROR
                                    || viewModel.phoneStatus.value == PhoneRequestStatus.EMPTY)
                            && ((viewModel.pseudoStatus.value == RequestStatus.DONE && viewModel.playerPseudo.value!!.id == player.id)
                                    || viewModel.pseudoStatus.value == RequestStatus.ERROR)
                        ) {
                            viewModel.updatePlayer(
                                binding.editName.text.toString(),
                                binding.editFirstName.text.toString(),
                                binding.editPseudo.text.toString(),
                                binding.editMail.text.toString(),
                                binding.editPhone.text.toString(),
                                binding.editAvatar.text.toString(),
                                binding.editBio.text.toString(),
                                binding.checkPrivacyBox.isChecked
                            )
                            viewModel.resetCheckStatus()
                        }
                    }
                }
            }

        })

        viewModel.pseudoStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it) {
                    RequestStatus.DONE -> { //case we got the logged player because the mail stayed unchanged
                        if(viewModel.playerPseudo.value!!.id == player.id) {
                            if(((viewModel.phoneStatus.value == PhoneRequestStatus.DONE && viewModel.playerPhone.value!!.id == player.id)
                                        || viewModel.phoneStatus.value == PhoneRequestStatus.ERROR
                                        || viewModel.phoneStatus.value == PhoneRequestStatus.EMPTY)
                                && ((viewModel.mailStatus.value == RequestStatus.DONE && viewModel.playerMail.value!!.id == player.id)
                                        || viewModel.mailStatus.value == RequestStatus.ERROR)
                            ) {
                                    viewModel.updatePlayer(
                                        binding.editName.text.toString(),
                                        binding.editFirstName.text.toString(),
                                        binding.editPseudo.text.toString(),
                                        binding.editMail.text.toString(),
                                        binding.editPhone.text.toString(),
                                        binding.editAvatar.text.toString(),
                                        binding.editBio.text.toString(),
                                        binding.checkPrivacyBox.isChecked)
                                viewModel.resetCheckStatus()
                            }
                        } else {
                            Toast.makeText(context, "Le pseudo choisi est déja utilisé par un autre joueur", Toast.LENGTH_SHORT).show()
                        }

                    }
                    RequestStatus.LOADING -> Timber.i("Checking pseudo...")
                    else -> {
                        if (((viewModel.phoneStatus.value == PhoneRequestStatus.DONE && viewModel.playerPhone.value!!.id == player.id)
                                    || viewModel.phoneStatus.value == PhoneRequestStatus.ERROR
                                    || viewModel.phoneStatus.value == PhoneRequestStatus.EMPTY)
                            && ((viewModel.mailStatus.value == RequestStatus.DONE && viewModel.playerMail.value!!.id == player.id)
                                    || viewModel.mailStatus.value == RequestStatus.ERROR)
                        ) {
                            viewModel.updatePlayer(
                                binding.editName.text.toString(),
                                binding.editFirstName.text.toString(),
                                binding.editPseudo.text.toString(),
                                binding.editMail.text.toString(),
                                binding.editPhone.text.toString(),
                                binding.editAvatar.text.toString(),
                                binding.editBio.text.toString(),
                                binding.checkPrivacyBox.isChecked
                            )
                            viewModel.resetCheckStatus()
                        }
                    }
                }
            }

        })

        viewModel.phoneStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it) {
                    PhoneRequestStatus.DONE -> { //case phone given and got a player checking it
                        if(viewModel.playerPhone.value?.id == player.id) { //update only if its our logged player
                            if (((viewModel.pseudoStatus.value == RequestStatus.DONE && viewModel.playerPseudo.value!!.id == player.id)
                                        || viewModel.pseudoStatus.value == RequestStatus.ERROR)
                                && ((viewModel.mailStatus.value == RequestStatus.DONE && viewModel.playerMail.value!!.id == player.id)
                                        || viewModel.mailStatus.value == RequestStatus.ERROR)
                            ) {
                                viewModel.updatePlayer(
                                    binding.editName.text.toString(),
                                    binding.editFirstName.text.toString(),
                                    binding.editPseudo.text.toString(),
                                    binding.editMail.text.toString(),
                                    binding.editPhone.text.toString(),
                                    binding.editAvatar.text.toString(),
                                    binding.editBio.text.toString(),
                                    binding.checkPrivacyBox.isChecked
                                )
                                viewModel.resetCheckStatus()
                            }
                        } else {
                            Toast.makeText(context, "Le numéro de téléphone choisi est déja utilisé par un autre joueur", Toast.LENGTH_SHORT).show()
                        }
                    }
                    PhoneRequestStatus.LOADING -> Timber.i("Checking phone number...")
                    PhoneRequestStatus.EMPTY -> { // case no phone given
                        if (viewModel.pseudoStatus.value == RequestStatus.DONE
                            && viewModel.mailStatus.value == RequestStatus.DONE
                        ) {
                            viewModel.updatePlayer(
                                binding.editName.text.toString(),
                                binding.editFirstName.text.toString(),
                                binding.editPseudo.text.toString(),
                                binding.editMail.text.toString(),
                                null,
                                binding.editAvatar.text.toString(),
                                binding.editBio.text.toString(),
                                binding.checkPrivacyBox.isChecked)
                            viewModel.resetCheckStatus()
                        }
                    }
                    else -> { // case new phone not used
                        if (((viewModel.pseudoStatus.value == RequestStatus.DONE && viewModel.playerPseudo.value!!.id == player.id)
                                    || viewModel.pseudoStatus.value == RequestStatus.ERROR)
                            && ((viewModel.mailStatus.value == RequestStatus.DONE && viewModel.playerMail.value!!.id == player.id)
                                    || viewModel.mailStatus.value == RequestStatus.ERROR)
                        ) {
                            viewModel.updatePlayer(
                                binding.editName.text.toString(),
                                binding.editFirstName.text.toString(),
                                binding.editPseudo.text.toString(),
                                binding.editMail.text.toString(),
                                binding.editPhone.text.toString(),
                                binding.editAvatar.text.toString(),
                                binding.editBio.text.toString(),
                                binding.checkPrivacyBox.isChecked)
                            viewModel.resetCheckStatus()
                        }
                    }
                }
            }

        })

        /* observer for update request */
        viewModel.updateStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it) {
                    RequestStatus.LOADING -> Timber.i("Updating player...")
                    RequestStatus.DONE -> {
                        findNavController().navigate(ProfileEditFragmentUIDirections.actionProfileEditFragmentUIToProfileFragmentUI())
                        Toast.makeText(context, "Profil mis à jour avec succès !", Toast.LENGTH_SHORT).show()
                    }
                    else -> Toast.makeText(context, "Erreur de mise à jour de votre profil, réessayez ou contactez le support", Toast.LENGTH_LONG).show()
                }
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Modifier mon profil"
    }
}
