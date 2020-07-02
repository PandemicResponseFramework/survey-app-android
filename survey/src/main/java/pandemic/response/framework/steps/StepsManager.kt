package pandemic.response.framework.steps

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import pandemic.response.framework.common.WorkManagerProvider
import pandemic.response.framework.dto.StepCount
import pandemic.response.framework.network.SurveyApi
import java.util.concurrent.TimeUnit

class StepsManager(val stepCounter: StepCounter,
                   val prefs: SharedPreferences,
                   val surveyApi: SurveyApi,
                   val workManagerProvider: WorkManagerProvider) {

    private val TAG_STEP_COUNTER = "stepcounter"
    private val KEY_TOTAL_STEPS_COUNT = "totalStepsCount"
    private val KEY_TOTAL_STEPS_TIME = "totalStepsTime"

    //minimum 15 minutes
    private val STEP_COUNTER_MINUTES_INTERVAL: Long = 6 * 60
    //if (pandemic.response.framework.BuildConfig.DEBUG) 15 else 6 * 60

    private var storeStepTotal: StepTotal?
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

    fun start() {
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val workRequest = PeriodicWorkRequestBuilder<StepCounterWorker>(
                STEP_COUNTER_MINUTES_INTERVAL,
                TimeUnit.MINUTES
        )
                .setInitialDelay(15, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .addTag(TAG_STEP_COUNTER)
                .build()

        workManagerProvider.workManager.enqueueUniquePeriodicWork(
                TAG_STEP_COUNTER,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        )
    }

    suspend fun sendSteps(time: Long) {
        val total = stepCounter.getStepCount()

        storeStepTotal?.let { previous ->
            //the step counter can been rseted by rebooting the device in that case new total step count can be lower
            val count = if (total > previous.total) total - previous.total else total
            surveyApi.stepcount(StepCount(count, previous.time, time))
        } // if there is no previous data we only save the current total number of steps

        storeStepTotal = StepTotal(total, time)
    }


    fun stop() {
        workManagerProvider.workManager.cancelAllWorkByTag(TAG_STEP_COUNTER)
    }
}