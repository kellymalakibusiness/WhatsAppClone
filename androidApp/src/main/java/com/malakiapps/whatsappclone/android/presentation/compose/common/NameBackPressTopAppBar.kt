package com.malakiapps.whatsappclone.android.presentation.compose.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameBackPressTopAppBar(name: String, onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = name,
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