package pandemic.response.framework

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pandemic.response.framework.databinding.ActionContainerBinding
import pandemic.response.framework.dto.Verification
import timber.log.Timber


class RegisterActivity : BaseActivity() {
    private val binding by lazy {
        ActionContainerBinding.inflate(layoutInflater)
    }

    private val verification by lazy {
        val pathSegments = intent.data?.pathSegments
        if (pathSegments != null && pathSegments.size >= 1) {
            val verificationToken = pathSegments[0]
            val userToken = if (pathSegments.size >= 2) pathSegments[1] else null
            Verification(verificationToken, userToken)
        } else {
            throw IllegalArgumentException("Invalid url missing verification token")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        register()
    }

    private fun register() = lifecycleScope.launch {
        loading()
        try {
            userManager.register(verification)
            success()
        } catch (e: Throwable) {
            error(e)
        }
    }


    private fun loading() = binding.run {
        actionTitle.setText(R.string.register_access)
        actionMessage.text = null
        button.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun success() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }

    private fun error(e: Throwable): Unit = binding.run {
        Timber.e(e, "registration token verification failed:")
        actionTitle.setText(R.string.register_failed)
        actionMessage.setText(R.string.register_failed_description)
        button.visibility = View.VISIBLE
        button.setText(R.string.action_retry)
        button.setOnClickListener { register() }
        progressBar.visibility = View.INVISIBLE
    }
}