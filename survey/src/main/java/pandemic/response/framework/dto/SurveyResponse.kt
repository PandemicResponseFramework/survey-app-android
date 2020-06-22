package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SurveyResponse(
        val questionId: Long,
        val answerIds: List<Long>? = null,
        val boolAnswer: Boolean? = null,
        val textAnswer: String? = null,
        val checklistAnswer: Map<Long, Boolean>? = null,
        val numberAnswer: Int? = null,
        val surveyToken: String,
        val skipped: Boolean? = null
)