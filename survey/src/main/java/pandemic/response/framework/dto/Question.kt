package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

sealed class Question {
    //          @Type(value = BooleanQuestionDto.class, name = "BOOL"),
    //          @Type(value = ChoiceQuestionDto.class, name = "CHOICE"),
    //          @Type(value = RangeQuestionDto.class, name = "RANGE"),
    //          @Type(value = TextQuestionDto.class, name = "TEXT"),
    //          @Type(value = ChecklistQuestionDto.class, name = "CHECKLIST"),
    //          @Type(value = ChecklistEntryDto.class, name = "CHECKLIST_ENTRY")
    abstract val id: Long
    abstract val question: String
    abstract val optional: Boolean
    open val container: Container?
        get() = null

    fun subquestion(response: SurveyResponse): List<Question>? = container?.run {
        if (matchAnswer(response)) subQuestions else null
    }

    val subquestion
        get() = container?.subQuestions
}

@JsonClass(generateAdapter = true)
data class BooleanQuestion(
        override val id: Long,
        override val question: String,
        override val optional: Boolean,
        override val container: BooleanContainer?,
        val defaultAnswer: Boolean? = null
) : Question()

@JsonClass(generateAdapter = true)
data class ChecklistEntry(
        val id: Long,
        val question: String,
        val order: Int,
        val defaultAnswer: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ChecklistQuestion(
        override val id: Long,
        override val question: String,
        override val optional: Boolean,
        val entries: List<ChecklistEntry>
) : Question()

@JsonClass(generateAdapter = true)
data class ChoiceQuestion(
        override val id: Long,
        override val question: String,
        override val optional: Boolean,
        override val container: ChoiceContainer?,
        val answers: List<Answer>,
        val defaultAnswer: Long? = null,
        val multiple: Boolean = false
) : Question()

@JsonClass(generateAdapter = true)
data class RangeQuestion(
        override val id: Long,
        override val question: String,
        override val optional: Boolean,
        val minValue: Int,
        val maxValue: Int,
        val minText: String?,
        val maxText: String?,
        val defaultValue: Int? = null
) : Question()

@JsonClass(generateAdapter = true)
data class TextQuestion(
        override val id: Long,
        override val question: String,
        override val optional: Boolean,
        val multiline: Boolean = false,
        val length: Int = 0
) : Question()

@JsonClass(generateAdapter = true)
data class NumberQuestion(
        override val id: Long,
        override val question: String,
        override val optional: Boolean,
        val minValue: Int?,
        val maxValue: Int?,
        val defaultValue: Int?
) : Question()