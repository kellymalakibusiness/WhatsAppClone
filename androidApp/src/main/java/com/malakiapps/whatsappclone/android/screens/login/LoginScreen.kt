package com.malakiapps.whatsappclone.android.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.common.view_models.AuthenticationViewModel

const val URL_LINK = "https://youtu.be/SV-eOBr6VC4?si=nD6QGV3_UBRaAw23"

@Composable
fun LoginScreen(
    onContinueWithGoogleClick: () -> Unit,
    onContinueWithoutSigningInClick: () -> Unit
) {
    Scaffold{ paddingValues ->
        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary).fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(
                    if (isSystemInDarkTheme()){
                        R.drawable.login_image_dark
                    } else {
                        R.drawable.login_image_light
                    }
                ),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 32.dp, bottom = 32.dp),

            )

            Text(
                text = "Welcome to Fake WhatsApp",
                style = MaterialTheme.typography.titleLarge,
                //modifier = Modifier.padding(bottom = 16.dp)
            )

            val annotatedText = buildAnnotatedString {
                append("The purpose of this app is mainly to portray the things we can achieve with jetpack compose, material 3 design and firebase. No ")
                withLink(
                    LinkAnnotation.Url(
                        url = URL_LINK,
                        styles = TextLinkStyles(style = SpanStyle(color = Color(0xFF0085f6), textDecoration = TextDecoration.None))
                    )
                ){
                    append("Privacy Policy")
                }
                append(" or ")
                withLink(
                    LinkAnnotation.Url(
                        url = URL_LINK,
                        styles = TextLinkStyles(style = SpanStyle(color = Color(0xFF0085f6), textDecoration = TextDecoration.None))
                    )
                ){
                    append("Terms of Service")
                }
                append(" available, think of it as a concept app.")
            }

            Text(
                text = annotatedText,
                modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 22.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "If you continue with google, you would be able to send and receive messages to other accounts powered by firebase firestore, but you can continue without signing up to just view the structure of the application",
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 22.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )

            Button(
                onClick = onContinueWithGoogleClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icons8_google),
                        contentDescription = "Google icon",
                        modifier = Modifier.size(25.dp)//.padding(end = 8.dp)
                    )
                    Text("Continue with Google")
                }
            }

            OutlinedButton(
                onClick = onContinueWithoutSigningInClick,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Continue without Signing In")
            }
        }
    }
}

@Preview//(uiMode = 33)
@Composable
fun DefaultPreview() {
    FakeWhatsAppTheme {
        LoginScreen(
            onContinueWithoutSigningInClick = {},
            onContinueWithGoogleClick = {}
        )
    }
}