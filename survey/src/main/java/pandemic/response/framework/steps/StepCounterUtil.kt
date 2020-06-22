package pandemic.response.framework.steps

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

val PERMISSION_ACTIVITY_RECOGNITION = "android.permission.ACTIVITY_RECOGNITION"

class StepCounterException(message: String) : RuntimeException(message)

suspend fun Context.getStepCount() = suspendCancellableCoroutine<Int> { cont ->
    val sensorManager = ContextCompat.getSystemService(this, SensorManager::class.java)
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


fun testStepCounterSensor(applicationContext: Context) {
    val sensorManager =
            ContextCompat.getSystemService(applicationContext, SensorManager::class.java)
    val sensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    if (sensor == null) {
        Timber.w("testStepCounterSensor no step counter")
        return
    }
    Timber.i("testStepCounterSensor started %s \nrequired permission ", sensor)


    val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            Timber.i("testStepCounterSensor onAccuracyChanged %d", accuracy)
        }

        override fun onSensorChanged(event: SensorEvent) {
            Timber.i("testStepCounterSensor onSensorChanged $event")
            if (event.values.isNotEmpty()) {
                Timber.i("testStepCounterSensor number of steps %d", event.values[0])
                sensorManager.unregisterListener(this)
            }
        }
    }

    val registered =
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    Timber.i("testStepCounterSensor registered %b", registered)
}