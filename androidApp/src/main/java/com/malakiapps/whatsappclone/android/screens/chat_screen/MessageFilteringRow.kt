package com.malakiapps.whatsappclone.android.screens.chat_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme

@Composable
fun MessageFilteringRow(activeOption: MessageFilteringOption, onFilterSelect: (MessageFilteringOption) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MessageFilteringOption.entries.forEach { entry ->
            FilteringButton(
                filteringOption = entry,
                isActive = entry == activeOption,
                onClick = onFilterSelect,
            )
        }
    }
}

@Composable
fun FilteringButton(filteringOption: MessageFilteringOption, isActive: Boolean, onClick: (MessageFilteringOption) -> Unit, modifier: Modifier = Modifier) {

    val color = if(isActive){
        MaterialTheme.colorScheme.onTertiary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val colorBackground = if (isActive){
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            //.clip(MaterialTheme.shapes.large)
            .background(colorBackground)
            .padding(horizontal = 12.dp, 6.dp)
            .clickable{
                if(!isActive){
                    onClick(filteringOption)
                }
            }
    ){
        Text(
            filteringOption.text,
            color = color,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun FilterButtonPrev() {
    FakeWhatsAppTheme {
        Scaffold { padding ->
            Column(modifier = Modifier.padding(padding)) {
                /*FilteringButton(
                    filteringOption = MessageFilteringOption.ALL,
                    isActive = true,
                    onClick = {}
                )*/

                MessageFilteringRow(
                    activeOption = MessageFilteringOption.ALL,
                    onFilterSelect = {_ -> }
                )
            }
        }
    }
}