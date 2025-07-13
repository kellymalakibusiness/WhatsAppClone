package com.malakiapps.whatsappclone.android.presentation.compose.screens.new_chat_screen

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.TopAppBarButton
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.ProfileType
import com.malakiapps.whatsappclone.domain.user.SearchProfileResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SelectContactScreen(selfProfile: Profile?, contacts: List<Profile>?, searchResults: List<SearchProfileResult>?, isLoading: Boolean, helpMessage: String, onSearchBarTextChange: (String) -> Unit, onBackPress: () -> Unit, onSelectContact: (Email) -> Unit, onAddNewContact: (Email) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NewChatTopAppBar(contactSize = contacts?.size ?: 0, isLoading = isLoading, onSearchBarTextChange = onSearchBarTextChange, onBackPress = onBackPress)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if(searchResults == null){
                item {
                    CreateElementRow(
                        icon = R.drawable.baseline_group_add_24,
                        text = "New group",
                    )
                }

                item {
                    CreateElementRow(
                        icon = R.drawable.group_selected,
                        text = "New community",
                    )
                }
            }

            item {
                Text(
                    text = "Contacts on FakeWhatsApp",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            if (searchResults != null){
                items(items = searchResults, key = { it.profile.email.value }){ contact ->
                    ContactRow(
                        image = contact.profile.image,
                        name = contact.profile.name,
                        about = contact.profile.about,
                        onClick = {
                            onSelectContact(contact.profile.email)
                        },
                        showAdd = contact.profileType == ProfileType.NEW,
                        onAddClick = {
                            onAddNewContact(contact.profile.email)
                        }
                    )
                }
            } else {
                if(selfProfile != null){
                    item {
                        ContactRow(
                            image = selfProfile.image,
                            name = selfProfile.name,
                            about = selfProfile.about,
                            onClick = {
                                onSelectContact(selfProfile.email)
                            }
                        )
                    }
                }
                if(contacts != null){
                    items(items = contacts, key = { it.email.value }){ contact ->
                        ContactRow(
                            image = contact.image,
                            name = contact.name,
                            about = contact.about,
                            onClick = {
                                onSelectContact(contact.email)
                            }
                        )
                    }
                } else {
                    items(count = 10){
                        LoadingContactRow()
                    }
                }
            }

            item { 
                HelpRow(message = helpMessage, modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}

@Composable
fun CreateElementRow(@DrawableRes icon: Int, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
                .size(24.dp)
        )
        Text(
            text = text
        )
    }
}

@Composable
fun HelpRow(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(FlowPreview::class)
@Composable
private fun NewChatTopAppBar(contactSize: Int, isLoading: Boolean, onSearchBarTextChange: (String) -> Unit, onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    var showSearchBar by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }

    BackHandler(showSearchBar) {
        showSearchBar = false
        text = ""
    }

    LaunchedEffect(true) {
        snapshotFlow { text }
            .debounce(600)
            .distinctUntilChanged()
            .collect { value ->
                onSearchBarTextChange(value)
            }
    }
    Surface(
        modifier = modifier,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            SelectContactTitle(contactSize = contactSize, onBackPress = onBackPress, isLoading = isLoading, onShowSearchBar = {
                showSearchBar = true
                text = ""
            })
            AnimatedVisibility(
                visible = showSearchBar,
                enter = slideInHorizontally(
                    initialOffsetX = { (it*1.9).toInt() }
                ) + expandHorizontally(expandFrom = Alignment.Start),
                exit = slideOutHorizontally(
                    targetOffsetX = { it }
                ) + shrinkHorizontally(shrinkTowards = Alignment.Start)
                ) {
                SearchContactTextField(
                    isLoading = isLoading,
                    text = text,
                    onTextChange = {
                        text = it
                    },
                    onCancel = {
                        showSearchBar = false
                        text = ""
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContactTitle(contactSize: Int, isLoading: Boolean, onBackPress: () -> Unit, onShowSearchBar: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = modifier
            ) {
                Column {
                    Text(
                        text = "Select contact",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val textValue = if(contactSize != 1){
                        "$contactSize contacts"
                    } else {
                        "$contactSize contact"
                    }
                    Text(
                        text = textValue,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.weight(1f))
                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp)
                    )
                }
                TopAppBarButton(
                    icon = R.drawable.topappbar_search,
                    onClick = onShowSearchBar
                )
                TopAppBarButton(
                    icon = R.drawable.topappbar_settings,
                    onClick = {  }
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back arrow"
                )
            }
        }
    )
}

@Composable
fun SearchContactTextField(isLoading: Boolean, text: String, onTextChange: (String) -> Unit, onCancel: () -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    TextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier.focusRequester(focusRequester).fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.extraLarge,
        placeholder = {
            Text(
                text = "Search name or email...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
                )
        },
        leadingIcon = {
            IconButton(
                onClick = onCancel
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back arrow"
                )
            }
        },
        trailingIcon = {
            if(isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    )
}
@Preview
@Composable
private fun NewChatScreenPrev() {
    FakeWhatsAppTheme {
        SelectContactScreen(
            selfProfile = Profile(
                name = Name("Kelly Malaki (You)"),
                email = Email(""),
                about = About("Message yourself"),
                image = null
            ),
            contacts = listOf(
                Profile(
                    name = Name("Batman"),
                    email = Email("secrectIdentity@gmail.com"),
                    about = About("Hi, I'm batman"),
                    image = null,
                )
            ),
            helpMessage = "To add new contacts, search for their email to find them.",
            searchResults = listOf(
                SearchProfileResult(
                    profile = Profile(
                        name = Name("A new user"),
                        email = Email(""),
                        image = null,
                        about = About("I love apples")
                    ),
                    profileType = ProfileType.NEW

                )
            ),
            isLoading = false,
            onSearchBarTextChange = {},
            onSelectContact = {},
            onBackPress = {},
            onAddNewContact = {}
        )
    }
}