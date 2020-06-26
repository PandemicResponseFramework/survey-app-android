package pandemic.response.framework

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pandemic.response.framework.databinding.ActionContainerBinding
import pandemic.response.framework.dto.Verification
import timber.log.Timber


class RegisterActivity : BaseActivity(), TermAndCondDialog.TermsConditionCallBack {

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

        showTermAndConditionsDialog()
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
        button.isVisible = false
        progressBar.isVisible = true
    }

    private fun success() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }

    private fun error(e: Throwable): Unit = binding.run {
        Timber.e(e, "registration token verification failed:")
        actionTitle.setText(R.string.register_failed)
        actionMessage.setText(R.string.register_failed_description)
        button.isVisible = true
        button.setText(R.string.action_retry)
        button.setOnClickListener { register() }
        progressBar.isVisible = false
    }

    private fun showTermAndConditionsDialog() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, TermAndCondDialog::class.java, null, null)
            .addToBackStack(null)
            .commit()
    }

    override fun returnTermsConditionAcceptance(areAccepted: Boolean) {
        userManager.termAndConditionAccepted = areAccepted
        if (areAccepted) {
            register()
        } else {
            finish()
        }
    }
}