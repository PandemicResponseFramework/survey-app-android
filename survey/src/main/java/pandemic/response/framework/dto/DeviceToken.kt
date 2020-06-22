package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceToken(
        val token: String
)