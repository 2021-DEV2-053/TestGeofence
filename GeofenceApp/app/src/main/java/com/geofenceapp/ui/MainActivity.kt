package com.geofenceapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.geofenceapp.R
import com.geofenceapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult


private const val RC_LOCATION_PERM = 1

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks,
    LocationListener
{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var launchLocationSettings = registerForActivityResult(StartActivityForResult()) { result ->
        locationTask()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        locationTask()
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    private fun hasLocationPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }
    private fun requestLocationPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.rationale_location),
            RC_LOCATION_PERM,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("TAG", "onPermissionsGranted:$requestCode")
        enableLocation()
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("TAG", "onPermissionsDenied:$requestCode")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .build()
                .show()
        }
        requestLocationPermissions()
    }
    @AfterPermissionGranted(RC_LOCATION_PERM)
    fun locationTask() {
        if (hasLocationPermissions()) {
            enableLocation()
        } else {
            requestLocationPermissions()
        }
    }
    private fun enableLocationSettings() {
        val settingsIntent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
        launchLocationSettings.launch(settingsIntent)
    }

    private fun enableLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            enableLocationSettings()
        }
        /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
             10000,          // 10-second interval.
             10f,             // 10 meters.
             this)*/
    }

    override fun onLocationChanged(position: Location) {
        Log.d("TAG", "onLocationChanged: $position")
    }

}