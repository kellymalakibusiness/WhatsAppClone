package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.name_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.NameBackPressTopAppBar
import com.malakiapps.whatsappclone.domain.user.Name

@Composable
fun ProfileNameScreen(name: Name?, onSaveClick: (Name) -> Unit, onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var currentName by remember {
        mutableStateOf(
            TextFieldValue(
                text = name?.value ?: "",
                selection = TextRange(name?.value?.length ?: 0)
            )
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            NameBackPressTopAppBar(
                name = "Name",
                onBackPress = onBackPress
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues).imePadding()
        ) {
            OutlinedTextField(
                value = currentName,
                onValueChange = {
                    if(it.text.length <= 25){
                        currentName = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyMedium,
                label = {
                    Text("Your name")
                },
                suffix = {
                    Icon(
                        painter = painterResource(R.drawable.mood_24px),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
                
            )
            Text(
                text = "${currentName.text.length}/25",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End).padding(bottom = 16.dp, end = 16.dp, top = 2.dp)
            )

            Text(
                text = "People will see this name if you interact with them and they don't have you saved as a contact.",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSaveClick(Name(currentName.text))
                },
                enabled = name != null && (name.value != currentName.text) && currentName.text.isNotBlank(),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 32.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Preview
@Composable
private fun ProfileNameScreenPrev() {
    FakeWhatsAppTheme {
        ProfileNameScreen(
            name = Name("Kelly"),
            onSaveClick = {},
            onBackPress = {},
        )
    }
}