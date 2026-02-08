package com.carlosnicolaugalves.makelifebetter.util

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op on Web - browser handles back navigation
}
