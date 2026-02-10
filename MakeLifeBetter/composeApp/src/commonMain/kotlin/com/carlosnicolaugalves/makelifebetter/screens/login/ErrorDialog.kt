package com.carlosnicolaugalves.makelifebetter.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.util.AppStrings
import com.carlosnicolaugalves.makelifebetter.util.Language
import com.carlosnicolaugalves.makelifebetter.util.Translations

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    buttonText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    )
}

@Composable
fun LoginRequiredDialog(
    strings: AppStrings,
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                strings.loginRequired,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(strings.loginRequiredMessage)
        },
        confirmButton = {
            TextButton(onClick = onLoginClick) {
                Text(strings.signIn)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}

@Preview
@Composable
fun ErrorDialogPreview() {
    ErrorDialog(
        title = "Erro no Login",
        message = "Email ou senha incorretos. Por favor, verifique suas credenciais e tente novamente.",
        buttonText = "Tentar novamente",
        onDismiss = {}
    )
}

@Preview
@Composable
fun LoginRequiredDialogPreview() {
    val strings = Translations.getStrings(Language.PORTUGUESE)
    LoginRequiredDialog(
        strings = strings,
        onLoginClick = {},
        onDismiss = {}
    )
}