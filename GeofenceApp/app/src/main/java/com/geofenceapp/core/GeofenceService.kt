package com.geofenceapp.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.geofenceapp.data.model.Geofence
import com.geofenceapp.util.GEOFENCEAPP_NOTIFICATION_ID
import com.geofenceapp.util.GeofenceUtils.Companion.checkInside
import com.geofenceapp.util.NotificationUtils.Companion.enterGeofenceNotification
import com.geofenceapp.util.NotificationUtils.Companion.exitGeofenceNotification
import com.geofenceapp.util.NotificationUtils.Companion.locationBackgroundNotification
import com.google.android.gms.maps.model.LatLng

const val CURRENT_LOCATION_UPDATED = "CURRENT_LOCATION_UPDATED"
const val ACTION_START = "ACTION_START"
const val ACTION_STOP = "ACTION_STOP"
/*
 * this is a service that prompts itself to a foreground service with a persistent
 * notification.  Which is now required by Oreo otherwise, a background service without an app will be killed.
 *
 */
class GeofenceService: Service(), LocationListener {

    private var locationManager: LocationManager?= null

    //Geofence data
    private var radius: Double = 0.0
    private var center: LatLng? = null
    private var isInside: Boolean? = null

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        startForeground(GEOFENCEAPP_NOTIFICATION_ID, locationBackgroundNotification(applicationContext))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action: String? = intent.getAction()
            if (action != null) {
                when (action) {
                    ACTION_START -> {
                        radius = intent.getDoubleExtra("radius", 0.0)
                        center = LatLng(
                            intent.getDoubleExtra("latitude", 0.0),
                            intent.getDoubleExtra("longitude", 0.0)
                        )
                        startGeofence()
                    }
                    ACTION_STOP -> stopGeofence()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startGeofence() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1f,   this)
    }
    private fun stopGeofence() {
        locationManager!!.removeUpdates(this)
        stopSelf()
    }
    override fun onLocationChanged(location: Location) {
        Log.d("TAG", "onLocationChanged: $location")

        val newValue = checkInside(radius, center!!, LatLng(location.latitude, location.longitude))

        if(isInside != null && isInside != newValue){
            isInside = newValue
            if(isInside!!){
                enterGeofenceNotification(this)
            }else{
                exitGeofenceNotification(this)
            }
        }
    }

    companion object {
        fun start(context: Context, geofence: Geofence) {
            val intent = Intent(context, GeofenceService::class.java)
            intent.putExtra("radius", geofence.radius)
            intent.putExtra("latitude", geofence.latitude)
            intent.putExtra("longitude", geofence.longitude)
            intent.action = ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        fun stop(context: Context) {
            val intent = Intent(context, GeofenceService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }
    }

}