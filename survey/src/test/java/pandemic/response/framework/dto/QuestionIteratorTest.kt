package pandemic.response.framework.dto

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import pandemic.response.framework.network.provideMoshi
import pandemic.response.framework.survey.QuestionIterator

internal class QuestionIteratorTest {

    val surveyAdapter = provideMoshi().adapter<Survey>(Survey::class.java)

    val survey = surveyAdapter.fromJson(
            """
{
    "id": 32,
    "questions": [{
            "type": "BOOL",
            "id": 1,
            "question": "Q1",
            "order": 0,
            "defaultAnswer": null,
            "optional": true,
            "container": null
        }, {
            "type": "BOOL",
            "id": 3,
            "question": "Q2",
            "order": 1,
            "defaultAnswer": null,
            "optional": true,
            "container": {
                "type": "BOOL",
                "subQuestions": [{
                        "type": "BOOL",
                        "id": 2,
                        "question": "Q2C1",
                        "order": 0,
                        "defaultAnswer": null,
                        "optional": true,
                        "container": null
                    }
                ],
                "boolDependsOn": true
            }
        }, {
            "type": "CHOICE",
            "id": 8,
            "question": "Q3",
            "order": 2,
            "answers": [{
                    "id": 5,
                    "value": "Q3A1"
                }, {
                    "id": 6,
                    "value": "Q3A2"
                }, {
                    "id": 7,
                    "value": "Q3A3"
                }
            ],
            "defaultAnswer": null,
            "multiple": false,
            "optional": true,
            "container": null
        }, {
            "type": "CHOICE",
            "id": 12,
            "question": "Q4",
            "order": 3,
            "answers": [{
                    "id": 9,
                    "value": "Q4A1"
                }, {
                    "id": 10,
                    "value": "Q4A2"
                }, {
                    "id": 11,
                    "value": "Q4A3"
                }
            ],
            "defaultAnswer": null,
            "multiple": true,
            "optional": true,
            "container": null
        }, {
            "type": "CHOICE",
            "id": 17,
            "question": "Q5",
            "order": 4,
            "answers": [{
                    "id": 14,
                    "value": "Q5A1"
                }, {
                    "id": 15,
                    "value": "Q5A2"
                }, {
                    "id": 16,
                    "value": "Q5A3"
                }
            ],
            "defaultAnswer": null,
            "multiple": false,
            "optional": true,
            "container": {
                "type": "CHOICE",
                "subQuestions": [{
                        "type": "BOOL",
                        "id": 13,
                        "question": "Q5C1",
                        "order": 0,
                        "defaultAnswer": null,
                        "optional": true,
                        "container": null
                    }
                ],
                "choiceDependsOn": [14, 15]
            }
        }, {
            "type": "CHOICE",
            "id": 23,
            "question": "Q6",
            "order": 5,
            "answers": [{
                    "id": 20,
                    "value": "Q6A1"
                }, {
                    "id": 21,
                    "value": "Q6A2"
                }, {
                    "id": 22,
                    "value": "Q6A3"
                }
            ],
            "defaultAnswer": null,
            "multiple": true,
            "optional": true,
            "container": {
                "type": "CHOICE",
                "subQuestions": [{
                        "type": "BOOL",
                        "id": 19,
                        "question": "Q6C1",
                        "order": 0,
                        "defaultAnswer": null,
                        "optional": true,
                        "container": null
                    }
                ],
                "choiceDependsOn": [20, 21]
            }
        }, {
            "type": "CHECKLIST",
            "id": 28,
            "question": "Q7",
            "order": 6,
            "optional": true,
            "entries": [{
                    "type": "CHECKLIST_ENTRY",
                    "id": 25,
                    "question": "Q7E1",
                    "order": 0,
                    "defaultAnswer": null
                }, {
                    "type": "CHECKLIST_ENTRY",
                    "id": 26,
                    "question": "Q7E2",
                    "order": 1,
                    "defaultAnswer": null
                }, {
                    "type": "CHECKLIST_ENTRY",
                    "id": 27,
                    "question": "Q7E3",
                    "order": 2,
                    "defaultAnswer": null
                }
            ]
        }, {
            "type": "RANGE",
            "id": 29,
            "question": "Q8",
            "order": 7,
            "minValue": 1,
            "maxValue": 10,
            "minText": "Q8MIN",
            "maxText": "Q8MAX",
            "optional": true,
            "defaultValue": 5
        }, {
            "type": "TEXT",
            "id": 30,
            "question": "Q9",
            "order": 8,
            "multiline": false,
            "optional": true,
            "length": 256
        }, {
            "type": "TEXT",
            "id": 31,
            "question": "Q10",
            "order": 9,
            "multiline": true,
            "optional": true,
            "length": 512
        }
    ],
    "nameId": "TEST",
    "title": "TITLE",
    "description": "DESCRIPTION",
    "version": 0
}
"""
    )

    @Test
    fun nextSingle() {
        val survey = surveyAdapter.fromJson(
                """
{
  "description": "string",
  "id": 0,
  "nameId": "string",
  "questions": [
    {
      "id": 0,
      "question": "string",
      "type": "CHOICE",
      "optional": true,
      "answers": []
    }
  ],
  "title": "string",
  "version": 0
}
            """
        )

        val surveyIterator = QuestionIterator(survey!!)

        assertThat(surveyIterator.next(null)?.id).isEqualTo(0)
        assertThat(surveyIterator.next(null)).isNull()
    }


    @Test
    fun nextWithPosition() {
        val survey = surveyAdapter.fromJson(
                """
{
  "description": "string",
  "id": 0,
  "nameId": "string",
  "questions": [
    {
      "id": 0,
      "question": "string",
      "optional": true,
      "type": "BOOL"
    },
    {
      "id": 1,
      "question": "string",
      "optional": true,
      "type": "TEXT"
    }
  ],
  "title": "string",
  "version": 0
}
            """
        )

        val surveyIterator = QuestionIterator(survey!!, 1)

        assertThat(surveyIterator.next(null)?.id).isEqualTo(1)
        assertThat(surveyIterator.next(null)).isNull()
    }


    @Test
    fun positionInSubquestion() {

        val surveyIterator = QuestionIterator(survey!!, 2)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(2)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(8)

        val surveyIterator2 = QuestionIterator(survey, 13)
        assertThat(surveyIterator2.next(null)?.id).isEqualTo(13)
        assertThat(surveyIterator2.next(null)?.id).isEqualTo(23)
    }


    @Test
    fun answerShowSubquestion() {
        val surveyIterator = QuestionIterator(survey!!, 3)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(3)
        assertThat(
                surveyIterator.next(
                        SurveyResponse(questionId = 3, boolAnswer = true, surveyToken = "")
                )?.id
        ).isEqualTo(2)


        val surveyIterator2 = QuestionIterator(survey, 3)
        assertThat(surveyIterator2.next(null)?.id).isEqualTo(3)
        assertThat(
                surveyIterator2.next(
                        SurveyResponse(questionId = 3, boolAnswer = false, surveyToken = "")
                )?.id
        ).isEqualTo(8)

    }

    @Test
    fun fullSurvey() {
        val surveyIterator = QuestionIterator(survey!!, null)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(1)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(3)
        assertThat(
                surveyIterator.next(
                        SurveyResponse(questionId = 3, boolAnswer = true, surveyToken = "")
                )?.id
        ).isEqualTo(2)
        assertThat(
                surveyIterator.next(
                        SurveyResponse(questionId = 2, boolAnswer = false, surveyToken = "")
                )?.id
        ).isEqualTo(8)
        assertThat(
                surveyIterator.next(
                        SurveyResponse(questionId = 8, answerIds = listOf(5, 7), surveyToken = "")
                )?.id
        ).isEqualTo(12)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(17)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(23)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(28)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(29)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(30)
        assertThat(surveyIterator.next(null)?.id).isEqualTo(31)
        assertThat(surveyIterator.next(null)).isNull()
        assertThat(surveyIterator.next(null)).isNull()
    }

}