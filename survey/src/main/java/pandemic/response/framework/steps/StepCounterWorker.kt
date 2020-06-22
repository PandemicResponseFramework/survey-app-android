package pandemic.response.framework.steps

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pandemic.response.framework.SurveyBaseApp
import pandemic.response.framework.common.handleWorkerError
import timber.log.Timber

class StepCounterWorker(
        context: Context,
        params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            (applicationContext as SurveyBaseApp).stepsManager.sendSteps()
            return Result.success()
        } catch (e: StepCounterException) {
            Timber.e(e, "StepCounterWorker error")
            return Result.failure()
        } catch (e: Throwable) {
            return applicationContext.handleWorkerError(e)
        }
    }
}


