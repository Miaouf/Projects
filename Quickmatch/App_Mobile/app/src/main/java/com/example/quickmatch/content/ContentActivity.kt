package com.example.quickmatch.content

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.quickmatch.R
import com.example.quickmatch.access.AccessActivity
import com.example.quickmatch.content.agenda.AgendaFragmentUI
import com.example.quickmatch.content.club.ClubFragmentUI
import com.example.quickmatch.content.home.HomeFragmentUI
import com.example.quickmatch.content.match.MatchFragmentUI
import com.example.quickmatch.content.profile.ProfileFragmentUI
import com.example.quickmatch.content.stat.StatFragmentUI
import com.example.quickmatch.databinding.ActivityContentBinding
import com.example.quickmatch.network.PlayerObject
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_content.*
import timber.log.Timber

private lateinit var contentDrawerLayout: DrawerLayout
lateinit var player: PlayerObject
private lateinit var navigationView: NavigationView
private lateinit var navHeader: View

class ContentActivity : AppCompatActivity()/*, NavigationView.OnNavigationItemSelectedListener */{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Bind layout elements at compile time to be faster at runtime */
        val binding = DataBindingUtil.setContentView<ActivityContentBinding>(this, R.layout.activity_content)

        contentDrawerLayout = binding.drawerLayout
        player = intent.getParcelableExtra("player")
        navigationView = binding.navView
        navHeader = navigationView.getHeaderView(0)

        //navigationView.setNavigationItemSelectedListener { item -> onNavigationItemSelected(item) }

        /* Setup the navigation_access for fragments */
        val navController = this.findNavController(R.id.contentNavHostFragment)
        val fragmentManager = this.supportFragmentManager

        /* Setup navigation_access bar, with drawer menu */
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)


        //fragment = HomeFragmentUI()
    }

    /* Called when UP button is called : pop back stack */
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.contentNavHostFragment)
        /* Replace up button by drawer button on title screen */
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    /* Keep track of what the current fragment is like */
    var fragment : Fragment? = null

    /*
    /* Important to override this behavior because default doesnt add fragments to backstack when we navigate with drawer */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        Timber.i("Coucou")
        val itemId = item.itemId

        val fm = this.supportFragmentManager

        /* Clear stack */
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        /* Add a case for each item in the drawer */
        /* For each case, always add (push) a HomeFragment and then the fragment we navigate to */
        /* Then, going back will always return to Home because we cleared backstack before */
        when(itemId) {
            R.id.profileFragmentUI -> {
                fragment = ProfileFragmentUI()
                Timber.i(fragment!!.javaClass.toString())
            }
            R.id.agendaFragmentUI -> {
                fragment = AgendaFragmentUI()
            }
            R.id.clubFragmentUI -> {
                fragment = ClubFragmentUI()
            }
            R.id.statFragmentUI -> {
                fragment = StatFragmentUI()
            }
            R.id.matchFragmentUI -> {
                fragment = MatchFragmentUI()
            }
            R.id.settingFragmentUI -> {
                fragment = SettingFragmentUI()
            }
        }

        /* Add selected fragment to backstack when we navigate, and add home for back support */
        if (fragment != null) {
            fm.beginTransaction()
                    .add(HomeFragmentUI(), "HomeFragmentUI")
                    .addToBackStack("HomeFragmentUI")
                    .replace(R.id.contentNavHostFragment, fragment!!)
                    .commit()
        }

        return true
    }*/

    /*
    /* Override behavior of back button */
    override fun onBackPressed() {

        Timber.i(fragment!!.javaClass.toString())
        /* Case we are not on the home screen */
        if (fragment !is HomeFragmentUI) {
            super.onBackPressed()
        }

        else {

            /* Otherwise we return to the access activity */

            /* Dialog window to prevent accidental leaving */
            val backAlertDialog = android.app.AlertDialog.Builder(this)

            backAlertDialog.setTitle("Déconnexion")
            backAlertDialog.setMessage("Etes-vous sûr de vouloir quitter Quick Match ?")

            /* buttons YES/NO actions */
            backAlertDialog.setPositiveButton("Non") { dialog, which ->
                dialog.cancel()
            }

            backAlertDialog.setNegativeButton("Yes") { dialog, which ->
                val accessIntent = Intent(this, AccessActivity::class.java)
                accessIntent.flags = FLAG_ACTIVITY_CLEAR_TOP // Clear activity backstack
                startActivity(accessIntent) // Go back to access activity
                finish() // Finish this activity
            }

            backAlertDialog.show()
        }
    }*/
}