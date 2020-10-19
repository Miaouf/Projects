package com.example.quickmatch.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmatch.access.AccessActivity

class SplashLaunch : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, AccessActivity::class.java)
        startActivity(intent)
        finish()
    }
}