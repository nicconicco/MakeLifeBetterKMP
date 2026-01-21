package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun NotificationPermissionHandler(
    shouldRequest: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onRequestHandled: () -> Unit
) {
    // Desktop doesn't require notification permissions
    LaunchedEffect(shouldRequest) {
        if (shouldRequest) {
            onPermissionResult(true)
            onRequestHandled()
        }
    }
}
