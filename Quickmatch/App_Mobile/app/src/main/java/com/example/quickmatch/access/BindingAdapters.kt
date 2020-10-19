package com.example.quickmatch.access

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.quickmatch.R
import com.example.quickmatch.access.signin.SigninCheckStatus
import timber.log.Timber

@BindingAdapter("signinCheckStatus")
fun bindStatusMail(imgView: ImageView, status: SigninCheckStatus?) {
    Timber.i("Get in adapter")
    Timber.i(status.toString())
    when(status) {
        SigninCheckStatus.VALID -> {
            imgView.visibility = View.VISIBLE
            imgView.setImageResource(R.drawable.ic_check_circle_white_24dp)
        }
        SigninCheckStatus.LOADING -> {
            imgView.visibility = View.VISIBLE
            imgView.setImageResource(R.drawable.status_loading_animation)
        }
        null -> {
            imgView.visibility = View.GONE
        }
        else -> {
            imgView.visibility = View.VISIBLE
            imgView.setImageResource(R.drawable.ic_remove_circle_outline_red_24dp)
        }
    }
}

@BindingAdapter("inputBasicFormat")
fun bindFormat(imgView: ImageView, status : Boolean?) {
    when(status) {
        true -> {
            imgView.visibility = View.VISIBLE
            imgView.setImageResource(R.drawable.ic_check_circle_white_24dp)
        }
        false -> {
            imgView.visibility = View.VISIBLE
            imgView.setImageResource(R.drawable.ic_remove_circle_outline_red_24dp)
        }
        null -> imgView.visibility = View.GONE
    }
}

