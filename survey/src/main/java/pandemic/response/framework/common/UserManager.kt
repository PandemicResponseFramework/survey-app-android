package pandemic.response.framework.common

import android.content.SharedPreferences
import androidx.core.content.edit
import okhttp3.Credentials
import pandemic.response.framework.dto.Verification
import pandemic.response.framework.network.RegisterApi

class UserManager(
        val clientId: String,
        val clientSecret: String,
        val prefs: SharedPreferences,
        val registerApi: RegisterApi) {

    private val KEY_TOKEN = "token"
    private val KEY_TERMS_CONDITIONS = "termsAndConditions"

    private val listeners = mutableListOf<() -> Unit>()

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit {
            putString(KEY_TOKEN, value)
        }

    fun isRegistered(): Boolean = token != null

    var termAndConditionAccepted: Boolean
        get() = prefs.getBoolean(KEY_TERMS_CONDITIONS, false)
        set(value) = prefs.edit().putBoolean(KEY_TERMS_CONDITIONS, value).apply()


    suspend fun register(verification: Verification) {
        val basicAuthToken = Credentials.basic(clientId, clientSecret)
        val authNToken = registerApi.verify(basicAuthToken, verification).token
        unregister()
        token = authNToken
    }

    fun unregister() {
        token = null
        listeners.forEach {
            it()
        }
    }

    fun addUnregisterListener(action: () -> Unit) = listeners.add(action)
}