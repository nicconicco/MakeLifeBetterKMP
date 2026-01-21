package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun NotificationPermissionHandler(
    shouldRequest: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onRequestHandled: () -> Unit
) {
    // Web notifications would use the Notification API but for simplicity
    // we'll just grant permission
    LaunchedEffect(shouldRequest) {
        if (shouldRequest) {
            onPermissionResult(true)
            onRequestHandled()
        }
    }
}
