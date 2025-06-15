package com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.TopAppBarButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationTopAppBar(image: Int, label: String, onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    //horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(46.dp)
                            .clip(RoundedCornerShape(30.dp))
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TopAppBarButton(
                        icon = R.drawable.add_call,
                        onClick = { }
                    )
                    TopAppBarButton(
                        icon = R.drawable.topappbar_settings,
                        onClick = { }
                    )
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