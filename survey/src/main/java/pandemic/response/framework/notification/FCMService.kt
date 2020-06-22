package pandemic.response.framework.notification

import com.google.firebase.messaging.FirebaseMessagingService
import pandemic.response.framework.SurveyBaseApp
import timber.log.Timber

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("Device token %s", token)
        (this@FCMService.applicationContext as SurveyBaseApp).pushNotificationManager.postDeviceToken(token)
    }
}