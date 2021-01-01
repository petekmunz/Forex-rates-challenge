package com.petermunyao.mobileandroidchallenge.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.petermunyao.mobileandroidchallenge.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var destinationListener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment).navController
        destinationListener =
            NavController.OnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
                if (destination.id == R.id.nav_rates) {
                    //supportActionBar!!.title = "Exchange Rates"
                } else if (destination.id == R.id.nav_main) {
                    supportActionBar!!.title = "Currency Converter"
                }
            }
        navController.addOnDestinationChangedListener(destinationListener)
    }
}