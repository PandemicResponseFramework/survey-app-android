package pandemic.response.framework.repo

import okhttp3.RequestBody
import pandemic.response.framework.dto.TokenResponse
import pandemic.response.framework.dto.Verification
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RegisterApi {

    @POST("auth/verify")
    suspend fun verify(
            @Header("Authorization") token: String,
            @Body verification: Verification
    ): TokenResponse

    @POST("auth/devicetoken")
    suspend fun addDeviceToken(
            @Header("Authorization") token: String,
            @Body body: RequestBody
    )
}