package pandemic.response.framework.steps

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

val PERMISSION_ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION"

class StepCounterException(message: String) : RuntimeException(message)

class StepCounter(val context: Context) {
    suspend fun getStepCount() = suspendCancellableCoroutine<Int> { cont ->
        val sensorManager = ContextCompat.getSystemService(context, SensorManager::class.java)
        val sensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (sensor == null) {
            cont.resumeWithException(
                    StepCounterException(
                            "Step counter sensor missing"
                    )
            )
        } else {
            val listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.values.isNotEmpty()) {
                        cont.resume(event.values[0].toInt())
                        sensorManager.unregisterListener(this)
                    }
                }
            }
            if (!sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)) {
                sensorManager.unregisterListener(listener)
                cont.resumeWithException(
                        StepCounterException(
                                "Cannot register sensor check if access has be granted"
                        )
                )
            }
            cont.invokeOnCancellation {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}