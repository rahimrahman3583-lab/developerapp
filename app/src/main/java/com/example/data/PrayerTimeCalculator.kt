package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

data class PrayerTimesResult(
    val dateStr: String,
    val locationName: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val nextPrayerName: String,
    val nextPrayerTime: String
)

/**
 * Astronomical solar calculation for Islamic Prayer Times.
 * Uses Karachi / University of Islamic Sciences convention (Fajr 18°, Isha 18°),
 * standard for Bangladesh and South Asia.
 */
object PrayerTimeCalculator {

    fun calculate(
        latitude: Double = 23.8103, // Default Dhaka
        longitude: Double = 90.4125,
        timeZoneOffsetHours: Double = 6.0,
        locationName: String = "Dhaka, Bangladesh"
    ): PrayerTimesResult {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val dateFormatted = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())

        // Approximate solar calculations
        val b = 2.0 * Math.PI * (dayOfYear - 81) / 364.0
        val eot = 9.87 * sin(2.0 * b) - 7.53 * cos(b) - 1.5 * sin(b) // equation of time in minutes
        val declination = 23.45 * sin(2.0 * Math.PI * (284 + dayOfYear) / 365.0) // solar declination in degrees

        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)

        // Solar noon in hours (UTC)
        val solarNoonUtc = 12.0 - (longitude / 15.0) - (eot / 60.0)
        val solarNoonLocal = solarNoonUtc + timeZoneOffsetHours

        // Helper for hour angle
        fun getHourAngle(alphaDegrees: Double): Double {
            val alphaRad = Math.toRadians(alphaDegrees)
            val cosHA = (sin(alphaRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
            val clamped = cosHA.coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(clamped)) / 15.0
        }

        // Sunrise/Sunset (Sun center 0.833° below horizon)
        val sunriseSunsetHA = getHourAngle(-0.833)
        val sunriseHours = solarNoonLocal - sunriseSunsetHA
        val sunsetHours = solarNoonLocal + sunriseSunsetHA

        // Fajr (18° below horizon)
        val fajrHA = getHourAngle(-18.0)
        val fajrHours = solarNoonLocal - fajrHA

        // Isha (18° below horizon)
        val ishaHA = getHourAngle(-18.0)
        val ishaHours = solarNoonLocal + ishaHA

        // Asr (Hanafi shadow ratio = 2, Shafi shadow ratio = 1). Using Shafi/Standard 1.0 + tan(|lat - dec|)
        val asrAngleRad = atan(1.0 / (1.0 + tan(abs(latRad - decRad))))
        val asrAngleDeg = Math.toDegrees(asrAngleRad)
        val asrHA = getHourAngle(asrAngleDeg)
        val asrHours = solarNoonLocal + asrHA

        // Dhuhr is slightly after solar noon
        val dhuhrHours = solarNoonLocal + (4.0 / 60.0) // 4 min buffer

        fun formatHours(h: Double): String {
            val totalMins = (h * 60).roundToInt()
            val cleanMins = ((totalMins % 1440) + 1440) % 1440
            val hours = cleanMins / 60
            val mins = cleanMins % 60
            val period = if (hours >= 12) "PM" else "AM"
            val displayHour = when {
                hours == 0 -> 12
                hours > 12 -> hours - 12
                else -> hours
            }
            return String.format(Locale.US, "%02d:%02d %s", displayHour, mins, period)
        }

        val fajrStr = formatHours(fajrHours)
        val sunriseStr = formatHours(sunriseHours)
        val dhuhrStr = formatHours(dhuhrHours)
        val asrStr = formatHours(asrHours)
        val maghribStr = formatHours(sunsetHours)
        val ishaStr = formatHours(ishaHours)

        // Determine next prayer based on current time
        val nowHours = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60.0
        val (nextName, nextTime) = when {
            nowHours < fajrHours -> "Fajr (ফজর)" to fajrStr
            nowHours < sunriseHours -> "Sunrise (সূর্যোদয়)" to sunriseStr
            nowHours < dhuhrHours -> "Dhuhr (যোহর)" to dhuhrStr
            nowHours < asrHours -> "Asr (আসর)" to asrStr
            nowHours < sunsetHours -> "Maghrib (মাগরিব)" to maghribStr
            nowHours < ishaHours -> "Isha (ইশা)" to ishaStr
            else -> "Fajr (আগামীকালের ফজর)" to fajrStr
        }

        return PrayerTimesResult(
            dateStr = dateFormatted,
            locationName = locationName,
            fajr = fajrStr,
            sunrise = sunriseStr,
            dhuhr = dhuhrStr,
            asr = asrStr,
            maghrib = maghribStr,
            isha = ishaStr,
            nextPrayerName = nextName,
            nextPrayerTime = nextTime
        )
    }
}
