package cz.jscelectronics.adeon.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.*
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.billingrepo.BillingRepository
import cz.jscelectronics.adeon.data.PrefManager
import cz.jscelectronics.adeon.databinding.ActivityMainBinding
import cz.jscelectronics.adeon.ui.onboarding.IntroActivity
import cz.jscelectronics.adeon.ui.onboarding.WhatsNewActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val repository: BillingRepository by lazy {
        BillingRepository.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Onboarding
        val prefManager = PrefManager(this)
        if (!prefManager.firstTimeOnboardingShown()) {
            prefManager.setOnboardingCompleted()
            val onboardingIntent = Intent(this@MainActivity, IntroActivity::class.java)
            startActivity(onboardingIntent)
        } else if (!prefManager.whatsNewOnboardingShown()) {
            prefManager.setOnboardingCompleted()
            val onboardingIntent = Intent(this@MainActivity, WhatsNewActivity::class.java)
            startActivity(onboardingIntent)
        }

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        drawerLayout = binding.drawerLayout

        navController = Navigation.findNavController(this, R.id.device_list_nav_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.device_list_fragment, R.id.help_fragment, R.id.makePurchaseFragment), drawerLayout)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)

        binding.navigationView.setNavigationItemSelectedListener { item ->
            val bundle = bundleOf("action" to item.itemId)

            val handled = when(item.itemId) {
                R.id.action_import -> {
                    navController.navigate(R.id.device_list_fragment, bundle)
                    true
                }
                R.id.action_export -> {
                    navController.navigate(R.id.device_list_fragment, bundle)
                    true
                }
                R.id.action_delete -> {
                    navController.navigate(R.id.device_list_fragment, bundle)
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

        repository.noAdvertisementsLiveData.observe(this, Observer {
            it?.apply {
                if (entitled) {
                    binding.navigationView.menu.removeGroup(R.id.store)
                }
            }
        })
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