package com.geofenceapp.ui

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.geofenceapp.R
import com.geofenceapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import android.location.LocationProvider
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider: LocationProvider? = locationManager.getProvider(LocationManager.GPS_PROVIDER)
        if(provider != null){

        }

        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            // call enableLocationSettings()
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            10000,          // 10-second interval.
            10f,             // 10 meters.
            this);

    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onLocationChanged(position: Location) {
        Log.d("TAG", "onLocationChanged: $position")
    }

    private fun enableLocationSettings() {
        val settingsIntent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

}