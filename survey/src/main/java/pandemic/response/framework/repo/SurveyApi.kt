package pandemic.response.framework.repo

import okhttp3.RequestBody
import pandemic.response.framework.dto.StepCount
import pandemic.response.framework.dto.Survey
import pandemic.response.framework.dto.SurveyStatus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SurveyApi {
    @GET("survey/{id}")
    suspend fun survey(
            @Path("id") id: String
    ): Survey

    @POST("survey/{nameId}/answer")
    suspend fun sendQuestionAnswer(
            @Path("nameId") nameId: String,
            @Body body: RequestBody
    )

    @GET("overview")
    suspend fun overviews(): List<SurveyStatus>

    @GET("overview/{nameId}")
    suspend fun overview(@Path("nameId") nameId: String): SurveyStatus

    @POST("health/stepcount")
    suspend fun stepcount(@Body stepCount: StepCount)
}