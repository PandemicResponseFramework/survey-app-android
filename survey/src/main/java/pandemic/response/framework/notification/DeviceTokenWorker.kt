package pandemic.response.framework.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import pandemic.response.framework.SurveyBaseApp
import pandemic.response.framework.common.handleWorkerError

const val KEY_DEVICE_TOKEN = "token"

class DeviceTokenWorker(
        context: Context,
        params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val token = inputData.getString(KEY_DEVICE_TOKEN)!!
            (applicationContext as SurveyBaseApp).pushNotificationManager.sendDeviceToken(token)
            Result.success()
        } catch (e: Throwable) {
            applicationContext.handleWorkerError(e)
        }
    }
}

fun createData(deviceToken: String): Data = Data.Builder()
        .putString(KEY_DEVICE_TOKEN, deviceToken)
        .build()
