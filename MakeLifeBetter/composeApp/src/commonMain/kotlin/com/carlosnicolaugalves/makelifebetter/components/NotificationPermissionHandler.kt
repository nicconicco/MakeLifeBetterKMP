package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable

/**
 * A composable that automatically requests notification permission when shouldRequest is true.
 * This is used to trigger the OS permission dialog programmatically.
 */
@Composable
expect fun NotificationPermissionHandler(
    shouldRequest: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onRequestHandled: () -> Unit
)
