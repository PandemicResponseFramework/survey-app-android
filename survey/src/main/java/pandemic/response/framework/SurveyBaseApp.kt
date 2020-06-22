package pandemic.response.framework

import android.app.Application
import com.google.firebase.FirebaseApp
import pandemic.response.framework.repo.Prefs
import pandemic.response.framework.repo.SurveyRepo
import pandemic.response.framework.repo.provideAuthApi
import pandemic.response.framework.repo.provideSurveyApi
import pandemic.response.framework.workers.SurveyManager

open class SurveyBaseApp(
        val surveyBaseUrl: String,
        val registrationBaseUrl: String,
        val clientId: String,
        val clientSecret: String
) : Application() {
    val prefs by lazy { Prefs(this) }
    val api by lazy { provideSurveyApi(surveyBaseUrl, prefs) }
    val authApi by lazy { provideAuthApi(registrationBaseUrl) }
    val surveyManager by lazy { SurveyManager(this) }
    val surveyRepo by lazy { SurveyRepo(api, surveyManager) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    fun unregister() {
        prefs.reset()
        surveyManager.reset()
        surveyRepo.reset()
    }
}