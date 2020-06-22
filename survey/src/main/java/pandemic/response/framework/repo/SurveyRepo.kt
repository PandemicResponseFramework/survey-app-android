package pandemic.response.framework.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pandemic.response.framework.dto.Question
import pandemic.response.framework.dto.Survey
import pandemic.response.framework.dto.SurveyResponse
import pandemic.response.framework.dto.SurveyStatus
import pandemic.response.framework.dto.SurveyStatus.Status
import pandemic.response.framework.workers.SurveyManager

class SurveyRepo(val api: SurveyApi, val surveyManager: SurveyManager) {

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


    fun reset() {
        statusList = null
        statusMap.clear()
    }

    fun answer(surveyStatus: SurveyStatus, answer: SurveyResponse, nextQuestion: Question?) {
        surveyManager.postSurveyResponse(surveyStatus.nameId, answer)

        val newStatus = when {
            nextQuestion == null -> Status.COMPLETED
            surveyStatus.isCompleted && isPrimaryQuestion(surveyStatus.nameId, nextQuestion) ->
                Status.COMPLETED
            else -> Status.INCOMPLETE//primary question
        }
        setStatus(surveyStatus, newStatus, nextQuestion?.id)

    }

    private fun isPrimaryQuestion(surveyNameId: String, question: Question) =
            surveyMap[surveyNameId]?.questions?.contains(question) ?: false

    private fun setStatus(surveyStatus: SurveyStatus, status: Status, nextQuestionId: Long?) {
        if (surveyStatus.status == status && surveyStatus.nextQuestionId == nextQuestionId) return
        val newSurveyStatus = surveyStatus.copy(nextQuestionId = nextQuestionId, status = status)
        statusMap.put(surveyStatus.nameId, newSurveyStatus)
        statusList =
                statusList?.map { if (it.nameId == surveyStatus.nameId) newSurveyStatus else it }
    }
}