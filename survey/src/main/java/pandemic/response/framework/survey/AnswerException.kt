package pandemic.response.framework.survey

import androidx.annotation.StringRes
import pandemic.response.framework.R


open class AnswerException(@StringRes val messageRes: Int) : Exception()

object NoAnswerException : AnswerException(R.string.answer_required)