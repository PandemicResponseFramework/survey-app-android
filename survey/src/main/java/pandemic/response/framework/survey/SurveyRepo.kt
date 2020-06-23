package pandemic.response.framework.survey

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pandemic.response.framework.dto.Question
import pandemic.response.framework.dto.Survey
import pandemic.response.framework.dto.SurveyResponse
import pandemic.response.framework.dto.SurveyStatus
import pandemic.response.framework.dto.SurveyStatus.Status
import pandemic.response.framework.network.SurveyApi

class SurveyRepo(val api: SurveyApi, val surveyResponseManager: SurveyResponseManager) {

    private var statusList: List<SurveyStatus>? = null

    private val statusMap: MutableMap<String, SurveyStatus> = HashMap()
    private val surveyMap: MutableMap<String, Survey> = HashMap()

    suspend fun getSurvey(id: String): Survey =
            surveyMap[id] ?: withContext(Dispatchers.IO) { api.survey(id) }
                    .also { surveyMap[id] = it }


    suspend fun getSurveyStatus(id: String): SurveyStatus =
            statusMap[id] ?: withContext(Dispatchers.IO) { api.overview(id) }
                    .also { statusMap[id] = it }


    suspend fun getSurveyStatusList(fresh: Boolean): List<SurveyStatus> = statusList.let { list ->
        if (!fresh && list != null) {
            list
        } else {
            withContext(Dispatchers.IO) { api.overviews() }
                    .also { serverList ->
                        statusMap.clear()
                        statusMap.putAll(serverList.associateBy { it.nameId })
                        statusList = serverList
                    }
        }.filter(::isDependentSurveyCompleted)
    }

    private fun isDependentSurveyCompleted(survey: SurveyStatus): Boolean =
            survey.dependsOn?.let {
                statusMap[it]?.isCompleted ?: false
            } ?: true // doesn't depend on other survey


    fun clearCache() {
        statusList = null
        statusMap.clear()
    }

    fun answer(surveyStatus: SurveyStatus, answer: SurveyResponse, nextQuestion: Question?) {
        surveyResponseManager.postSurveyResponse(surveyStatus.nameId, answer)
        val newStatus = if (nextQuestion == null) Status.COMPLETED else Status.INCOMPLETE
        setStatus(surveyStatus, newStatus, nextQuestion?.id)
    }

    private fun isPrimaryQuestion(surveyNameId: String, question: Question) =
            surveyMap[surveyNameId]?.questions?.contains(question) ?: false

    private fun setStatus(surveyStatus: SurveyStatus, newStatus: Status, newNextQuestionId: Long?) =
            statusMap[surveyStatus.nameId]?.run {
                if (surveyStatus.token != token) return@run
                if (status == newStatus && nextQuestionId == newNextQuestionId) return@run
                val newSurveyStatus = copy(status = newStatus, nextQuestionId = newNextQuestionId)
                statusMap.put(nameId, newSurveyStatus)
                statusList =
                        statusList?.map { if (it.nameId == nameId) newSurveyStatus else it }
            }
}