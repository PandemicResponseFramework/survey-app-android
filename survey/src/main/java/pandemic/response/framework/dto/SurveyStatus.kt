package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SurveyStatus(
        val countQuestions: Long,
        val nameId: String,
        val description: String?,
        val title: String,
        var nextQuestionId: Long?,
        val status: Status = Status.NEW,
        val token: String,
        val startTime: Long?,
        val endTime: Long?,
        val dependsOn: String?
) {
    enum class Status {
        INCOMPLETE, COMPLETED, NEW
    }

    val uiStatus: Status
        get() = if (status == Status.INCOMPLETE && nextQuestionId == null) Status.NEW else status

    val isCompleted
        get() = status == Status.COMPLETED
}


