package pandemic.response.framework

import android.os.Bundle
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
    }
}