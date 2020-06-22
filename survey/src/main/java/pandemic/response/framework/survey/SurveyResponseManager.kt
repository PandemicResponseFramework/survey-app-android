package pandemic.response.framework.survey

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import pandemic.response.framework.common.WorkManagerProvider
import pandemic.response.framework.dto.SurveyResponse
import pandemic.response.framework.network.provideMoshi

class SurveyResponseManager(val workManagerProvider: WorkManagerProvider) {
    private val TAG_SURVEY_RESPONSE = "surveyresponse"

    val surveyResponseAdapter by lazy {
        provideMoshi()
                .adapter(SurveyResponse::class.java)
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
        workManagerProvider.workManager.enqueue(workRequest)
    }

    fun cancelResponses() {
        workManagerProvider.workManager.cancelAllWorkByTag(TAG_SURVEY_RESPONSE)
    }
}