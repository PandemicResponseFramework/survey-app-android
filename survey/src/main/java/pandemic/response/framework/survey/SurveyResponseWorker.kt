package pandemic.response.framework.survey

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import pandemic.response.framework.SurveyBaseApp
import pandemic.response.framework.common.handleWorkerError

const val KEY_JSON = "json"
const val KEY_SURVEY_ID = "surveyNameId"

class SurveyResponseWorker(
        context: Context,
        params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val json = inputData.getString(KEY_JSON)!!
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val surveyNameId = inputData.getString(KEY_SURVEY_ID)!!
            (applicationContext as SurveyBaseApp).surveyApi.sendQuestionAnswer(surveyNameId, requestBody)
            Result.success()
        } catch (e: Throwable) {
            applicationContext.handleWorkerError(e)
        }
    }
}

fun createData(surveyNameId: String, responeJson: String): Data = Data.Builder()
        .putString(KEY_JSON, responeJson)
        .putString(KEY_SURVEY_ID, surveyNameId)
        .build()


