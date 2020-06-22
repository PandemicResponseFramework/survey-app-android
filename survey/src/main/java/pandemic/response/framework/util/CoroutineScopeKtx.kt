package pandemic.response.framework.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


inline fun CoroutineScope.launchWithPostponeLoading(
        crossinline onLoading: (loading: Boolean) -> Unit,
        postponeMillis: Long = 500L,
        crossinline block: suspend CoroutineScope.() -> Unit
) = this.launch {
    val showLoadingWithDelay = launch { delay(postponeMillis); onLoading(true) }
    try {
        block()
    } finally {
        if (showLoadingWithDelay.isActive) {
            showLoadingWithDelay.cancel()
        } else if (!showLoadingWithDelay.isCancelled) {
            onLoading(false)
        }
    }
}

fun CoroutineScope.launchWithPostponeBlock(
        postponeBlock: () -> Unit,
        postponeMillis: Long = 500L,
        block: suspend CoroutineScope.() -> Unit
) = this.launch {
    val showLoadingWithDelay = launch { delay(postponeMillis); postponeBlock() }
    try {
        block()
        //fast enough no need to display the loading if hasn't been display already
        showLoadingWithDelay.cancelAndJoin()
    } catch (e: Throwable) {
        showLoadingWithDelay.cancelAndJoin()
        throw e
    }
}