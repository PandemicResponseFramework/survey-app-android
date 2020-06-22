package pandemic.response.framework

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pandemic.response.framework.common.UserManager
import pandemic.response.framework.common.WorkManagerProvider
import pandemic.response.framework.network.provideAuthApi
import pandemic.response.framework.network.provideSurveyApi
import pandemic.response.framework.notification.PushNotificationManager
import pandemic.response.framework.steps.StepCounter
import pandemic.response.framework.steps.StepsManager
import pandemic.response.framework.survey.SurveyRepo
import pandemic.response.framework.survey.SurveyResponseManager

open class SurveyBaseApp(
        val surveyBaseUrl: String,
        val registrationBaseUrl: String,
        val clientId: String,
        val clientSecret: String
) : Application() {
    val prefs by lazy { getSharedPreferences("preferences", Context.MODE_PRIVATE) }
    val workManagerProvider by lazy { WorkManagerProvider(this) }
    val registerApi by lazy { provideAuthApi(registrationBaseUrl) }
    val userManager by lazy { UserManager(clientId, clientSecret, prefs, registerApi) }
    val surveyApi by lazy { provideSurveyApi(surveyBaseUrl, userManager) }
    val surveyRepo by lazy { SurveyRepo(surveyApi, SurveyResponseManager(workManagerProvider)) }
    val stepsManager by lazy { StepsManager(StepCounter(this), prefs, surveyApi, workManagerProvider) }
    val pushNotificationManager by lazy { PushNotificationManager(userManager, registerApi, workManagerProvider) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        userManager.addUnregisterListener(surveyRepo::clearCache)
        userManager.addUnregisterListener { workManagerProvider.workManager.cancelAllWork() }
        userManager.addUnregisterListener(stepsManager::stop)
        userManager.addUnregisterListener {
            GlobalScope.launch {
                pushNotificationManager::invalidateDeviceToken
            }
        }
    }
}