package com.example.data

import kotlin.math.*

object QiblaCalculator {
    const val KAABA_LATITUDE = 21.4225
    const val KAABA_LONGITUDE = 39.8262

    /**
     * Calculates the Qibla bearing from True North in degrees (0-360)
     */
    fun calculateBearing(userLat: Double, userLon: Double): Double {
        val userLatRad = Math.toRadians(userLat)
        val userLonRad = Math.toRadians(userLon)
        val kaabaLatRad = Math.toRadians(KAABA_LATITUDE)
        val kaabaLonRad = Math.toRadians(KAABA_LONGITUDE)

        val deltaLon = kaabaLonRad - userLonRad

        val y = sin(deltaLon)
        val x = cos(userLatRad) * tan(kaabaLatRad) - sin(userLatRad) * cos(deltaLon)

        var bearing = Math.toDegrees(atan2(y, x))
        if (bearing < 0) {
            bearing += 360.0
        }
        return bearing
    }

    /**
     * Calculates great-circle distance to Kaaba in kilometers
     */
    fun calculateDistanceKm(userLat: Double, userLon: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(KAABA_LATITUDE - userLat)
        val dLon = Math.toRadians(KAABA_LONGITUDE - userLon)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(KAABA_LATITUDE)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
