package pandemic.response.framework.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Verification(
        val verificationToken: String,
        val confirmationToken: String?
)