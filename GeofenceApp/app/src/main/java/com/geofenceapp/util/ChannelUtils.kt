package com.geofenceapp.util

import android.app.Notification.VISIBILITY_PUBLIC
import android.content.Context
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import androidx.annotation.RequiresApi
import com.geofenceapp.R

const val CHANNEL_LOCATION_ID = "CHANNEL_LOCATION_ID"
const val CHANNEL_ENTER_GEOFENCE_ID = "CHANNEL_ENTER_GEOFENCE_ID"
const val CHANNEL_EXIT_GEOFENCE_ID = "CHANNEL_EXIT_GEOFENCE_ID"

class ChannelUtils {
    companion object {
        /**
         * for API 26+ create notification channels
         */
        fun createAllChannels(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                locationBackgroundChannel(ctx)

                enterGeofenceChannel(ctx)
                exitGeofenceChannel(ctx)
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun locationBackgroundChannel(ctx: Context) {
            val channel = NotificationChannel(
                CHANNEL_LOCATION_ID,
                ctx.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_NONE
            )
            val manager = ctx.getSystemService(
                NotificationManager::class.java
            )
            manager?.createNotificationChannel(channel)
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun enterGeofenceChannel(ctx: Context) {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mChannel = NotificationChannel(
                CHANNEL_ENTER_GEOFENCE_ID,
                ctx.getString(R.string.Geofence_notification),
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = ctx.getString(R.string.enter_in_Geofence)
            mChannel.lockscreenVisibility = VISIBILITY_PUBLIC
            mChannel.lightColor = R.color.purple_500
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            nm.createNotificationChannel(mChannel)
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun exitGeofenceChannel(ctx: Context) {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mChannel = NotificationChannel(
                CHANNEL_EXIT_GEOFENCE_ID,
                ctx.getString(R.string.Geofence_notification),
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = ctx.getString(R.string.exit_from_Geofence)
            mChannel.lockscreenVisibility = VISIBILITY_PUBLIC
            mChannel.lightColor = R.color.teal_700
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            nm.createNotificationChannel(mChannel)
        }
    }
}