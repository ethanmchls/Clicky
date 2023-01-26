package com.ethanmichaelis.clicky.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethanmichaelis.clicky.nav.UsernameValidationType.*
import com.ethanmichaelis.clicky.ui.theme.ClickyTheme

@Composable
fun UsernameDialog(
    updateUsername: (String) -> Unit = {},
) {
    var username by rememberSaveable { mutableStateOf("") }
    var validation by rememberSaveable { mutableStateOf(SUCCESS) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Enter a username to proceed",
            fontSize = 18.sp,
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Username") },
            placeholder = { Text(text = "Username") },
            value = username,
            onValueChange = {
                username = it
            },
            isError = validation != SUCCESS,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
        )
        if (validation != SUCCESS) {
            Text(
                text = when (validation) {
                    EMPTY_USERNAME -> "No username was provided"
                    USERNAME_TOO_LONG -> "Username is too long"
                    else -> "Try Again"
                },
                color = MaterialTheme.colors.error,
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                validation = validateUsername(username)
                if (validation == SUCCESS) updateUsername(username.trim())
            },
        ) {
            Text(text = "Submit")
        }
    }
}

private fun validateUsername(username: String): UsernameValidationType {
    if (username.isBlank()) return EMPTY_USERNAME
    if (username.length > 256) return USERNAME_TOO_LONG // This can obviously be changed
    return SUCCESS
}

private enum class UsernameValidationType {
    SUCCESS, USERNAME_TOO_LONG, EMPTY_USERNAME, ;
}

@Preview
@Composable
fun UsernameDialogPreview() {
    ClickyTheme {
        UsernameDialog()
    }
}