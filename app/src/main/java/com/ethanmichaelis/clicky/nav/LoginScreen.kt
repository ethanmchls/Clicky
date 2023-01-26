package com.ethanmichaelis.clicky.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethanmichaelis.clicky.ui.theme.ClickyTheme

@Composable
fun LoginScreen(
    loginClick: () -> Unit = {},
    signUpClick: () -> Unit = {},
    signupRequested: Boolean = false,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 100.dp)
                    .weight(1f),
                text = "CLICKY",
                style = TextStyle(
                    fontSize = 64.sp,
                    fontFamily = FontFamily.SansSerif,
                ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            if (signupRequested) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Please check your email to confirm registration. After confirming your email, login with your email and password.",
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = loginClick,
            ) {
                Text(text = "Login")
            }
            TextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = signUpClick
            ) {
                Text(text = "Sign Up")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    ClickyTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenRegistrationTriggeredPreview() {
    ClickyTheme {
        LoginScreen(signupRequested = true)
    }
}