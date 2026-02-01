package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ImagePlaceholder(
    contentDescription: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier.background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Image,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize(0.4f)
        )
    }
}
