package com.geofenceapp.data.model

data class Geofence (
    /**
     * latitude in degrees
     */
    var latitude: Double = 0.0,
    /**
     * longitude in degrees
     */
    var longitude: Double = 0.0,
    /**
     * Radius in meters.
     */
    var radius: Double = 0.0,
)