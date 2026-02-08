package com.carlosnicolaugalves.makelifebetter.util

import androidx.compose.runtime.Composable

/**
 * Platform-specific back button handler.
 * On Android: intercepts the system back button.
 * On iOS: no-op (iOS uses swipe gestures which are handled by the navigation).
 * On other platforms: no-op.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit)
