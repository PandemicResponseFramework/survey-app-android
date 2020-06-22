package pandemic.response.framework.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import pandemic.response.framework.steps.StepTotal

class Prefs(context: Context) {

    companion object {
        private const val FILE = "prefs"
        private const val KEY_TOKEN = "token"

        const val KEY_TOTAL_STEPS_COUNT = "totalStepsCount"
        const val KEY_TOTAL_STEPS_TIME = "totalStepsTime"
        const val KEY_TERMS_CONDITIONS = "termsAndConditions"

    }

    private val prefs: SharedPreferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit {
            clear()
            putString(KEY_TOKEN, value)
        }

    fun reset() = prefs.edit().clear().apply()

    fun isRegistered(): Boolean = token != null

    var stepTotal: StepTotal?
        get() = prefs.run {
            if (contains(KEY_TOTAL_STEPS_TIME)) {
                StepTotal(
                        getInt(KEY_TOTAL_STEPS_COUNT, 0),
                        getLong(KEY_TOTAL_STEPS_TIME, 0)

                )
            } else null
        }
        set(value) = prefs.edit {
            if (value == null) {
                remove(KEY_TOTAL_STEPS_TIME)
                remove(KEY_TOTAL_STEPS_COUNT)
            } else {
                putInt(KEY_TOTAL_STEPS_COUNT, value.total)
                putLong(KEY_TOTAL_STEPS_TIME, value.time)
            }
        }

    var termAndConditionAccepted: Boolean
        get() = prefs.getBoolean(KEY_TERMS_CONDITIONS, false)
        set(value) = prefs.edit().putBoolean(KEY_TERMS_CONDITIONS, value).apply()

}