package com.geofenceapp.core

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import com.geofenceapp.data.model.Geofence
import com.geofenceapp.util.GEOFENCEAPP_NOTIFICATION_ID
import com.geofenceapp.util.GeofenceUtils.Companion.checkInside
import com.geofenceapp.util.NotificationUtils.Companion.enterGeofenceNotification
import com.geofenceapp.util.NotificationUtils.Companion.exitGeofenceNotification
import com.geofenceapp.util.NotificationUtils.Companion.locationBackgroundNotification
import com.google.android.gms.maps.model.LatLng

const val ACTION_START = "ACTION_START"
const val ACTION_STOP = "ACTION_STOP"
/**
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

    //LocationListener data
    private val MILLISECONDS_PER_SECOND = 1000
    private val UPDATE_INTERVAL_IN_SECONDS = 5
    private val UPDATE_INTERVAL = (MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS).toLong()

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
    /**
     * Start the location service with an Interval define in this constant 'UPDATE_INTERVAL'
     *
     */
    private fun startGeofence() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            UPDATE_INTERVAL,
            1f,
            this
        )
    }
    /**
     * Stop the location listener and kill the service
     *
     */
    private fun stopGeofence() {
        locationManager!!.removeUpdates(this)
        stopSelf()
    }
    /**
     *Check if the user enters or exits the geofence
     *
     */
    override fun onLocationChanged(location: Location) {
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
    /**
     * Start or stop the background service from Activity.
     *
     */
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