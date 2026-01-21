package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.carlosnicolaugalves.makelifebetter.notification.createNotificationScheduler
import kotlinx.coroutines.launch

@Composable
actual fun NotificationPermissionHandler(
    shouldRequest: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onRequestHandled: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scheduler = createNotificationScheduler()

    LaunchedEffect(shouldRequest) {
        if (shouldRequest) {
            scope.launch {
                val granted = scheduler.requestPermission()
                onPermissionResult(granted)
            }
            onRequestHandled()
        }
    }
}
