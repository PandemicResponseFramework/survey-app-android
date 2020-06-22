package pandemic.response.framework

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    val userManager by lazy { (application as SurveyBaseApp).userManager }
    val surveyRepo by lazy { (application as SurveyBaseApp).surveyRepo }
}