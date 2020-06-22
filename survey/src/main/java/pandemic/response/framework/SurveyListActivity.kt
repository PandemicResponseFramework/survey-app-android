package pandemic.response.framework

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.navigation.NavigationView
import pandemic.response.framework.MainActivity.Companion.SURVEY_ID
import pandemic.response.framework.databinding.SurveyListActivityBinding
import pandemic.response.framework.dto.SurveyStatus
import pandemic.response.framework.util.launchWithPostponeLoading
import pandemic.response.framework.workers.showError
import timber.log.Timber
import kotlin.math.abs

class SurveyListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var surveyAdapter: SurveyAdapter
    private val TAG = "SurveyListActivity"

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private val binding by lazy {
        SurveyListActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            setContentView(root)
            surveyAdapter = SurveyAdapter(::onSurveyClicked)
            binding.surveyListView.adapter = surveyAdapter
            refreshLayout.apply {
                setSize(CircularProgressDrawable.DEFAULT)
                setOnRefreshListener {
                    loadSurveys(true)
                }
            }
        }

        setSupportActionBar(binding.toolbarMain)
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
                this,
                drawer,
                binding.toolbarMain,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.navView.setNavigationItemSelectedListener(this)

        binding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        loadSurveys(false)
    }

    private fun loadSurveys(fresh: Boolean) = lifecycleScope.launchWithPostponeLoading(::loading) {
        try {
            val survey = surveyRepo.getSurveyStatusList(fresh)
            success(survey)
        } catch (e: Throwable) {
            error(e)
        } finally {
            loading(false)
        }
    }

    private fun success(surveys: List<SurveyStatus>) {
        surveyAdapter.submitList(surveys)
    }

    private fun error(e: Throwable) {
        Timber.e(e, "Error loading survey status list")
        showError(e, binding.coordinatorLayout, "survey list") {
            loadSurveys(true)
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.refreshLayout.isRefreshing = isLoading
    }

    private fun onSurveyClicked(survey: SurveyStatus) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(SURVEY_ID, survey.nameId)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.nav_term_conditions -> showTermAndConditionsDialog()
        }
        return true
    }

    private fun showTermAndConditionsDialog() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.add(android.R.id.content, TermAndCondDialog())
                .addToBackStack(null)
                .commit()
    }
}