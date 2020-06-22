package pandemic.response.framework.notification

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pandemic.response.framework.common.UserManager
import pandemic.response.framework.common.WorkManagerProvider
import pandemic.response.framework.dto.DeviceToken
import pandemic.response.framework.network.RegisterApi
import timber.log.Timber
import java.io.IOException

class PushNotificationManager(val userManager: UserManager, val registerApi: RegisterApi, val workManagerProvider: WorkManagerProvider) {
    private val TAG_DEVICE_TOKEN = "deviceToken"

    suspend fun invalidateDeviceToken() = withContext(Dispatchers.IO) {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId()
        } catch (e: IOException) {
            Timber.e(e, "fail to delete instance id")
        }
    }

    fun postDeviceToken(deviceToken: String) {
        if (!userManager.isRegistered()) return
        val data = createData(deviceToken)
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val workRequest = OneTimeWorkRequestBuilder<DeviceTokenWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .addTag(TAG_DEVICE_TOKEN)
                .build()
        workManagerProvider.workManager.enqueueUniqueWork(TAG_DEVICE_TOKEN, ExistingWorkPolicy.REPLACE, workRequest)
    }

    suspend fun sendDeviceToken(deviceToken: String) {
        userManager.token?.let {
            registerApi.addDeviceToken("Bearer $it", DeviceToken(deviceToken))
        }

    }

}