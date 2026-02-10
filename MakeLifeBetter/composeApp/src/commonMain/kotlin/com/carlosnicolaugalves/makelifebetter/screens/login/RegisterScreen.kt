package com.carlosnicolaugalves.makelifebetter.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlosnicolaugalves.makelifebetter.auth.RegisterResult
import com.carlosnicolaugalves.makelifebetter.util.AppStrings
import com.carlosnicolaugalves.makelifebetter.util.Language
import com.carlosnicolaugalves.makelifebetter.util.Translations

data class RegisterFormData(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

@Composable
fun RegisterScreen(
    strings: AppStrings,
    termsAccepted: Boolean = false,
    registerState: RegisterResult = RegisterResult.Idle,
    initialFormData: RegisterFormData = RegisterFormData(),
    requiredAccessCode: String = "", // From Remote Config - empty means no access code required
    onRegisterClick: (username: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {},
    onTermsClick: (formData: RegisterFormData) -> Unit = {},
    onTermsCheckedChange: (Boolean) -> Unit = {}
) {
    var username by remember { mutableStateOf(initialFormData.username) }
    var email by remember { mutableStateOf(initialFormData.email) }
    var password by remember { mutableStateOf(initialFormData.password) }
    var confirmPassword by remember { mutableStateOf(initialFormData.confirmPassword) }
    var accessCode by remember { mutableStateOf("") }
    var accessCodeError by remember { mutableStateOf(false) }

    // Check if access code is required
    val accessCodeRequired = requiredAccessCode.isNotEmpty()

    val fieldsCompleted =
        username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
    val passwordsMatch = password == confirmPassword
    val passwordLongEnough = password.length >= 8 // Minimum 8 characters for security

    // Access code validation: either not required, or matches the Remote Config value
    val accessCodeValid = !accessCodeRequired || accessCode == requiredAccessCode

    val canRegister = fieldsCompleted && termsAccepted && passwordsMatch && passwordLongEnough && accessCodeValid
    val isLoading = registerState is RegisterResult.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = strings.register,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("${strings.name} *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("${strings.email} *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("${strings.password} *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !isLoading,
            isError = password.isNotBlank() && !passwordLongEnough
        )

        if (password.isNotBlank() && !passwordLongEnough) {
            Text(
                text = strings.passwordMinLength,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("${strings.confirmPassword} *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !isLoading,
            isError = confirmPassword.isNotBlank() && !passwordsMatch
        )

        if (confirmPassword.isNotBlank() && !passwordsMatch) {
            Text(
                text = strings.passwordsDoNotMatch,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Only show access code field if required by Remote Config
        if (accessCodeRequired) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = accessCode,
                onValueChange = {
                    accessCode = it
                    accessCodeError = false
                },
                label = { Text("${strings.accessCode} *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading,
                isError = accessCodeError
            )

            if (accessCodeError) {
                Text(
                    text = strings.invalidAccessCode,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = strings.requiredFields,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        // Error message
        if (registerState is RegisterResult.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = registerState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { checked ->
                    if (!isLoading) onTermsCheckedChange(checked)
                },
                enabled = !isLoading
            )
            Text(
                text = strings.acceptTerms,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .clickable {
                        if (!isLoading) onTermsClick(
                            RegisterFormData(username, email, password, confirmPassword)
                        )
                    }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onRegisterClick(username, email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = canRegister && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(strings.registerButton)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackClick,
            enabled = !isLoading
        ) {
            Text(strings.backToLogin)
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    val strings = Translations.getStrings(Language.PORTUGUESE)

    RegisterScreen(
        strings = strings,
        termsAccepted = false,
        registerState = RegisterResult.Idle,
        onRegisterClick = { _, _, _ -> },
        onBackClick = {},
        onTermsClick = {},
        onTermsCheckedChange = {}
    )
}
