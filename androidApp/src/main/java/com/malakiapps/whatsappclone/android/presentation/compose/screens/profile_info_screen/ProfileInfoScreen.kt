package com.malakiapps.whatsappclone.android.presentation.compose.screens.profile_info_screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.NoProfileImage
import com.malakiapps.whatsappclone.android.presentation.compose.common.base64ToUri
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.User
import kotlinx.coroutines.launch

@Composable
fun ProfileInfoScreen(
    userState: User?,
    initialName: Name,
    initialBase64Image: Image?,
    convertUriToBase64Image: suspend (Uri) -> Image?,
    onStartClick: (Name, Image?) -> Unit) {
    val scope = rememberCoroutineScope()
    var nameValue by remember { mutableStateOf(initialName.value) }
    var selectedImage by remember { mutableStateOf(initialBase64Image) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { imageUri ->
                scope.launch {
                    selectedImage = convertUriToBase64Image(imageUri)
                }
            }
        }
    )

    LaunchedEffect(userState) {
        if(userState?.image != null){
            selectedImage = userState.image
        }
        if(userState?.name != null){
            nameValue = userState.name.value
        }
    }

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.topappbar_settings),
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()
        ) {
            Text(
                text = "Profile info",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.padding(top = 32.dp)
            )

            Text(
                text = "Please provide your name and an optional profile photo",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center
            )

            Box {
                if (selectedImage == null){
                    NoProfileImage(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .size(120.dp)
                    )
                } else {
                    AsyncImage(
                        model = selectedImage?.base64ToUri()?.value,
                        contentDescription = "User Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
            }

            TextField(
                value = nameValue,
                onValueChange = {
                    nameValue = it
                },
                suffix = {
                    Box(
                        contentAlignment = Alignment.BottomCenter
                    ){
                        Text(
                            text = nameValue.count().toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp)
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    //Do the check to confirm we're good
                    if(nameValue.isNotBlank()){
                        onStartClick(Name(nameValue), selectedImage)
                    } else {
                        //TODO("Show error dialog with required name")
                    }
                },
                enabled = userState != null,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "Get Started",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

        }
    }
}


@Preview
@Composable
private fun ProfileInfoScreenPrev() {
    FakeWhatsAppTheme {
        ProfileInfoScreen(
            initialName = Name("Kelly"),
            initialBase64Image = null,
            convertUriToBase64Image = { _ -> Image("") },
            onStartClick = {_, _ -> },
            userState = null
        )
    }
}