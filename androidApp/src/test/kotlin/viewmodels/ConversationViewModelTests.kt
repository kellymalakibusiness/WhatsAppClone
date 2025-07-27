package viewmodels

import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.screens.ConversationMessage
import com.malakiapps.whatsappclone.domain.screens.TimeCard
import com.malakiapps.whatsappclone.domain.use_cases.GetConversationUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateMessagesUseCase
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.presentation.view_models.ConversationViewModel
import common.day1Message1
import common.day1Message2
import common.day1Message3
import common.day2Message1
import common.day2Message2
import common.day2Message3
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ConversationViewModelTests {
    val userManager: UserManager = mockk(relaxed = true)
    val messagesManager: MessagesManager = mockk(relaxed = true)
    val contactsManager: ContactsManager = mockk(relaxed = true)
    val getConversationUseCase: GetConversationUseCase = mockk(relaxed = true)
    val authenticationContextManager: AuthenticationContextManager = mockk(relaxed = true)
    val eventsManager: EventsManager = mockk(relaxed = true)
    val updateMessagesUseCase: UpdateMessagesUseCase = mockk(relaxed = true)
    private val defaultAuthenticationContext = AuthenticationContext(
        name = Name("authName"),
        email = Email("authEmail"),
        type = UserType.REAL
    )



    @Test
    fun `it should sort single day messages correctly with date cards`() = runTest {
        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )
        every { userManager.userDetailsState } returns MutableStateFlow(
            StateValue(UserDetails(type = UserType.REAL, contacts = emptyList()))
        )
        val conversationInput: Response<RawConversation, GetMessagesError> = Response.Success(
            RawConversation(
                contact1 = Email(""),
                contact2 = Email(""),
                messages = listOf(
                    day1Message3,
                    day1Message2,
                    day1Message1,
                ),
                hasPendingWrites = false
            )
        )

        val expectedOutput = listOf(
            day1Message3.messageId,
            day1Message2.messageId,
            day1Message1.messageId,
        )

        every { getConversationUseCase.listenForConversationChanges(any(), any())} returns flowOf(
            conversationInput
        )
        val conversationViewModel = ConversationViewModel(
            authenticationContextManager = authenticationContextManager,
            userManager = userManager,
            messagesManager = messagesManager,
            contactsManager = contactsManager,
            getConversationUseCase = getConversationUseCase,
            targetEmail = Email(""),
            eventsManager = eventsManager,
            updateMessagesUseCase = updateMessagesUseCase
        )

        backgroundScope.launch {
            conversationViewModel.conversation.collect {
                if(it != null){
                    assertEquals(it.size, 4)
                    it.forEachIndexed { index, card -> 
                        when(card){
                            is ConversationMessage -> {
                                assertEquals(expectedOutput[index], card.messageId)
                            }
                            is TimeCard -> {
                                assertEquals(card.time.value, "Today")
                                assertEquals(index, 3)
                            }
                        }
                    }
                    cancel()
                }
            }
        }
    }

    @Test
    fun `it should sort multiple day dates correctly with date cards`() = runTest {
        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )
        every { userManager.userDetailsState } returns MutableStateFlow(
            StateValue(UserDetails(type = UserType.REAL, contacts = emptyList()))
        )
        val conversationInput: Response<RawConversation, GetMessagesError> = Response.Success(
            RawConversation(
                contact1 = Email(""),
                contact2 = Email(""),
                messages = listOf(
                    day1Message3,
                    day1Message2,
                    day1Message1,
                    day2Message3,
                    day2Message2,
                    day2Message1,
                ),
                hasPendingWrites = false
            )
        )

        val expectedOutput = listOf(
            day1Message3.messageId,
            day1Message2.messageId,
            day1Message1.messageId,
            day2Message3.messageId,
            day2Message2.messageId,
            day2Message1.messageId,
        )

        every { getConversationUseCase.listenForConversationChanges(any(), any())} returns flowOf(
            conversationInput
        )
        val conversationViewModel = ConversationViewModel(
            authenticationContextManager = authenticationContextManager,
            userManager = userManager,
            messagesManager = messagesManager,
            contactsManager = contactsManager,
            getConversationUseCase = getConversationUseCase,
            targetEmail = Email(""),
            eventsManager = eventsManager,
            updateMessagesUseCase = updateMessagesUseCase
        )

        backgroundScope.launch {
            conversationViewModel.conversation.collect {
                if(it != null){
                    assertEquals(it.size, 8)

                    assertEquals(expectedOutput[0], (it[0] as ConversationMessage).messageId)
                    assertEquals(expectedOutput[1], (it[1] as ConversationMessage).messageId)
                    assertEquals(expectedOutput[2], (it[2] as ConversationMessage).messageId)

                    assertEquals("Today", (it[3] as TimeCard).time.value)

                    assertEquals(expectedOutput[4], (it[4] as ConversationMessage).messageId)
                    assertEquals(expectedOutput[5], (it[5] as ConversationMessage).messageId)
                    assertEquals(expectedOutput[6], (it[6] as ConversationMessage).messageId)

                    assertEquals("Today", (it[7] as TimeCard).time.value)
                    cancel()
                }
            }
        }
    }

    @Test
    fun `it should change current conversation on message manager`() = runTest {
        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )
        every { userManager.userDetailsState } returns MutableStateFlow(
            StateValue(UserDetails(type = UserType.REAL, contacts = emptyList()))
        )
        val conversationViewModel = ConversationViewModel(
            authenticationContextManager = authenticationContextManager,
            userManager = userManager,
            messagesManager = messagesManager,
            contactsManager = contactsManager,
            getConversationUseCase = getConversationUseCase,
            targetEmail = Email(""),
            eventsManager = eventsManager,
            updateMessagesUseCase = updateMessagesUseCase
        )

        backgroundScope.launch {
            var index = 0
            conversationViewModel.conversation.collect {
                if(it != null || index > 0){
                    verify(exactly = 1) { messagesManager.changeCurrentOnConversation(any()) }
                    cancel()
                }
                index++
            }
        }
    }

    @Test
    fun `it should listen for current contact updates`() = runTest {
        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )
        every { userManager.userDetailsState } returns MutableStateFlow(
            StateValue(UserDetails(type = UserType.REAL, contacts = emptyList()))
        )
        val profile1 = Profile(
            name = Name("Kdof"),
            email = Email("kdfjdo"),
            about = About("difnhdf"),
            image = Image("dfihdgdif")
        )
        val profile2 = profile1.copy(about = About("kdfodhggk"), name = Name("kdfh"))
        
        every { contactsManager.listenToContactChanges(any()) } returns flowOf(Response.Success(profile1), Response.Success(profile2))

        val conversationViewModel = ConversationViewModel(
            authenticationContextManager = authenticationContextManager,
            userManager = userManager,
            messagesManager = messagesManager,
            contactsManager = contactsManager,
            getConversationUseCase = getConversationUseCase,
            targetEmail = Email(""),
            eventsManager = eventsManager,
            updateMessagesUseCase = updateMessagesUseCase
        )

        backgroundScope.launch {
            var index = 0
            conversationViewModel.targetContact.collect {
                when(index){
                    0 -> {
                        assertEquals(null, it)
                    }
                    1 -> {
                        assertEquals(profile1, it)
                    }
                    2 -> {
                        assertEquals(profile2, it)
                        cancel()
                    }
                    else -> {
                        cancel()
                    }
                }
                index++
            }
        }
    }

    @Test
    fun `it should add new contact to our contacts list on new conversations`() = runTest {
        val targetEmail = Email("target")
        every { userManager.userDetailsState } returns MutableStateFlow(
            StateValue(UserDetails(type = UserType.REAL, contacts = emptyList()))
        )
        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )


        val conversationViewModel = ConversationViewModel(
            authenticationContextManager = authenticationContextManager,
            userManager = userManager,
            messagesManager = messagesManager,
            contactsManager = contactsManager,
            getConversationUseCase = getConversationUseCase,
            targetEmail = targetEmail,
            eventsManager = eventsManager,
            updateMessagesUseCase = updateMessagesUseCase
        )

        backgroundScope.launch {
            conversationViewModel.conversation.collect {
                if(it != null){
                    coVerify(exactly = 1) { userManager.updateUserDetails(addContactUpdate = Some(targetEmail)) }
                    cancel()
                }
            }
        }
    }
}