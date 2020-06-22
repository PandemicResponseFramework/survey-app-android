package pandemic.response.framework.workers

import androidx.work.*
import pandemic.response.framework.SurveyBaseApp
import pandemic.response.framework.dto.DeviceToken
import pandemic.response.framework.dto.SurveyResponse
import pandemic.response.framework.repo.provideMoshi
import java.util.concurrent.TimeUnit

class SurveyManager(val app: SurveyBaseApp) {

    private val TAG_STEP_COUNTER = "stepcounter"
    private val TAG_SURVEY_RESPONSE = "surveyresponse"
    private val TAG_DEVICE_TOKEN = "deviceToken"

    //minimum 15 minutes
    private val STEP_COUNTER_MINUTES_INTERVAL: Long = 6 * 60
    //if (pandemic.response.framework.BuildConfig.DEBUG) 15 else 6 * 60

    val workManager by lazy { WorkManager.getInstance(app) }
    val surveyResponseAdapter by lazy {
        provideMoshi()
                .adapter(SurveyResponse::class.java)
    }
    val tokenAdapter by lazy {
        provideMoshi()
                .adapter(DeviceToken::class.java)
    }

    fun reset() {
        workManager.cancelAllWork()
    }

    fun cancelStepCounter() {
        workManager.cancelAllWorkByTag(TAG_STEP_COUNTER)
    }

    fun postSurveyResponse(surveyNameId: String, surveyResponse: SurveyResponse) {
        val json = surveyResponseAdapter.toJson(surveyResponse)
        val data = createData(surveyNameId, json)
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val workRequest = OneTimeWorkRequestBuilder<SurveyResponseWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .addTag(TAG_SURVEY_RESPONSE)
                .addTag(surveyNameId)
                .addTag(surveyResponse.surveyToken)
                .build()
        workManager.enqueue(workRequest)
    }

    fun startStepCounter() {
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val workRequest = PeriodicWorkRequestBuilder<StepCounterWorker>(
                STEP_COUNTER_MINUTES_INTERVAL,
                TimeUnit.MINUTES
        )
                .setInitialDelay(15, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag(TAG_STEP_COUNTER)
                .build()

        workManager.enqueueUniquePeriodicWork(
                TAG_STEP_COUNTER,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        )
    }

    fun postDeviceToken(deviceToken: String) {
        val json = tokenAdapter.toJson(DeviceToken(deviceToken))
        val data = createData(json)
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val workRequest = OneTimeWorkRequestBuilder<DeviceTokenWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .addTag(TAG_DEVICE_TOKEN)
                .build()
        workManager.enqueue(workRequest)
    }
}