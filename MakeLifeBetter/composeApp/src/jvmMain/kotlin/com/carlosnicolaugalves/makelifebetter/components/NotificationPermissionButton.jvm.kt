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
    // Desktop doesn't require notification permissions
    Box(
        modifier = modifier.clickable {
            onPermissionResult(true)
        }
    ) {
        content()
    }
}
