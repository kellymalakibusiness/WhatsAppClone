package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.NoProfileImage
import com.malakiapps.whatsappclone.android.presentation.compose.common.base64ToUri
import com.malakiapps.whatsappclone.android.presentation.compose.common.shimmerEffect
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.UserType

@Composable
fun SettingsScreen(
    userDetailsInfo: UserDetailsInfo?,
    userType: UserType?,
    sharedElementModifier: Modifier,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    onAccountSettingsClick: () -> Unit,
    onSignInWithGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopAppBar(
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(state = rememberScrollState())
        ) {
            UserDetailsRow(
                userDetailsInfo = userDetailsInfo,
                sharedElementModifier = sharedElementModifier,
                userType = userType,
                modifier = Modifier
                    .clickable {
                        onProfileClick()
                    }
                    .fillMaxWidth(),
                onSignInWithGoogleClick = onSignInWithGoogleClick
            )

            SettingsRowOption(
                icon = R.drawable.outline_key,
                name = "Account",
                description = "Security notifications, log out",
                onClick = onAccountSettingsClick
            )

            SettingsRowOption(
                icon = R.drawable.outline_lock_24,
                name = "Privacy",
                description = "Block contacts, disappearing messages"
            )

            SettingsRowOption(
                icon = R.drawable.outline_favorite_border_24,
                name = "Favourites",
                description = "Add, reorder, remove"
            )

            SettingsRowOption(
                icon = R.drawable.outline_message_24,
                name = "Chats",
                description = "Theme, wallpapers, chat history"
            )

            SettingsRowOption(
                icon = R.drawable.outline_notifications_24,
                name = "Notifications",
                description = "Message, group & call tones"
            )

            SettingsRowOption(
                icon = R.drawable.baseline_data_saver_off_24,
                name = "Storage and data",
                description = "Network usage, auto-download"
            )

            SettingsRowOption(
                icon = R.drawable.baseline_language_24,
                name = "App language",
                description = "English(device's language)"
            )

            SettingsRowOption(
                icon = R.drawable.outline_help_outline_24,
                name = "Help",
                description = "Help center, contact us, privacy policy"
            )

            SettingsRowOption(
                icon = R.drawable.outline_group_24,
                name = "Invite a friend",
                description = null
            )

            SettingsRowOption(
                icon = R.drawable.outline_security_update_good_24,
                name = "App updates",
                description = null
            )

            Text(
                text = "Also from Meta",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(16.dp)
            )

            SettingsRowOption(
                icon = R.drawable.instagram,
                name = "Open Instagram",
                description = null
            )

            SettingsRowOption(
                icon = R.drawable.facebook,
                name = "Open Facebook",
                description = null
            )

            SettingsRowOption(
                icon = R.drawable.threads,
                name = "Open Threads",
                description = null
            )

        }
    }
}

@Composable
private fun UserDetailsRow(
    userDetailsInfo: UserDetailsInfo?,
    onSignInWithGoogleClick: () -> Unit,
    userType: UserType?,
    modifier: Modifier = Modifier,
    sharedElementModifier: Modifier
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.padding(16.dp)
        ) {
            userDetailsInfo?.let { userDetailsInfo ->
                if (userDetailsInfo.image != null) {
                    AsyncImage(
                        model = userDetailsInfo.image.base64ToUri().value,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = sharedElementModifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                } else {
                    NoProfileImage(
                        modifier = sharedElementModifier
                            .size(60.dp)
                    )
                }
                //Name part
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = userDetailsInfo.name.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = userDetailsInfo.email.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = userDetailsInfo.about.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shimmerEffect(MaterialTheme.shapes.extraSmall)
                )

                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .shimmerEffect(shape = RectangleShape)
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .shimmerEffect(shape = RectangleShape)
                    )
                }
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = DividerDefaults.color.copy(alpha = 0.3f)
        )
        if(userType == UserType.ANONYMOUS){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
                    .clickable{
                        onSignInWithGoogleClick()
                    }
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.google),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(24.dp)
                )

                Text(
                    text = "Sign In with Google"
                )
            }
            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = DividerDefaults.color.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun SettingsRowOption(
    icon: Int,
    name: String,
    description: String?,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Icon for $name",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall
            )

            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(uiMode = 33)
@Composable
private fun SettingsScreenPrev() {
    FakeWhatsAppTheme {
        SharedTransitionScope {
            AnimatedVisibility(true) {
                SettingsScreen(
                    userDetailsInfo = UserDetailsInfo(
                        image = null,
                        name = Name("Malaki"),
                        email = Email("kellymalaki@gmail.com"),
                        about = About("Hey there, blah blah blah")
                    ),
                    userType = UserType.ANONYMOUS,
                    onProfileClick = {},
                    onNavigateBack = {},
                    onAccountSettingsClick = {},
                    sharedElementModifier = Modifier,
                    onSignInWithGoogleClick = {}
                )
            }
        }
    }
}