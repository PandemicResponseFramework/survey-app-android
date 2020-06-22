package pandemic.response.framework

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import pandemic.response.framework.databinding.TermsConditionsDialogBinding
import pandemic.response.framework.repo.Prefs

class TermAndCondDialog : DialogFragment() {

    internal lateinit var termsConditionCallBack: TermsConditionCallBack

    private var termAndConditionAccepted = false

    private var _binding: TermsConditionsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        termAndConditionAccepted = Prefs(context).termAndConditionAccepted
        if (!termAndConditionAccepted)
            try {
                termsConditionCallBack = context as TermsConditionCallBack
            } catch (e: ClassCastException) {
                throw ClassCastException(("$context must implement TermsConditionCallBack"))
            }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = TermsConditionsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        binding.termsText.text = HtmlCompat.fromHtml(getTermsAndConditions(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.acceptBtn.setOnClickListener {
            closeDialogWithResponse(true)
        }

        binding.declineBtn.setOnClickListener {
            closeDialogWithResponse(false)
        }

        if (termAndConditionAccepted) {
            val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 0.9f
            binding.acceptBtn.layoutParams = layoutParams
            binding.declineBtn.visibility = View.GONE
            binding.acceptBtn.text = resources.getText(R.string.close_text)
            binding.acceptBtn.isEnabled = true
            binding.checkPrivacy.visibility = View.GONE
            binding.checkTerms.visibility = View.GONE
        } else {
            initCheckBoxes()
        }
    }

    private fun closeDialogWithResponse(areAccepted: Boolean) {
        if (!termAndConditionAccepted && ::termsConditionCallBack.isInitialized)
            termsConditionCallBack.returnTermsConditionAcceptance(areAccepted)
        dismiss()
    }

    private fun initCheckBoxes() {
        var arePrivacyAccepted = false
        var areTermsAccepted = false

        binding.checkPrivacy.setOnCheckedChangeListener { _, isChecked ->
            arePrivacyAccepted = isChecked
            enableAcceptButton(arePrivacyAccepted, areTermsAccepted)
        }
        binding.checkTerms.setOnCheckedChangeListener { _, isChecked ->
            areTermsAccepted = isChecked
            enableAcceptButton(arePrivacyAccepted, areTermsAccepted)
        }
    }

    private fun enableAcceptButton(arePrivacyAccepted: Boolean, areTermsAccepted: Boolean) {
        binding.acceptBtn.isEnabled = arePrivacyAccepted && areTermsAccepted
    }

    interface TermsConditionCallBack {
        fun returnTermsConditionAcceptance(areAccepted: Boolean)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTermsAndConditions(): String =
            requireContext().assets.open("toc.html").bufferedReader().use {
                it.readText()
            }

}