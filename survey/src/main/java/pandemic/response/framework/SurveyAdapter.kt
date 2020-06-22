package pandemic.response.framework

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pandemic.response.framework.databinding.ElementSurveyBinding
import pandemic.response.framework.dto.SurveyStatus
import java.util.*

class SurveyAdapter(private val surveyClicked: (surveySelected: SurveyStatus) -> Unit) :
        ListAdapter<SurveyStatus, SurveyAdapter.ViewHolder>(SurveyDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, surveyClicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ElementSurveyBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun bind(survey: SurveyStatus, surveyClicked: (surveySelected: SurveyStatus) -> Unit) {
            binding.apply {
                surveyTitle.text = survey.title
                surveyDescription.text = survey.description
                when (survey.uiStatus) {
                    SurveyStatus.Status.NEW -> {
                        progressText.text = root.context.resources.getString(R.string.new_survey)
                        cardSurvey.setOnClickListener { surveyClicked(survey) }
                        cardSurvey.strokeWidth = 4
                        action.text = root.context.getText(R.string.survey_start)
                        survey.endTime?.let {
                            surveyExpiration.text = getHoursRemaining(
                                    root.context,
                                    survey.endTime,
                                    System.currentTimeMillis()
                            )
                        }
                    }
                    SurveyStatus.Status.INCOMPLETE -> {
                        progressText.text =
                                root.context.resources.getString(R.string.progress_survey)
                        cardSurvey.setOnClickListener { surveyClicked(survey) }
                        cardSurvey.strokeWidth = 4
                        action.text = root.context.getText(R.string.continue_survey)
                        survey.endTime?.let {
                            surveyExpiration.text = getHoursRemaining(
                                    root.context,
                                    survey.endTime,
                                    System.currentTimeMillis()
                            )
                        }
                    }
                    SurveyStatus.Status.COMPLETED -> {
                        progressText.text =
                                root.context.resources.getString(R.string.completed_survey)
                        cardSurvey.setOnClickListener { surveyClicked(survey) }
                        action.text = root.context.getText(R.string.retake_survey)
                        cardSurvey.strokeWidth = 4
                        survey.endTime?.let {
                            surveyExpiration.text = getHoursRemaining(
                                    root.context,
                                    survey.endTime,
                                    System.currentTimeMillis()
                            )
                        }
                    }
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ElementSurveyBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        private fun getHoursRemaining(context: Context, endTime: Long, currentTime: Long): String {
            val timeLeft = Date(endTime - currentTime).time / 1000

            val min: Long = timeLeft % 3600 / 60
            val hours: Long = timeLeft % 86400 / 3600
            val days: Long = timeLeft / 86400
            if (days > 0) {
                return String.format(
                        context.resources.getString(R.string.expiration1),
                        days,
                        hours,
                        min
                )
            } else if (days < 0 && hours > 0) {
                return String.format(context.resources.getString(R.string.expiration2), hours, min)
            } else if (days < 0 && hours < 0) {
                return String.format(context.resources.getString(R.string.expiration3), min)

            }
            return ""
        }
    }

    class SurveyDiffCallback : DiffUtil.ItemCallback<SurveyStatus>() {

        override fun areItemsTheSame(oldItem: SurveyStatus, newItem: SurveyStatus): Boolean {
            return oldItem.nameId == newItem.nameId
        }

        override fun areContentsTheSame(oldItem: SurveyStatus, newItem: SurveyStatus): Boolean {
            return oldItem == newItem
        }
    }
}