package com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.NoProfileImage
import com.malakiapps.whatsappclone.android.presentation.compose.common.TopAppBarButton
import com.malakiapps.whatsappclone.android.presentation.compose.common.base64ToUri
import com.malakiapps.whatsappclone.domain.user.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationTopAppBar(profile: Profile?, selectedMessages: Int?, onNavigateBack: () -> Unit, onProfileClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                selectedMessages?.let { onSelectMessagesView ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = onSelectMessagesView.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        TopAppBarButton(
                            icon = R.drawable.outline_delete_24,
                            onClick = { }
                        )
                        TopAppBarButton(
                            icon = R.drawable.topappbar_settings,
                            onClick = { }
                        )
                    }
                } ?: run {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        //horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        profile?.image?.let {
                            AsyncImage(
                                model = it.base64ToUri().value,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(30.dp))
                            )
                        } ?: run {
                            NoProfileImage(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(30.dp))
                            )
                        }

                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .clickable {
                                    onProfileClick()
                                }
                                .weight(1f)
                                .height(46.dp)
                        ){
                            Text(
                                text = profile?.name?.value ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        TopAppBarButton(
                            icon = R.drawable.add_call,
                            onClick = { }
                        )
                        TopAppBarButton(
                            icon = R.drawable.topappbar_settings,
                            onClick = { }
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onNavigateBack()
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