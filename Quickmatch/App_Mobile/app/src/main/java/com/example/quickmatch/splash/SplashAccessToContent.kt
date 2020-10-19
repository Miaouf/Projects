package com.example.quickmatch.splash

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmatch.content.ContentActivity
import com.example.quickmatch.content.player
import com.example.quickmatch.network.PlayerObject

class SplashAccessToContent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* get player from login */
        val playerObject: PlayerObject = this.intent.getParcelableExtra("player")

        /* launch content activity asap with the player object */
        val intent = Intent(this, ContentActivity::class.java)
        intent.putExtra("player", playerObject)
        startActivity(intent)
        finish()
    }
}