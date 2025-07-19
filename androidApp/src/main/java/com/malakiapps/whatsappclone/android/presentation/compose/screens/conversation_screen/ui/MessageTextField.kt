package com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTextField(messageValue: String, onMessageChange: (String)-> Unit, onRecordMessagePress: () -> Unit, onSendMessage: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shadowElevation = 2.dp,
            tonalElevation = 16.dp,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.weight(1f),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.Bottom)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sticker),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if(messageValue.isEmpty()){
                        Text(
                            "Message",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    BasicTextField(
                        value = messageValue,
                        modifier = Modifier
                            .padding(vertical = 4.dp),
                        onValueChange = {
                            onMessageChange(it)
                        },
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.Bottom)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_attach_file_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                AnimatedVisibility(
                    visible = messageValue.isBlank(),
                    enter = slideInHorizontally(
                        initialOffsetX = { it }
                    ) + expandHorizontally(),
                    exit = slideOutHorizontally(
                       targetOffsetX = { it }
                    ) + shrinkHorizontally()
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_photo_camera_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = {
                if(messageValue.isBlank()){
                    onRecordMessagePress()
                } else {
                    onSendMessage(messageValue)
                }
            },
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.extraLarge)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.primary)

        ) {
            AnimatedContent(
                targetState = messageValue.isBlank(),
                transitionSpec = {
                    scaleIn().togetherWith(scaleOut())
                }
            ) { isMessageInputEmpty ->
                Icon(
                    painter = painterResource(
                        if (isMessageInputEmpty){
                            R.drawable.baseline_mic_24
                        } else {
                            R.drawable.baseline_send_24
                        }
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


@Preview(uiMode = 33)
@Composable
private fun MessageTextFieldPrev() {
    FakeWhatsAppTheme {
        Surface(
        ) {
            MessageTextField(
                modifier = Modifier.padding(8.dp),
                messageValue = "",
                onMessageChange = {},
                onSendMessage = {},
                onRecordMessagePress = {}
            )
        }
    }
}