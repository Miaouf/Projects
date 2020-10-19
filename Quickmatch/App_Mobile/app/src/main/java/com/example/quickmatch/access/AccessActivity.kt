package com.example.quickmatch.access

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.example.quickmatch.R
import com.example.quickmatch.databinding.ActivityAccessBinding


class AccessActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Bind layout elements at compile time to be faster at runtime */
        val binding = DataBindingUtil.setContentView<ActivityAccessBinding>(this, R.layout.activity_access)

        /* Setup the navigation_access for fragments */
        val navController = this.findNavController(R.id.accessNavHostFragment)

        supportActionBar!!.hide()
    }

}
