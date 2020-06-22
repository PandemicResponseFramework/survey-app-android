package pandemic.response.framework.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pandemic.response.framework.SurveyBaseApp
import pandemic.response.framework.dto.StepCount
import pandemic.response.framework.steps.StepCounterException
import pandemic.response.framework.steps.StepTotal
import pandemic.response.framework.steps.getStepCount
import timber.log.Timber

class StepCounterWorker(
        context: Context,
        params: WorkerParameters
) : CoroutineWorker(context, params) {

    val prefs by lazy { (applicationContext as SurveyBaseApp).prefs }
    val api by lazy { (applicationContext as SurveyBaseApp).api }

    override suspend fun doWork(): Result {
        try {
            val total = applicationContext.getStepCount()
            val time = System.currentTimeMillis()

            prefs.stepTotal?.let { previous ->
                //the step counter can been rseted by rebooting the device in that case new total step count can be lower
                val count = if (total > previous.total) total - previous.total else total
                api.stepcount(StepCount(count, previous.time, time))
            } // if there is no previous data we only save the current total number of steps

            prefs.stepTotal = StepTotal(total, time)

            return Result.success()
        } catch (e: StepCounterException) {
            Timber.e(e, "StepCounterWorker error")
            return Result.failure()
        } catch (e: Throwable) {
            return applicationContext.handleWorkerError(e)
        }
    }
}


