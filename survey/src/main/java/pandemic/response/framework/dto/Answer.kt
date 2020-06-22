package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Answer(
        val id: Long,
        val value: String
)