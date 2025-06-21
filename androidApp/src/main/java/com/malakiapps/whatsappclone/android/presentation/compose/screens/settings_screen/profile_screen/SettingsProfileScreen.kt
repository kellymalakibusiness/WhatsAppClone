package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.NoProfileImage
import com.malakiapps.whatsappclone.android.presentation.compose.common.base64ToUri
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.UserDetailsInfo
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SettingsProfileScreen(
    userDetailsInfo: UserDetailsInfo?,
    sharedElementModifier: Modifier,
    onImageUpdate: (Uri) -> Unit,
    onBackPress: () -> Unit,
    onNamePress: () -> Unit,
    onAboutPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                onImageUpdate(it)
            }
        }
    )
    if (userDetailsInfo != null) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    onBackPress = onBackPress
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userDetailsInfo.image != null) {
                    AsyncImage(
                        model = userDetailsInfo.image.base64ToUri().value,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = sharedElementModifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    )
                } else {
                    NoProfileImage(
                        modifier = sharedElementModifier
                            .size(120.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    )
                }

                TextButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                UserElementRow(
                    userElement = UserElement.NAME,
                    value = userDetailsInfo.name.value,
                    onClick = onNamePress
                )
                UserElementRow(
                    userElement = UserElement.ABOUT,
                    value = userDetailsInfo.about,
                    onClick = onAboutPress
                )
                UserElementRow(
                    userElement = UserElement.EMAIL,
                    value = userDetailsInfo.email.value,
                    onClick = {},
                    isClickable = false
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onBackPress()
                    }
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

enum class UserElement {
    NAME,
    ABOUT,
    EMAIL
}

@Composable
fun UserElementRow(
    userElement: UserElement,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isClickable: Boolean = true
) {
    val netModifier = if (isClickable) {
        modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
    } else {
        modifier.fillMaxWidth()
    }
    Row(
        modifier = netModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (userElement) {
            UserElement.NAME -> R.drawable.person
            UserElement.ABOUT -> R.drawable.outline_info_24
            UserElement.EMAIL -> R.drawable.outline_email_24
        }
        val label = when (userElement) {
            UserElement.NAME -> "Name"
            UserElement.ABOUT -> "About"
            UserElement.EMAIL -> "Email"
        }

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun ProfileScreenPrev() {
    FakeWhatsAppTheme {
        SharedTransitionScope {
            AnimatedVisibility(true) {
                SettingsProfileScreen(
                    userDetailsInfo = UserDetailsInfo(
                        image = null,
                        name = Name("Kelly"),
                        email = Email("one@two.com"),
                        about = "Hey there! I'm using WhatsApp"
                    ),
                    sharedElementModifier = Modifier,
                    onImageUpdate = {},
                    onBackPress = {},
                    onNamePress = {},
                    onAboutPress = {}
                )
            }
        }
    }
}