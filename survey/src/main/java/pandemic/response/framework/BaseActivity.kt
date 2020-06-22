package pandemic.response.framework

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    val prefs by lazy { (application as SurveyBaseApp).prefs }
    val surveyManager by lazy { (application as SurveyBaseApp).surveyManager }
    val surveyRepo by lazy { (application as SurveyBaseApp).surveyRepo }
    val authApi by lazy { (application as SurveyBaseApp).authApi }
}