package pandemic.response.framework.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import pandemic.response.framework.SurveyBaseApp

const val KEY_DEVICE_TOKEN = "token"

class DeviceTokenWorker(
        context: Context,
        params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val json = inputData.getString(KEY_DEVICE_TOKEN)!!
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val app = (applicationContext as SurveyBaseApp)
            app.authApi.addDeviceToken("Bearer ${app.prefs.token}", requestBody)
            Result.success()
        } catch (e: Throwable) {
            applicationContext.handleWorkerError(e)
        }
    }
}

fun createData(deviceToken: String): Data = Data.Builder()
        .putString(KEY_DEVICE_TOKEN, deviceToken)
        .build()
