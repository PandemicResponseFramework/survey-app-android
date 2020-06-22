package pandemic.response.framework.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import okhttp3.OkHttpClient
import pandemic.response.framework.common.UserManager
import pandemic.response.framework.dto.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun provideMoshi() = Moshi.Builder().add(
        PolymorphicJsonAdapterFactory.of(Question::class.java, "type")
                .withSubtype(BooleanQuestion::class.java, "BOOL")
                .withSubtype(ChoiceQuestion::class.java, "CHOICE")
                .withSubtype(RangeQuestion::class.java, "RANGE")
                .withSubtype(TextQuestion::class.java, "TEXT")
                .withSubtype(NumberQuestion::class.java, "NUMBER")
                .withSubtype(ChecklistQuestion::class.java, "CHECKLIST")
)
        .build()

fun provideOkHttpClient(userManager: UserManager) = OkHttpClient.Builder()
        .addInterceptor {
            val token = userManager.token
            val request =
                    if (token != null) it.request().newBuilder()
                            .header("Authorization", "Bearer ${userManager.token}")
                            .build()
                    else it.request()
            it.proceed(request)
        }
        .build()

fun provideSurveyApi(host: String, userManager: UserManager) = Retrofit.Builder()
        .client(provideOkHttpClient(userManager))
        .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
        .baseUrl(host)
        .build().create(SurveyApi::class.java)

fun provideAuthApi(host: String) = Retrofit.Builder()
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(host)
        .build().create(RegisterApi::class.java)