package viewmodels

import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.messages.ConversationBrief
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.presentation.view_models.DashboardViewModel
import common.day1Time1
import common.day2Time1
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class DashboardViewModelTests {
    @Test
    fun `it should order the conversations by time`() = runTest {
        //Arrange
        //Create dependencies
        val authenticationContextManager: AuthenticationContextManager = mockk(relaxed = true)
        val userManager: UserManager = mockk()
        val contactsManager: ContactsManager = mockk(relaxed = true)
        val messagesManager: MessagesManager = mockk()
        val eventManager: EventsManager = mockk(relaxed = true)
        val user1 = Email("de")
        val user2 = Email("de22")

        //Test data
        val testBrief1 = ConversationBrief(
            newMessageCount = 10,
            messageId = MessageId("12"),
            sender = user1,
            value = MessageValue("bello"),
            sendStatus = SendStatus.TWO_TICKS,
            time = day1Time1,
            target = user2,
            isSelfMessage = false,
        )
        val testBrief2 = ConversationBrief(
            newMessageCount = 0,
            messageId = MessageId("13"),
            sender = user2,
            value = MessageValue("thres"),
            sendStatus = SendStatus.TWO_TICKS,
            time = day2Time1,
            target = user2,
            isSelfMessage = false,
        )
        val currentConversationBriefs = listOf(
            testBrief1,
            testBrief2
        )

        //ExpectedResponse
        val expectedResult = listOf(
            testBrief1.sender,
            testBrief2.sender
        )

        //Set stubs
        every { messagesManager.conversationBriefs } returns MutableStateFlow(
            Response.Success(
                data = currentConversationBriefs
            )
        )

        //Initiate viewModel
        val dashboardViewModel = DashboardViewModel(
            authenticationContextManager = authenticationContextManager,
            userManager = userManager,
            contactsManager = contactsManager,
            messagesManager = messagesManager,
            eventsManager = eventManager
        )

        //Assert
        backgroundScope.launch {
            var i = 0
            dashboardViewModel.chatsScreenConversationRow.collect {
                if (it != null){
                    assertEquals(expectedResult.first(), it.first()?.email)
                    assertEquals(expectedResult.last(), it.last()?.email)
                    cancel()
                } else {
                    i++
                    if(i > 1){
                        fail()
                    }
                }

            }
        }
    }
}