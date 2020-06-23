package pandemic.response.framework

import android.os.Bundle
import androidx.core.text.HtmlCompat
import pandemic.response.framework.databinding.AboutActivityBinding

class AboutActivity : BaseActivity() {

    private val binding by lazy {
        AboutActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = getString(R.string.about_us)
        binding.aboutContent.text =
            HtmlCompat.fromHtml(getAboutContent(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun getAboutContent(): String =
        assets.open("about.html").bufferedReader().use {
            it.readText()
        }
}