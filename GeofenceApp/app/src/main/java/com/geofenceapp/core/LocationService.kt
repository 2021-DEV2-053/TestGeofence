package com.geofenceapp.core

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle

class LocationService : Service(), LocationListener {

    override fun onBind(intent: Intent?) = null

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)


        return START_STICKY
    }



    override fun onLocationChanged(location: Location) {
        println(location.toString())
    }

    override fun onProviderDisabled(provider: String) {

    }
    override fun onProviderEnabled(provider: String) {

    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }
}