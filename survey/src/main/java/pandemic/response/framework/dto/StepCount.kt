package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class StepCount(
        val count: Int,
        val startTime: Long,
        val endTime: Long
)