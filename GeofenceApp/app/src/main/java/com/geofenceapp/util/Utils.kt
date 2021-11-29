package com.geofenceapp.util

import android.app.Activity
import android.app.ActivityManager
import androidx.appcompat.app.AppCompatActivity

class Utils {
    companion object {
        fun isServiceRunning(activity: Activity): Boolean {
            val manager = activity.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if ("com.geofenceapp.core.GeofenceService" == service.service.className) {
                    return true
                }
            }
            return false
        }
    }
}