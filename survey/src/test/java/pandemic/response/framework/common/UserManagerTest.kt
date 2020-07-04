package pandemic.response.framework.common

import android.content.SharedPreferences
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import pandemic.response.framework.FakeSharedPreferences
import pandemic.response.framework.dto.TokenResponse
import pandemic.response.framework.dto.Verification
import pandemic.response.framework.network.RegisterApi

internal class UserManagerTest {
    private val registerApi: RegisterApi = mockk(relaxed = true)
    private val preferences: SharedPreferences = FakeSharedPreferences()
    private val expectedToken = "token14Token"
    private val verification = Verification("vToken", "cToken")

    private var userManager = UserManager("test", "secret", preferences, registerApi)

    @Test
    fun `Verify token when register is complete`() {
        //given
        coEvery { registerApi.verify(any(), verification) } returns TokenResponse(expectedToken)

        //when
        runBlocking { userManager.register(verification) }

        //then
        assertEquals(expectedToken, userManager.token)
    }

    @Test
    fun `Verify user is registered after register call`() {
        //given
        coEvery { registerApi.verify(any(), verification) } returns TokenResponse(expectedToken)

        //when
        runBlocking { userManager.register(verification) }

        //then
        assertTrue(userManager.isRegistered())
    }

    @Test
    fun `Terms and conditions remains accepted after app restart`() {
        //given
        assertFalse(userManager.termAndConditionAccepted)
        userManager.termAndConditionAccepted = true

        //when
        userManager = UserManager("test", "secret", preferences, registerApi)

        //then
        assertTrue(userManager.termAndConditionAccepted)
    }

    @Test
    fun `Verify user remain register after restart`() {
        //given
        coEvery { registerApi.verify(any(), verification) } returns TokenResponse(expectedToken)
        runBlocking { userManager.register(verification) }

        //when
        userManager = UserManager("test", "secret", preferences, registerApi)

        //then
        assertTrue(userManager.isRegistered())
        assertEquals(expectedToken, userManager.token)
    }

    @Test
    fun `Verify unregister listeners are invoked`() {
        //given
        var invoked = false
        val listener: () -> Unit = { invoked = true }
        val unregisterListener = mockk<() -> Unit>(relaxed = true)

        userManager.addUnregisterListener(listener)
        userManager.addUnregisterListener(unregisterListener)

        //when
        userManager.unregister()

        //then
        assertTrue(invoked)
        verify { unregisterListener() }
        confirmVerified(unregisterListener)
    }
}
