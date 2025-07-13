package com.rishabh.duressstepss.stepcounter.data.source

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.rishabh.duressstepss.stepcounter.domain.exception.SensorNotAvailableException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val TAG = "StepSensorSource"

class StepSensorSource @Inject constructor(
    private val sensorManager: SensorManager
) {
    val stepCount: Flow<Result<Int>> = callbackFlow {
        val stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            Log.e(TAG, "Step counter sensor not available")
            trySend(Result.failure(SensorNotAvailableException()))
            close()
            return@callbackFlow
        }

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                        Log.d(TAG, "onSensorChanged: New step count received: ${it.values[0]}")
                        trySend(Result.success(it.values[0].toInt()))
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.i(TAG, "onAccuracyChanged: Accuracy changed to $accuracy for sensor $sensor")
                // Not needed for this use case, but good to log.
            }
        }

        Log.d(TAG, "Registering sensor listener")
        sensorManager.registerListener(
            sensorEventListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose {
            Log.d(TAG, "Unregistering sensor listener")
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
}
