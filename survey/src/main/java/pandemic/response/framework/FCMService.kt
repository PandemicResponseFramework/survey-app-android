package pandemic.response.framework

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("Device token %s", token)
        if ((this@FCMService.applicationContext as SurveyBaseApp).prefs.token != null)
            (this@FCMService.applicationContext as SurveyBaseApp).surveyManager.postDeviceToken(token)
    }
}