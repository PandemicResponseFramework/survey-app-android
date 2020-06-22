package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Survey(
        val questions: List<Question>,
        val nameId: String,
        val title: String? = null,
        val description: String? = null,
        val version: Int
)