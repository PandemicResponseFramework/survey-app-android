package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

sealed class Container {
    //  @JsonSubTypes({
    //          @JsonSubTypes.Type(value = BooleanContainerDto.class, name = "BOOL"),
    //          @JsonSubTypes.Type(value = ChoiceContainerDto.class, name = "CHOICE"),
    //          @JsonSubTypes.Type(value = DefaultContainerDto.class, name = "DEFAULT"),
    //  })
    //  @ApiModel(discriminator = "type", subTypes = {
    //          BooleanContainerDto.class,
    //          ChoiceContainerDto.class,
    //          DefaultContainerDto.class})
    //val type: String? = null
    open val subQuestions: List<Question>
        get() = emptyList()

    abstract fun matchAnswer(response: SurveyResponse): Boolean
}

@JsonClass(generateAdapter = true)
data class BooleanContainer(val boolDependsOn: Boolean, override val subQuestions: List<Question>) :
        Container() {
    override fun matchAnswer(response: SurveyResponse) = boolDependsOn == response.boolAnswer
}

@JsonClass(generateAdapter = true)
data class ChoiceContainer(
        val choiceDependsOn: List<Long>,
        override val subQuestions: List<Question>
) : Container() {
    override fun matchAnswer(response: SurveyResponse): Boolean =
            choiceDependsOn.intersect(response.answerIds ?: emptyList()).isNotEmpty()
}