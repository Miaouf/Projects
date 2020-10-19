package com.example.quickmatch.access.signin


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.example.quickmatch.R
import com.example.quickmatch.content.ContentActivity
import com.example.quickmatch.databinding.FragmentSigninBinding
import com.example.quickmatch.splash.SplashAccessToContent
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */

class SigninFragmentUI : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentSigninBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signin, container, false)
        val viewModel = ViewModelProvider(this).get(SigninFragmentViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /* Navigation to login fragment */
        binding.buttonSigninLogin.setOnClickListener {
            findNavController().navigate(SigninFragmentUIDirections.actionSigninFragmentUIToLoginFragmentUI())
        }

        /* Launch signin request */
        binding.buttonSigninSignin.setOnClickListener {
                viewModel.tryToSignIn(
                    binding.inputSigninName.text.toString(),
                    binding.inputSigninFirstname.text.toString(),
                    binding.inputSigninPseudo.text.toString(),
                    binding.inputSigninMail.text.toString(),
                    binding.inputSigninPwd.text.toString(),
                    binding.inputSigninConfirmPwd.text.toString(),
                    binding.inputSigninPhone.text.toString()
                )
        }

        /* Lauch check requests */
        binding.buttonSigninCheck.setOnClickListener {
            viewModel.checkUnicity(binding.inputSigninMail.text.toString(),
                binding.inputSigninPseudo.text.toString(),
                binding.inputSigninPhone.text.toString())
        }

        /* Observer for signin authorization */
        viewModel.authorizedToSignin.observe(this, Observer {
            binding.buttonSigninSignin.isEnabled = it
            binding.buttonSigninCheck.isEnabled = !it
        })


        /* Observer for the signin status in the viewModel */
        viewModel.signinStatus.observe(this, Observer {
            when (it) {
                SigninStatus.NETWORK_ERROR -> {
                    binding.textSigninStatus.visibility = View.VISIBLE
                    binding.textSigninStatus.text = "Erreur d'inscription"
                }
                SigninStatus.DONE -> {
                    val intentContent = Intent(this.activity, SplashAccessToContent::class.java)
                    intentContent.putExtra("player", viewModel.player)

                    /* disable keyboard when navigating to the app */
                    val inputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.applicationWindowToken, 0)

                    this.activity!!.finish()
                    startActivity(intentContent)

                }
                else -> binding.textSigninStatus.visibility = View.GONE
            }
        })


        /* Focus observers to check fields format */

        binding.inputSigninPseudo.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkFormatPseudo(binding.inputSigninPseudo.text.toString())
            }
        }

        binding.inputSigninName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkFormatName(binding.inputSigninName.text.toString())
            }
        }

        binding.inputSigninFirstname.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkFormatFirstName(binding.inputSigninFirstname.text.toString())
            }
        }

        binding.inputSigninMail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkFormatMail(binding.inputSigninMail.text.toString())
            }
        }

        binding.inputSigninPwd.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkFormatPassword(binding.inputSigninPwd.text.toString())
            }
        }

        binding.inputSigninConfirmPwd.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.checkFormatPasswordCheck(
                    binding.inputSigninPwd.text.toString(),
                    binding.inputSigninConfirmPwd.text.toString()
                )
            }
        }

        binding.inputSigninPhone.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && binding.inputSigninPhone.text.toString() != "") {
                viewModel.checkPhoneNumberFormat(binding.inputSigninPhone.text.toString())
            }
        }

        /* Observer of unicity checks, change the state each time there is a change */
        viewModel.mailStatus.observe(this, Observer {
            viewModel.checkUnicityState()
        })

        viewModel.pseudoStatus.observe(this, Observer {
            viewModel.checkUnicityState()
        })

        viewModel.phoneNumberStatus.observe(this, Observer {
            viewModel.checkUnicityState()
        })

        /* change observers for unique fields, in order to disable sign in without a re-check */

        /* Text watcher object which is the same for all fields */
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonSigninCheck.isEnabled = true
                binding.buttonSigninSignin.isEnabled = false
            }
        }

        binding.inputSigninMail.addTextChangedListener(watcher)
        binding.inputSigninPhone.addTextChangedListener(watcher)
        binding.inputSigninPseudo.addTextChangedListener(watcher)

        return binding.root
    }


}
