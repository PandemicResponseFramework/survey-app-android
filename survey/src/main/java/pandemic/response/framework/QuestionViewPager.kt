package pandemic.response.framework

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import pandemic.response.framework.databinding.*
import pandemic.response.framework.dto.*

class QuestionViewPager : RecyclerView.Adapter<QuestionViewPager.BaseViewHolder<*>>() {

    var selectedChoice = mutableListOf<Int>()
    var selectedBool: Boolean? = null
    var selectedRange: Int? = null
    var selectedText: String? = null
    var selectedNumber: Int? = null
    var selectedChecklist = mutableMapOf<Long, Boolean>()

    var questions: MutableList<Question> = mutableListOf()
    var currentQuestion: Question? = null

    abstract class BaseViewHolder<T>(itemView: ViewBinding) :
            RecyclerView.ViewHolder(itemView.root) {
        abstract fun bind(question: T)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_BOOLEAN -> {
                BooleanViewHolder(QuestionBooleanBinding.inflate(layoutInflater, parent, false))
            }
            TYPE_CHECKLIST -> {
                ChecklistViewHolder(
                        QuestionChecklistBinding.inflate(layoutInflater, parent, false),
                        layoutInflater
                )
            }
            TYPE_CHOICE_SINGLE -> {
                ChoiceViewSingleHolder(
                        QuestionMultipleBinding.inflate(
                                layoutInflater,
                                parent,
                                false
                        ), layoutInflater
                )
            }
            TYPE_CHOICE_MULTIPLE -> {
                ChoiceViewMultipleHolder(
                        QuestionChecklistBinding.inflate(
                                layoutInflater,
                                parent,
                                false
                        )
                )
            }
            TYPE_RANGE -> {
                RangeViewHolder(QuestionSliderBinding.inflate(layoutInflater, parent, false))
            }
            TYPE_TEXT -> {
                TextViewHolder(QuestionTextBinding.inflate(layoutInflater, parent, false))
            }
            TYPE_NUMBER -> {
                NumberViewHolder(QuestionNumberBinding.inflate(layoutInflater, parent, false))
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = questions[position]
        when (holder) {
            is BooleanViewHolder -> holder.bind(element as BooleanQuestion)
            is ChecklistViewHolder -> holder.bind(element as ChecklistQuestion)
            is ChoiceViewSingleHolder -> holder.bind(element as ChoiceQuestion)
            is ChoiceViewMultipleHolder -> holder.bind(element as ChoiceQuestion)
            is RangeViewHolder -> holder.bind(element as RangeQuestion)
            is TextViewHolder -> holder.bind(element as TextQuestion)
            is NumberViewHolder -> holder.bind(element as NumberQuestion)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (questions[position]) {
            is BooleanQuestion -> TYPE_BOOLEAN
            is ChecklistQuestion -> TYPE_CHECKLIST
            is RangeQuestion -> TYPE_RANGE
            is TextQuestion -> TYPE_TEXT
            is NumberQuestion -> TYPE_NUMBER
            is ChoiceQuestion -> {
                return if ((questions[position] as ChoiceQuestion).multiple)
                    TYPE_CHOICE_MULTIPLE
                else
                    TYPE_CHOICE_SINGLE
            }
        }
    }

    inner class BooleanViewHolder(private val binding: QuestionBooleanBinding) :
            BaseViewHolder<BooleanQuestion>(binding) {
        override fun bind(question: BooleanQuestion) {
            binding.questionTitle.text = question.question
            binding.singleAnswersGroup.clearCheck()
            selectedBool = null
            binding.singleAnswersGroup.setOnCheckedChangeListener { _, checkedId ->
                selectedBool = checkedId == binding.radioYes.id
            }
        }
    }

    inner class ChecklistViewHolder(
            private val binding: QuestionChecklistBinding,
            private val layoutInflater: LayoutInflater
    ) :
            BaseViewHolder<ChecklistQuestion>(binding) {
        override fun bind(question: ChecklistQuestion) {
            binding.questionTitle.text = question.question


            val lister = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedChecklist[question.entries[buttonView.id].id] = true
                } else {
                    selectedChecklist.remove(question.entries[buttonView.id].id)
                }
            }

            for (i in question.entries.indices) {
                val bindingAnswer =
                        AnswerChecklistQuestionBinding.inflate(layoutInflater)
                bindingAnswer.checkBox.text = question.entries[i].question
                bindingAnswer.checkBox.id = i
                binding.multipleAnswersView.addView(bindingAnswer.checkBox)

                bindingAnswer.checkBox.setOnCheckedChangeListener(lister)

            }
        }
    }

    inner class ChoiceViewSingleHolder(
            private val binding: QuestionMultipleBinding,
            private val layoutInflater: LayoutInflater
    ) :
            BaseViewHolder<ChoiceQuestion>(binding) {
        override fun bind(question: ChoiceQuestion) {
            binding.questionTitle.text = question.question
            binding.singleAnswersGroup.removeAllViews()
            selectedChoice.clear()

            for (i in question.answers.indices) {
                val bindingAnswer by lazy {
                    AnswerMultipleChoiceQuestionBinding.inflate(layoutInflater)
                }
                bindingAnswer.root.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                bindingAnswer.radioButton.text = question.answers[i].value
                bindingAnswer.radioButton.id = i
                binding.singleAnswersGroup.addView(bindingAnswer.radioButton)
                binding.singleAnswersGroup.clearCheck()
            }
            binding.singleAnswersGroup.setOnCheckedChangeListener { _, checkedId ->
                selectedChoice.clear()
                selectedChoice.add(checkedId)
            }
        }
    }

    inner class ChoiceViewMultipleHolder(private val binding: QuestionChecklistBinding) :
            BaseViewHolder<ChoiceQuestion>(binding) {
        override fun bind(question: ChoiceQuestion) {
            binding.questionTitle.text = question.question
            selectedChoice.clear()
            val bindingAnswer by lazy {
                AnswerChecklistQuestionBinding.inflate(LayoutInflater.from(itemView.context))
            }
            val lister = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedChoice[buttonView.id]
                } else {
                    selectedChoice.remove(buttonView.id)
                }
            }

            for (i in question.answers.indices) {
                bindingAnswer.checkBox.text = question.answers[i].value
                bindingAnswer.checkBox.id = i
                binding.multipleAnswersView.addView(bindingAnswer.checkBox)
                bindingAnswer.checkBox.setOnCheckedChangeListener(lister)

            }
        }
    }

    inner class RangeViewHolder(private val binding: QuestionSliderBinding) :
            BaseViewHolder<RangeQuestion>(binding) {
        override fun bind(question: RangeQuestion) {
            binding.questionTitle.text = question.question

            val min = question.minValue
            val max = question.maxValue
            val step = 1

            binding.seekBar.progress = 0
            binding.minValueText.text = "$min"
            binding.maxValueText.text = "$max"
            binding.minText.text = question.minText
            binding.maxText.text = question.maxText
            binding.seekBar.max = (max - min) / step
            binding.seekBar.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onProgressChanged(
                                seekBar: SeekBar,
                                progress: Int,
                                fromUser: Boolean
                        ) {
                            selectedRange = min + progress * step
                        }
                    }
            )
        }
    }

    inner class TextViewHolder(private val binding: QuestionTextBinding) :
            BaseViewHolder<TextQuestion>(binding) {
        override fun bind(question: TextQuestion) {
            binding.questionTitle.text = question.question
            binding.textInputLayout.editText?.apply {
                setText(selectedText, TextView.BufferType.EDITABLE)

                addTextChangedListener {
                    selectedText = it?.toString()
                }

            }
            binding.textInputLayout.counterMaxLength = question.length
        }
    }

    inner class NumberViewHolder(private val binding: QuestionNumberBinding) :
            BaseViewHolder<NumberQuestion>(binding) {
        override fun bind(question: NumberQuestion) {
            binding.questionTitle.text = question.question
            val min = question.minValue
            val max = question.maxValue
            val default = question.defaultValue ?: 1
            selectedNumber = selectedNumber ?: default

            binding.inputText.hint = selectedNumber.toString()
            binding.inputText.requestFocus()

            if (selectedNumber != null) {
                binding.inputText.setText(selectedNumber.toString())
                binding.inputText.requestFocus()
            }
            val watcher = object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    if (!s.toString().isBlank()) {
                        val numberInput = s.toString().toInt()
                        if (numberInput >= min ?: 0 && numberInput <= max ?: 100000) {
                            selectedNumber = numberInput
                        }
                    } else {
                        selectedNumber = default
                    }
                }

                override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            }

            binding.inputText.addTextChangedListener(watcher)
        }
    }

    fun addCurrentQuestion(q: Question) {
        this.currentQuestion = q
        this.selectedChoice = mutableListOf<Int>()
        this.selectedBool = null
        this.selectedRange = null
        this.selectedText = null
        this.selectedChecklist = mutableMapOf<Long, Boolean>()
        this.questions.add(q)
        notifyItemInserted(questions.size - 1)
    }

    companion object {
        private const val TYPE_BOOLEAN = 0
        private const val TYPE_CHECKLIST = 1
        private const val TYPE_CHOICE_SINGLE = 2
        private const val TYPE_CHOICE_MULTIPLE = 3
        private const val TYPE_RANGE = 4
        private const val TYPE_TEXT = 5
        private const val TYPE_NUMBER = 6

    }
}