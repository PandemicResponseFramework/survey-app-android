package pandemic.response.framework.common

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.work.ListenableWorker.Result
import com.google.android.material.snackbar.Snackbar
import pandemic.response.framework.R
import pandemic.response.framework.SurveyBaseApp
import pandemic.response.framework.common.UIError.*
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException


fun Context.handleWorkerError(e: Throwable): Result {
    Timber.e(e, "worker error")
    return when (e) {
        is IOException -> Result.retry()
        is HttpException -> when (e.code()) {
            408, 429, 503 -> Result.retry()
            401, 403 -> { //token invalid or unregistered
                (applicationContext as SurveyBaseApp).userManager.unregister()
                Result.failure()
            }
            409 -> { //conflict survey token expired
                //survey list as is out of sync
                (applicationContext as SurveyBaseApp).surveyRepo.clearCache()
                Result.failure()
            }
            else -> Result.failure()
        }
        else -> Result.failure()
    }
}


fun Context.handleError(e: Throwable, resource: String): UIError {
    Timber.e(e, "%s  error", resource)
    return when (e) {
        is IOException -> Retry(R.string.error_connection)
        is HttpException -> when (e.code()) {
            401, 403 -> {
                (applicationContext as SurveyBaseApp).userManager.unregister()
                Unregistered(R.string.error_unregister)
            }
            404 -> {
                (applicationContext as SurveyBaseApp).surveyRepo.clearCache()
                NotFound(R.string.error_not_found)
            }
            in 500..599 -> Retry(R.string.error_server_internal)
            else -> Retry(R.string.error_server_other)
        }
        else -> Retry(R.string.error_application)
    }
}

sealed class UIError {
    abstract val messageRes: Int

    data class NotFound(@StringRes override val messageRes: Int) : UIError()
    data class Retry(@StringRes override val messageRes: Int) : UIError()
    data class Unregistered(@StringRes override val messageRes: Int) : UIError()
}

fun Activity.showError(e: Throwable, view: View, resource: String, retry: () -> Unit) {
    val uiError: UIError = this.handleError(e, resource)

    Snackbar.make(view, uiError.messageRes, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .apply {
                when (uiError) {
                    is Retry -> setAction(R.string.action_retry) {
                        retry()
                    }
                    is Unregistered -> setAction(R.string.action_exit_app) {
                        finishAffinity()
                    }
                    is NotFound -> setAction(R.string.action_close) {
                        finish()
                    }
                }
            }.show()

}
