package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapView(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
)
