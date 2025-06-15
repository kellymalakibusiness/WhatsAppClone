package com.malakiapps.whatsappclone.android.presentation.compose.screens.new_chat_screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.TopAppBarButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatScreen(contacts: List<String>, onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NewChatTopAppBar(contacts = contacts, onBackPress = onBackPress)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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

            item {
                Text(
                    text = "Contacts on FakeWhatsApp",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            //Delete with viewmodel code
            item {
                ContactRow(
                    image = null,
                    name = "Kelly Malaki (You)",
                    about = "Message yourself",
                    onClick = {}
                )
            }

            item {
                ContactRow(
                    image = null,
                    name = "Batman",
                    about = "Hi, I'm batman",
                    onClick = {}
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewChatTopAppBar(contacts: List<String>, onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                Row {
                    Column {
                        Text(
                            text = "Select contact",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val textValue = if(contacts.size != 1){
                            "${contacts.size} contacts"
                        } else {
                            "${contacts.size} contact"
                        }
                        Text(
                            text = textValue,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    TopAppBarButton(
                        icon = R.drawable.topappbar_search,
                        onClick = {  }
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
}
@Preview
@Composable
private fun NewChatScreenPrev() {
    FakeWhatsAppTheme {
        NewChatScreen(
            contacts = listOf("One"),
            {}
        )
    }
}