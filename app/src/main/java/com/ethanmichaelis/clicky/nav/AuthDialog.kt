package com.ethanmichaelis.clicky.nav

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethanmichaelis.clicky.nav.AuthType.LOGIN
import com.ethanmichaelis.clicky.nav.AuthType.REGISTER
import com.ethanmichaelis.clicky.nav.AuthValidationType.*
import com.ethanmichaelis.clicky.ui.theme.ClickyTheme

enum class AuthType { LOGIN, REGISTER, ; }

@Composable
fun AuthDialog(
    type: AuthType = LOGIN,
    loading: Boolean = false,
    submit: (String, String) -> Unit = { _, _ -> },
    onBackPressed: () -> Unit = {},
) {
    var validation by rememberSaveable { mutableStateOf(SUCCESS) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(4.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordConfirmation by rememberSaveable { mutableStateOf("") }
        var showPassword by rememberSaveable { mutableStateOf(false) }
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { if (!loading) onBackPressed() }
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Exit X")
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Email") },
                placeholder = { Text(text = "Email") },
                value = email,
                onValueChange = {
                    email = it
                },
                isError = validation == INVALID_EMAIL,
                enabled = !loading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )
            PasswordField(
                text = password,
                showPassword = showPassword,
                onTextChanged = { password = it },
                onShowPassword = { showPassword = it },
                isError = validation == INVALID_PASSWORD || validation == MISMATCHED_PASSWORDS,
                enabled = !loading,
            )
            if (type == REGISTER) {
                PasswordField(
                    text = passwordConfirmation,
                    isConfirmation = true,
                    showPassword = showPassword,
                    onTextChanged = { passwordConfirmation = it },
                    onShowPassword = { showPassword = it },
                    isError = validation == INVALID_PASSWORD || validation == MISMATCHED_PASSWORDS,
                    enabled = !loading,
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    validation = validateEmailAndPassword(
                        type = type,
                        email = email,
                        password = password,
                        passwordConfirmation = passwordConfirmation,
                    )
                    if (validation == SUCCESS) submit(email, password)
                },
                enabled = !loading,
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.Gray)
                } else {
                    Text(text = "Submit")
                }
            }
        }
    }
}

private enum class AuthValidationType {
    SUCCESS, INVALID_EMAIL, INVALID_PASSWORD, MISMATCHED_PASSWORDS, ;
}
private fun validateEmailAndPassword(
    type: AuthType,
    email: String,
    password: String,
    passwordConfirmation: String,
): AuthValidationType {
    if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) return INVALID_EMAIL
    if (password.length < 8) return INVALID_PASSWORD
    if (type == REGISTER && passwordConfirmation != password) return MISMATCHED_PASSWORDS
    return SUCCESS
}

@Composable
private fun PasswordField(
    text: String,
    isError: Boolean,
    showPassword: Boolean,
    enabled: Boolean,
    isConfirmation: Boolean = false,
    onTextChanged: (String) -> Unit,
    onShowPassword: (Boolean) -> Unit,
) {
    val label = if (isConfirmation) "Confirm Password" else "Password"
    TextField(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = label) },
        value = text,
        onValueChange = onTextChanged,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = {
                    onShowPassword(!showPassword)
                }
            ) {
                Icon(
                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (showPassword) "Hide password" else "Show password",
                )
            }
        },
        isError = isError,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}

@Preview
@Composable
private fun AuthDialogPreview() {
    ClickyTheme {
        AuthDialog()
    }
}

@Preview
@Composable
private fun AuthDialogLoadingPreview() {
    ClickyTheme {
        AuthDialog(loading = true)
    }
}