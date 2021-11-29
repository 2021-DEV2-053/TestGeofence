package com.geofenceapp.util

import android.app.Notification
import android.content.Context

import androidx.core.app.NotificationCompat
import com.geofenceapp.R

import androidx.core.app.NotificationManagerCompat

const val GEOFENCEAPP_NOTIFICATION_ID = 1
const val ENTER_GEOFENCE_NOTIFICATION_ID = 2
const val EXIT_GEOFENCE_NOTIFICATION_ID = 3

class NotificationUtils {
    companion object {
        fun locationBackgroundNotification(ctx: Context): Notification {
            return NotificationCompat.Builder(ctx, CHANNEL_LOCATION_ID)
                .setSmallIcon(R.drawable.ic_baseline_my_location_24)
                .setContentTitle(ctx.getString(R.string.GeofenceApp_is_running))
                .setOngoing(true)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build()
        }

        fun enterGeofenceNotification(ctx: Context) {
            NotificationManagerCompat.from(ctx).notify(
                ENTER_GEOFENCE_NOTIFICATION_ID,
                NotificationCompat.Builder(ctx, CHANNEL_ENTER_GEOFENCE_ID)
                    .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                    .setContentTitle(ctx.getString(R.string.enter_in_Geofence))
                    .setAutoCancel(false)
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build()
            )
        }

        fun exitGeofenceNotification(ctx: Context) {
            NotificationManagerCompat.from(ctx).notify(
                EXIT_GEOFENCE_NOTIFICATION_ID,
                NotificationCompat.Builder(ctx, CHANNEL_EXIT_GEOFENCE_ID)
                    .setSmallIcon(R.drawable.ic_baseline_wrong_location_24)
                    .setContentTitle(ctx.getString(R.string.exit_from_Geofence))
                    .setAutoCancel(false)
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build()
            )
        }
    }

}