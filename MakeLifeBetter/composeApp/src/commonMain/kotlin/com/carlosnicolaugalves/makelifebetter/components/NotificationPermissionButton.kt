package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NotificationPermissionButton(
    onPermissionResult: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
