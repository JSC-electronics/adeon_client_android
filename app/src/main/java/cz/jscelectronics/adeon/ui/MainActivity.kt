package cz.jscelectronics.adeon.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import cz.jscelectronics.adeon.R
import android.content.Intent
import androidx.navigation.ui.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import cz.jscelectronics.adeon.data.PrefManager
import cz.jscelectronics.adeon.databinding.ActivityMainBinding
import cz.jscelectronics.adeon.ui.help.content.dialogs.AboutDialogFragment
import cz.jscelectronics.adeon.ui.intro.IntroActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefManager = PrefManager(this)
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false)
            val introIntent = Intent(this@MainActivity, IntroActivity::class.java)
            startActivity(introIntent)
        }

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        drawerLayout = binding.drawerLayout

        navController = Navigation.findNavController(this, R.id.device_list_nav_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.device_list_fragment, R.id.help_fragment), drawerLayout)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)

        binding.navigationView.setNavigationItemSelectedListener { item ->
            val handled = when(item.itemId) {
                R.id.action_import -> {
                    navController.navigate(R.id.device_list_fragment)
                    true
                }
                R.id.action_export -> {
                    navController.navigate(R.id.device_list_fragment)
                    true
                }
                R.id.action_delete -> {
                    navController.navigate(R.id.device_list_fragment)
                    true
                }
                else -> {
                    // Fallback for all other (normal) cases.
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            }

            // This is usually done by the default ItemSelectedListener.
            // But there can only be one! Unfortunately.
            if (handled) binding.drawerLayout.closeDrawer(binding.navigationView)

            // return whether the action was consumed
            handled
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}