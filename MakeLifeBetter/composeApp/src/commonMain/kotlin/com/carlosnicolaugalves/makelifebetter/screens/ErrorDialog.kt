package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ErrorDialog(message: String) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                message,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "Por favor tente novamente.",
                fontWeight = FontWeight.Bold
            )
        },
        confirmButton = { },
        dismissButton = {}
    )
}

@Preview
@Composable
fun ErrorDialogPreview() {
    ErrorDialog("Erro no Login")
}