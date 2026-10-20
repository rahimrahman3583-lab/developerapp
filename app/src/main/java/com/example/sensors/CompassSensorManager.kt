package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val _azimuthDegrees = MutableStateFlow(0f)
    val azimuthDegrees: StateFlow<Float> = _azimuthDegrees.asStateFlow()

    private val _hasSensors = MutableStateFlow(false)
    val hasSensors: StateFlow<Boolean> = _hasSensors.asStateFlow()

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var hasAccelerometer = false
    private var hasMagnetometer = false

    fun start() {
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer != null && magnetometer != null) {
            _hasSensors.value = true
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager?.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            _hasSensors.value = false
            // Fallback azimuth
            _azimuthDegrees.value = 0f
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            hasAccelerometer = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            hasMagnetometer = true
        }

        if (hasAccelerometer && hasMagnetometer) {
            val success = SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuthInRadians = orientationAngles[0]
                var azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                if (azimuthInDegrees < 0) {
                    azimuthInDegrees += 360f
                }
                _azimuthDegrees.value = azimuthInDegrees
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
