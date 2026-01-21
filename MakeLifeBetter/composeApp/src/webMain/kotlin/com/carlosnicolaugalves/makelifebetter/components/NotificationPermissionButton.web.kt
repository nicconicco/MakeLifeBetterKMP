package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun NotificationPermissionButton(
    onPermissionResult: (Boolean) -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // Web notifications would use the Notification API but for simplicity
    // we'll just grant permission
    Box(
        modifier = modifier.clickable {
            onPermissionResult(true)
        }
    ) {
        content()
    }
}
