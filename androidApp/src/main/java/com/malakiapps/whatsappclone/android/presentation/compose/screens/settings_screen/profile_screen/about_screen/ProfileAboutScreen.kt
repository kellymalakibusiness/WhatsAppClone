package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.about_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.NameBackPressTopAppBar
import com.malakiapps.whatsappclone.domain.user.About

val aboutList = listOf(
    "Available",
    "Busy",
    "At school",
    "At the movies",
    "At work",
    "Battery about to die",
    "Can't talk, WhatsApp only",
    "In a meeting",
    "At the gym",
    "Sleeping",
    "Urgent calls only"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAboutScreen(
    currentValue: About?,
    onSelect: (About) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAboutEditorSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val focusRequester = remember { FocusRequester() }
    var aboutUpdatedTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentValue?.value ?: "",
                selection = TextRange(0, currentValue?.value?.length ?: 0)
            )
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                NameBackPressTopAppBar(
                    name = "About",
                    onBackPress = onBackPress
                )
            }
        ) { paddingValues ->
            val horizontalPaddingModifier = Modifier.padding(horizontal = 16.dp)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Currently set to",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = horizontalPaddingModifier.padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .clickable {
                            showAboutEditorSheet = true
                        }
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentValue?.value ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            showAboutEditorSheet = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_edit_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = DividerDefaults.color.copy(alpha = 0.3f)
                )
                Text(
                    text = "Select About",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = horizontalPaddingModifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.secondary
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    aboutList.forEach {
                        AboutElement(
                            element = About(it),
                            isSelected = it == currentValue?.value,
                            onSelect = onSelect
                        )
                    }
                }
            }
        }

        if (showAboutEditorSheet) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            ModalBottomSheet(
                sheetState = bottomSheetState,
                onDismissRequest = {
                    showAboutEditorSheet = false
                },
                shape = RectangleShape,
                dragHandle = {}
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    tonalElevation = 4.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "Add About"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = aboutUpdatedTextFieldValue,
                                onValueChange = {
                                    aboutUpdatedTextFieldValue = it
                                },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                                modifier = Modifier.focusRequester(focusRequester)
                            )
                            Spacer(Modifier.width(16.dp))
                            if (aboutUpdatedTextFieldValue.text.isNotBlank()) {
                                Text(
                                    text = (139 - aboutUpdatedTextFieldValue.text.length).toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                            Icon(
                                painter = painterResource(R.drawable.mood_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    showAboutEditorSheet = false
                                }
                            ) {
                                Text("Cancel")
                            }

                            Spacer(Modifier.width(16.dp))
                            TextButton(
                                onClick = {
                                    val about = About(aboutUpdatedTextFieldValue.text)
                                    onSelect(about)
                                    showAboutEditorSheet = false
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutElement(element: About, isSelected: Boolean, onSelect: (About) -> Unit) {
    Row(
        modifier = Modifier
            .clickable {
                onSelect(element)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = element.value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        )
        Spacer(Modifier.weight(1f))
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.outline_check_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun ProfileAboutScreenPrev(modifier: Modifier = Modifier) {
    FakeWhatsAppTheme {
        ProfileAboutScreen(
            currentValue = About("Hey there,"),
            onSelect = {},
            onBackPress = {}
        )
    }
}

