package pandemic.response.framework.common

import android.content.Context
import androidx.work.WorkManager

class WorkManagerProvider(context: Context) {
    val workManager by lazy { WorkManager.getInstance(context) }
}