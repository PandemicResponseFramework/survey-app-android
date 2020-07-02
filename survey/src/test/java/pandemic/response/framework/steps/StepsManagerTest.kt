package pandemic.response.framework.steps

import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import pandemic.response.framework.FakeSharedPreferences
import pandemic.response.framework.common.WorkManagerProvider
import pandemic.response.framework.dto.StepCount
import pandemic.response.framework.network.SurveyApi

internal class StepsManagerTest {

    private val stepCounter = mockk<StepCounter>()
    private val prefs = FakeSharedPreferences()
    private val surveyApi = mockk<SurveyApi>(relaxed = true)
    private val workManagerProvider = mockk<WorkManagerProvider>()

    private var tested = StepsManager(stepCounter, prefs, surveyApi, workManagerProvider)

    @Test
    fun `Verify sent step counter`() {
        //given
        val endTime = System.currentTimeMillis()
        val startTime = System.currentTimeMillis() - 3600
        val storedSteps = 2999
        val counterSteps = 5550

        coEvery { stepCounter.getStepCount() } returns storedSteps
        runBlocking {  tested.sendSteps(startTime) }

        //application restarted
        coEvery { stepCounter.getStepCount() } returns counterSteps

        tested = StepsManager(stepCounter, prefs, surveyApi, workManagerProvider)

        //when
        runBlocking { tested.sendSteps(endTime) }

        //then
        coVerify { surveyApi.stepcount(StepCount(counterSteps - storedSteps, startTime, endTime)) }
    }

    @Test
    fun `Send lower step counter value when the device was restarted`() {
        //given
        val endTime = System.currentTimeMillis()
        val startTime = System.currentTimeMillis() - 3600
        val storedSteps = 1000
        val counterSteps = 150
        coEvery { stepCounter.getStepCount() } returns storedSteps

        runBlocking { tested.sendSteps(startTime) }
        coEvery { stepCounter.getStepCount() } returns counterSteps

        //when
        runBlocking { tested.sendSteps(endTime) }

        //then
        coVerify { surveyApi.stepcount(StepCount(counterSteps, startTime, endTime)) }
    }

    @Test
    fun `When no previous data recorded do not send steps to api`() {
        //given
        val endTime = System.currentTimeMillis()
        coEvery { stepCounter.getStepCount() } returns 10

        //when
        runBlocking { tested.sendSteps(endTime) }

        //then
        coVerify(inverse = true) { surveyApi.stepcount(any()) }
    }

}
