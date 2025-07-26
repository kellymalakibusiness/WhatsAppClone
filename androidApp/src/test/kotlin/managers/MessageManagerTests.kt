package managers

import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.messages.ConversationBrief
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.use_cases.GetConversationUseCase
import com.malakiapps.whatsappclone.domain.use_cases.SendMessageUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateMessagesUseCase
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserType
import common.day1Time1
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MessageManagerTests {
    private val authenticatedUserEmail = Email("authUser@gmail.com")
    private val authenticatedUserName = Name("authUser")

    private val defaultAuthenticationContext = AuthenticationContext(
        name = authenticatedUserName,
        email = authenticatedUserEmail,
        type = UserType.REAL
    )


    @Test
    fun `it should listen for changes on conversation briefs`() = runTest {
        //Arrange
        val authenticationContextManager: AuthenticationContextManager = mockk()
        val getConversationUseCase: GetConversationUseCase = mockk()
        val updateMessagesUseCase: UpdateMessagesUseCase = mockk()
        val sendMessageUseCase: SendMessageUseCase = mockk()

        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )

        val currentConversationBriefs = listOf(
            ConversationBrief(
                newMessageCount = 0,
                messageId = MessageId("12"),
                sender = authenticatedUserEmail,
                value = MessageValue("bello"),
                sendStatus = SendStatus.TWO_TICKS,
                time = day1Time1
            )
        )

        getConversationUseCase.defineBriefUpdateStub(
            authenticationContext = defaultAuthenticationContext,
            values = listOf(
                Response.Success(
                    data = currentConversationBriefs
                )
            )
        )

        //Act
        val messagesManager = MessagesManager(
            authenticationContextManager = authenticationContextManager,
            getConversationUseCase = getConversationUseCase,
            updateMessagesUseCase = updateMessagesUseCase,
            sendMessageUseCase = sendMessageUseCase
        )

        //Assert
        backgroundScope.launch {
            messagesManager.conversationBriefs.collect {
                val value = it.getOrNull()
                if(value != null){
                    assertEquals(
                        Response.Success<List<ConversationBrief>?, GetMessagesError>(currentConversationBriefs),
                        messagesManager.conversationBriefs.value)
                    this.cancel()
                } else {
                }
            }
        }
    }

    @Test
    fun `it should listen for notifications from incoming briefs`() = runTest {
        //Arrange
        val authenticationContextManager: AuthenticationContextManager = mockk()
        val getConversationUseCase: GetConversationUseCase = mockk()
        val updateMessagesUseCase: UpdateMessagesUseCase = mockk()
        val sendMessageUseCase: SendMessageUseCase = mockk()

        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )

        val currentConversationBriefs = listOf(
            ConversationBrief(
                newMessageCount = 0,
                messageId = MessageId("12"),
                sender = Email("someOtherUser"),
                value = MessageValue("hi"),
                sendStatus = SendStatus.ONE_TICK,
                time = day1Time1
            )
        )

        every { getConversationUseCase.listenToBriefChanges(authenticationContext = defaultAuthenticationContext) } returns flowOf(*listOf(
            Response.Success<List<ConversationBrief>, GetMessagesError>(
                data = currentConversationBriefs
            )
        ).toTypedArray())
        /*getConversationUseCase.defineBriefUpdateStub(
            authenticationContext = defaultAuthenticationContext,
            values = listOf(
                Response.Success(
                    data = currentConversationBriefs
                )
            )
        )*/

        coEvery { updateMessagesUseCase.updateMessageSendStatus(any(), any(), any()) } returns Response.Success(Unit)

        //Act
        val messagesManager = MessagesManager(
            authenticationContextManager = authenticationContextManager,
            getConversationUseCase = getConversationUseCase,
            updateMessagesUseCase = updateMessagesUseCase,
            sendMessageUseCase = sendMessageUseCase
        )

        //Assert
        val response = messagesManager.listenForNotifications().first()
        assertEquals(currentConversationBriefs.first(), response)
    }

    @Test
    fun `it should listen for tone playing from brief changes`() = runTest {
        //Arrange
        val authenticationContextManager: AuthenticationContextManager = mockk()
        val getConversationUseCase: GetConversationUseCase = mockk()
        val updateMessagesUseCase: UpdateMessagesUseCase = mockk()
        val sendMessageUseCase: SendMessageUseCase = mockk()
        val sender = Email("someOtherUser")

        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )

        val currentConversationBriefs = listOf(
            ConversationBrief(
                newMessageCount = 0,
                messageId = MessageId("12"),
                sender = sender,
                value = MessageValue("hi"),
                sendStatus = SendStatus.ONE_TICK,
                time = day1Time1
            )
        )

        getConversationUseCase.defineBriefUpdateStub(
            authenticationContext = defaultAuthenticationContext,
            values = listOf(
                Response.Success(
                    data = currentConversationBriefs
                )
            )
        )

        coEvery { updateMessagesUseCase.updateMessageSendStatus(any(), any(), any()) } returns Response.Success(Unit)

        //Act
        val messagesManager = MessagesManager(
            authenticationContextManager = authenticationContextManager,
            getConversationUseCase = getConversationUseCase,
            updateMessagesUseCase = updateMessagesUseCase,
            sendMessageUseCase = sendMessageUseCase
        )
        messagesManager.changeCurrentOnConversation(sender)

        //Assert
        val response = messagesManager.listenForTonePlayer().first()
        assertEquals(true, response)
    }

    /*@Test
    fun itShouldSendAMessageAndCaptureTheChangeOnTheBrief() = runTest {
        //Arrange
        defineCommonStubs(authenticationContext = defaultAuthenticationContext)
        val currentConversationBriefs = emptyList<ConversationBrief>()
        val otherContact = Email("otherContact")

        defineBriefUpdateStub(
            authenticationContext = defaultAuthenticationContext,
            values = listOf(
                Response.Success(
                    data = currentConversationBriefs
                )
            )
        )

        mockkStatic(::getTodayLocalDateTime)
        every { getTodayLocalDateTime() } returns day1Time1

        //Act
        val messagesManager = MessagesManager(
            authenticationContextManager = authenticationContextManager,
            getConversationUseCase = getConversationUseCase,
            updateMessagesUseCase = updateMessagesUseCase,
            sendMessageUseCase = sendMessageUseCase
        )

        //Set current on convo
        messagesManager.changeCurrentOnConversation(otherContact)

        //Send a message
        val sentMessage = "Hello there"
        messagesManager.sendMessage(sentMessage)

        //Assert
        val expectedBriefAfterSentMessage = ConversationBrief(
            newMessageCount = 1,
            messageId = TODO(),
            sender = authenticatedUserEmail,
            value = MessageValue(sentMessage),
            sendStatus = SendStatus.ONE_TICK,
            time = day1Time1
        )
        backgroundScope.launch {
            var gotEmptyList = false
            messagesManager.conversationBriefs.collect {
                val value = it.getOrNull()
                if(value != null){
                    if(value.isEmpty()){
                        gotEmptyList = true
                    } else {
                        assertEquals(true, gotEmptyList)
                        assertEquals(
                            Response.Success<List<ConversationBrief>?, GetMessagesError>(currentConversationBriefs),
                            value)
                        this.cancel()
                    }
                } else {

                }
            }
        }
    }*/

    @Test
    fun `it should mark received messages with two ticks after getting it`() = runTest {
        //Arrange
        val authenticationContextManager: AuthenticationContextManager = mockk()
        val getConversationUseCase: GetConversationUseCase = mockk()
        val updateMessagesUseCase: UpdateMessagesUseCase = mockk()
        val sendMessageUseCase: SendMessageUseCase = mockk()
        val sender = Email("someOtherUser")

        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )

        val currentConversationBriefs = listOf(
            ConversationBrief(
                newMessageCount = 0,
                messageId = MessageId("12"),
                sender = sender,
                value = MessageValue("hi"),
                sendStatus = SendStatus.ONE_TICK,
                time = day1Time1
            )
        )

        getConversationUseCase.defineBriefUpdateStub(
            authenticationContext = defaultAuthenticationContext,
            values = listOf(
                Response.Success(
                    data = currentConversationBriefs
                )
            )
        )

        coEvery { updateMessagesUseCase.updateMessageSendStatus(defaultAuthenticationContext, any(), SendStatus.TWO_TICKS) } returns Response.Success(Unit)

        //Act
        val messagesManager = MessagesManager(
            authenticationContextManager = authenticationContextManager,
            getConversationUseCase = getConversationUseCase,
            updateMessagesUseCase = updateMessagesUseCase,
            sendMessageUseCase = sendMessageUseCase
        )

        //Assert
        //Block until we are sure conversation was updated
        backgroundScope.launch {
            var counter = 0
            messagesManager.conversationBriefs.collect {

                if(it.getOrNull() == currentConversationBriefs){
                    coVerify(exactly = 1) { updateMessagesUseCase.updateMessageSendStatus(defaultAuthenticationContext, any(), SendStatus.TWO_TICKS) }
                    cancel()
                } else {
                    if (counter != 0){
                        fail()
                    }
                    counter++
                }
            }
        }

    }

    @Test
    fun `it should mark new messages on current convo with two ticks read`() = runTest {
        //Arrange
        val authenticationContextManager: AuthenticationContextManager = mockk()
        val getConversationUseCase: GetConversationUseCase = mockk()
        val updateMessagesUseCase: UpdateMessagesUseCase = mockk()
        val sendMessageUseCase: SendMessageUseCase = mockk()
        val sender = Email("someOtherUser")
        val currentConversationBriefs = listOf(
            ConversationBrief(
                newMessageCount = 0,
                messageId = MessageId("12"),
                sender = sender,
                value = MessageValue("hi"),
                sendStatus = SendStatus.ONE_TICK,
                time = day1Time1
            )
        )

        every { authenticationContextManager.authenticationContextState } returns MutableStateFlow(
            StateValue(defaultAuthenticationContext)
        )

        getConversationUseCase.defineBriefUpdateStub(
            authenticationContext = defaultAuthenticationContext,
            values = listOf(
                Response.Success(
                    data = currentConversationBriefs
                )
            )
        )

        coEvery { updateMessagesUseCase.updateMessageSendStatus(defaultAuthenticationContext, any(), SendStatus.TWO_TICKS_READ) } returns Response.Success(Unit)

        //Act
        val messagesManager = MessagesManager(
            authenticationContextManager = authenticationContextManager,
            getConversationUseCase = getConversationUseCase,
            updateMessagesUseCase = updateMessagesUseCase,
            sendMessageUseCase = sendMessageUseCase
        )
        messagesManager.changeCurrentOnConversation(sender)

        //Assert
        backgroundScope.launch {
            messagesManager.conversationBriefs.collect {
                if(it.getOrNull() == currentConversationBriefs){
                    coVerify(exactly = 1) { updateMessagesUseCase.updateMessageSendStatus(defaultAuthenticationContext, any(), SendStatus.TWO_TICKS_READ) }
                    cancel()
                }
            }
        }
    }

    private fun GetConversationUseCase.defineBriefUpdateStub(authenticationContext: AuthenticationContext, values: List<Response<List<ConversationBrief>, GetMessagesError>>){
        every { listenToBriefChanges(authenticationContext = authenticationContext) } returns flowOf(*values.toTypedArray())
    }
}