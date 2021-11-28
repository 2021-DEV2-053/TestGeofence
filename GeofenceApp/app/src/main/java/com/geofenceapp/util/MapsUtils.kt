package com.geofenceapp.util

import com.google.android.gms.maps.model.Circle

class MapsUtils {
    companion object {
        fun checkInside(circle: Circle, longitude: Double, latitude: Double): Boolean {
            return calculateDistance(
                circle.center.longitude, circle.center.latitude, longitude, latitude
            ) < circle.radius
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