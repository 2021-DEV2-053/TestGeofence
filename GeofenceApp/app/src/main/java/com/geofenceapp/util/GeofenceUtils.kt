package com.geofenceapp.util

import com.google.android.gms.maps.model.LatLng

class GeofenceUtils {
    companion object {
        fun checkInside(
            radius: Double, center: LatLng, currentPosition: LatLng
        ): Boolean {
            return calculateDistance(
                center.longitude, center.latitude, currentPosition.longitude, currentPosition.latitude
            ) < radius
        }
        fun calculateDistance(
            longitude1: Double, latitude1: Double,
            longitude2: Double, latitude2: Double
        ): Double {
            var c = Math.sin(Math.toRadians(latitude1)) *
                    Math.sin(Math.toRadians(latitude2)) +
                    Math.cos(Math.toRadians(latitude1)) *
                    Math.cos(Math.toRadians(latitude2)) *
                    Math.cos(
                        Math.toRadians(longitude2) -
                                Math.toRadians(longitude1)
                    )
            c = if (c > 0) Math.min(1.0, c) else Math.max(-1.0, c)
            return 3959 * 1.609 * 1000 * Math.acos(c)
        }
    }


}