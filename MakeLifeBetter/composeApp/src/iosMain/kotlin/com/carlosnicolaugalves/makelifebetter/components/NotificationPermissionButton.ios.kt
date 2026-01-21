package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.carlosnicolaugalves.makelifebetter.notification.createNotificationScheduler
import kotlinx.coroutines.launch

@Composable
actual fun NotificationPermissionButton(
    onPermissionResult: (Boolean) -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scheduler = createNotificationScheduler()

    Box(
        modifier = modifier.clickable {
            scope.launch {
                val granted = scheduler.requestPermission()
                onPermissionResult(granted)
            }
        }
    ) {
        content()
    }
}
