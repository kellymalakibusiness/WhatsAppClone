package use_cases

import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.use_cases.MigrateToGoogleAccountUseCase
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.UserContactUpdate
import com.malakiapps.whatsappclone.domain.user.UserType
import common.day1Message1
import common.day1Message2
import common.day2Message3
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MigrateToGoogleAccountUseCaseTests {
    val anonymousUserAccountRepository: AnonymousUserAccountRepository = mockk(relaxed = true)
    val userAccountRepository: AuthenticatedUserAccountRepository = mockk(relaxed = true)
    val anonymousUserMessageRepository: AnonymousUserMessageRepository = mockk(relaxed = true)
    val userMessagesRepository: MessagesRepository = mockk(relaxed = true)

    val anonymousMessages = RawConversation(
        contact2 = ANONYMOUS_EMAIL,
        contact1 = ANONYMOUS_EMAIL,
        messages = listOf(
            day1Message1.copy(sender = ANONYMOUS_EMAIL, receiver = ANONYMOUS_EMAIL),
            day1Message2.copy(sender = ANONYMOUS_EMAIL, receiver = ANONYMOUS_EMAIL),
            day2Message3.copy(sender = ANONYMOUS_EMAIL, receiver = ANONYMOUS_EMAIL)
        )
    )

    private val defaultAuthenticationContext = AuthenticationContext(
        name = Name("authName"),
        email = Email("authEmail"),
        type = UserType.REAL
    )
    @Test
    fun `it should take all personal messages to main repository`() = runTest {
        val migrateToGoogleAccountUseCase = MigrateToGoogleAccountUseCase(
            anonymousUserAccountRepository = anonymousUserAccountRepository,
            userAccountRepository = userAccountRepository,
            anonymousUserMessageRepository = anonymousUserMessageRepository,
            userMessagesRepository = userMessagesRepository
        )
        val importSlot = slot<RawConversation>()

        coEvery { userMessagesRepository.importAllUserMessages(any(), capture(importSlot), any()) } returns Response.Success(
            Unit)
        coEvery { anonymousUserMessageRepository.exportAllUserMessages(any()) } returns Response.Success(anonymousMessages)

        migrateToGoogleAccountUseCase.invoke(signInResponse = SignInResponse(authenticationContext = defaultAuthenticationContext, initialBase64ProfileImage = null))

        assertEquals(anonymousMessages, importSlot.captured)
    }

    @Test
    fun `it should delete all the anonymous user messages`() = runTest {
        val migrateToGoogleAccountUseCase = MigrateToGoogleAccountUseCase(
            anonymousUserAccountRepository = anonymousUserAccountRepository,
            userAccountRepository = userAccountRepository,
            anonymousUserMessageRepository = anonymousUserMessageRepository,
            userMessagesRepository = userMessagesRepository
        )

        coEvery { userMessagesRepository.importAllUserMessages(any(), any(), any()) } returns Response.Success(
            Unit)
        coEvery { anonymousUserMessageRepository.exportAllUserMessages(any()) } returns Response.Success(anonymousMessages)

        migrateToGoogleAccountUseCase.invoke(signInResponse = SignInResponse(authenticationContext = defaultAuthenticationContext, initialBase64ProfileImage = null))

        coVerify(exactly = 1) { anonymousUserMessageRepository.deleteMessages(any(), anonymousMessages.messages.map { it.messageId }) }
    }

    @Test
    fun `it should update user details on the main repository`() = runTest {
        val migrateToGoogleAccountUseCase = MigrateToGoogleAccountUseCase(
            anonymousUserAccountRepository = anonymousUserAccountRepository,
            userAccountRepository = userAccountRepository,
            anonymousUserMessageRepository = anonymousUserMessageRepository,
            userMessagesRepository = userMessagesRepository
        )

        coEvery { userMessagesRepository.importAllUserMessages(any(), any(), any()) } returns Response.Success(
            Unit)
        coEvery { anonymousUserMessageRepository.exportAllUserMessages(any()) } returns Response.Success(anonymousMessages)
        val signInResponse = SignInResponse(authenticationContext = defaultAuthenticationContext, initialBase64ProfileImage = null)
        val oldProfile = Profile(
            name = Name("dkfjx"),
            email = ANONYMOUS_EMAIL,
            about = About("dofhdi"),
            image = Image("dkfjdofhdf")
        )

        coEvery { anonymousUserAccountRepository.getContact(any()) } returns Response.Success(oldProfile)
        migrateToGoogleAccountUseCase.invoke(signInResponse = signInResponse)

        coVerify(exactly = 1) { userAccountRepository.upgradeContactFromAnonymous(
            UserContactUpdate(
                email = signInResponse.authenticationContext.email ?: ANONYMOUS_EMAIL,
                name = Some(oldProfile.name),
                about = Some(oldProfile.about),
                image = Some(oldProfile.image)
            )
        ) }
    }

    @Test
    fun `it should delete user account after migration`() = runTest {
        val migrateToGoogleAccountUseCase = MigrateToGoogleAccountUseCase(
            anonymousUserAccountRepository = anonymousUserAccountRepository,
            userAccountRepository = userAccountRepository,
            anonymousUserMessageRepository = anonymousUserMessageRepository,
            userMessagesRepository = userMessagesRepository
        )

        coEvery { userMessagesRepository.importAllUserMessages(any(), any(), any()) } returns Response.Success(
            Unit)
        coEvery { anonymousUserMessageRepository.exportAllUserMessages(any()) } returns Response.Success(anonymousMessages)

        migrateToGoogleAccountUseCase.invoke(signInResponse = SignInResponse(authenticationContext = defaultAuthenticationContext, initialBase64ProfileImage = null))

        coVerify(exactly = 1) { anonymousUserAccountRepository.deleteAccount(any()) }
    }
}