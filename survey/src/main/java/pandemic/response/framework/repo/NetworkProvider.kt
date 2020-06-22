package pandemic.response.framework.repo

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import okhttp3.OkHttpClient
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

fun provideOkHttpClient(prefs: Prefs) = OkHttpClient.Builder()
        .addInterceptor {
            val token = prefs.token
            val request =
                    if (token != null) it.request().newBuilder()
                            .header("Authorization", "Bearer ${prefs.token}")
                            .build()
                    else it.request()
            it.proceed(request)
        }
        .build()

fun provideSurveyApi(host: String, prefs: Prefs) = Retrofit.Builder()
        .client(provideOkHttpClient(prefs))
        .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
        .baseUrl(host)
        .build().create(SurveyApi::class.java)

fun provideAuthApi(host: String) = Retrofit.Builder()
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(host)
        .build().create(RegisterApi::class.java)