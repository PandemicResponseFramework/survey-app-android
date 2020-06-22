package pandemic.response.framework.dto

import java.util.*

class QuestionIterator(private var survey: Survey, lastKnownQuestion: Long? = null) {
    private var currentQuestion: Question? = null
    private val questionStack = Stack<Question>()

    init {
        stackQuestions(survey.questions)
        lastKnownQuestion?.let {
            select(it)
        }
    }

    /**
     * @param response - response for current question optional
     */
    fun next(response: SurveyResponse? = null): Question? {
        if (response != null) {
            if (currentQuestion?.id != response.questionId) {
                throw IllegalArgumentException("Expected an answer for current question ${currentQuestion?.id}")
            }
            currentQuestion?.subquestion(response)?.let {
                stackQuestions(it)
            }
        }

        currentQuestion = if (questionStack.isEmpty()) null else questionStack.pop()

        return currentQuestion
    }

    private fun stackQuestions(questions: Iterable<Question>) =
            //reverse order last in first out
            questionStack.addAll(questions.reversed())


    private fun select(questionId: Long): Boolean {
        while (!questionStack.isEmpty()) {
            val question = questionStack.peek()
            if (question.id == questionId) {
                currentQuestion = question
                return true
            }
            questionStack.pop()
            question.subquestion?.let {
                stackQuestions(it)
            }
        }
        //not found reset the stack to initial
        stackQuestions(survey.questions)
        return false
    }
}