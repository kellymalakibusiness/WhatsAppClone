package managers

import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.use_cases.GetUserContactUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitializeUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.MigrateToGoogleAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.OnLoginUpdateAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserContactUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserDetailsUseCase
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.getOrNull
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UserManagerTests {
    @Test
    fun `it should update userProfile on authentication for real user`() = runTest {
        //Arrange
        val authenticationContextManager: AuthenticationContextManager = mockk(relaxed = true)
        val getUserContactUseCase: GetUserContactUseCase = mockk(relaxed = true)
        val initializeUserUseCase: InitializeUserUseCase = mockk(relaxed = true)
        val onLoginUpdateAccountUseCase: OnLoginUpdateAccountUseCase = mockk(relaxed = true)
        val updateUserContactUseCase: UpdateUserContactUseCase = mockk(relaxed = true)
        val updateUserDetailsUseCase: UpdateUserDetailsUseCase = mockk(relaxed = true)
        val migrateToGoogleAccountUseCase: MigrateToGoogleAccountUseCase = mockk(relaxed = true)


        val authenticationContext = AuthenticationContext(
            name = Name("user"),
            email = Email("email"),
            type = UserType.REAL
        )
        coEvery { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(authenticationContext)
        )

        val userManager = UserManager(
            authenticationContextManager = authenticationContextManager,
            getUserContactUseCase = getUserContactUseCase,
            initializeUserUseCase = initializeUserUseCase,
            onLoginUpdateAccountUseCase = onLoginUpdateAccountUseCase,
            updateUserContactUseCase = updateUserContactUseCase,
            updateUserDetailsUseCase = updateUserDetailsUseCase,
            migrateToGoogleAccountUseCase = migrateToGoogleAccountUseCase
        )

        backgroundScope.launch {
            userManager.userProfileState.collect {
                val profile = it.getOrNull()
                if(profile != null){
                    assertEquals(profile.name, authenticationContext.name)
                    assertEquals(profile.email, authenticationContext.email)
                    cancel()
                }
            }
        }


    }

    @Test
    fun `it should update userProfile on authentication for anonymous user`() = runTest {
        //Arrange
        val authenticationContextManager: AuthenticationContextManager = mockk(relaxed = true)
        val getUserContactUseCase: GetUserContactUseCase = mockk(relaxed = true)
        val initializeUserUseCase: InitializeUserUseCase = mockk(relaxed = true)
        val onLoginUpdateAccountUseCase: OnLoginUpdateAccountUseCase = mockk(relaxed = true)
        val updateUserContactUseCase: UpdateUserContactUseCase = mockk(relaxed = true)
        val updateUserDetailsUseCase: UpdateUserDetailsUseCase = mockk(relaxed = true)
        val migrateToGoogleAccountUseCase: MigrateToGoogleAccountUseCase = mockk(relaxed = true)


        val authenticationContext = AuthenticationContext(
            name = Name("user21"),
            email = null,
            type = UserType.ANONYMOUS
        )
        coEvery { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(authenticationContext)
        )

        val userManager = UserManager(
            authenticationContextManager = authenticationContextManager,
            getUserContactUseCase = getUserContactUseCase,
            initializeUserUseCase = initializeUserUseCase,
            onLoginUpdateAccountUseCase = onLoginUpdateAccountUseCase,
            updateUserContactUseCase = updateUserContactUseCase,
            updateUserDetailsUseCase = updateUserDetailsUseCase,
            migrateToGoogleAccountUseCase = migrateToGoogleAccountUseCase
        )

        backgroundScope.launch {
            userManager.userProfileState.collect {
                val profile = it.getOrNull()
                if(profile != null){
                    assertEquals(profile.name, authenticationContext.name)
                    assertEquals(profile.email, authenticationContext.email)
                    cancel()
                }
            }
        }


    }
}