package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StepCount(
        val count: Int,
        val startTime: Long,
        val endTime: Long
)