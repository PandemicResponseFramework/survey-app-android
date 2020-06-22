package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenResponse(val token: String)