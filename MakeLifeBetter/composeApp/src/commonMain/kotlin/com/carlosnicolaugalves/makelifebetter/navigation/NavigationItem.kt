package com.carlosnicolaugalves.makelifebetter.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
    val title: String,
    val icon: ImageVector
) {
    EVENTO("Event", Icons.Filled.Event),
    LOJA("Store", Icons.Filled.ShoppingCart),
    CHAT("Chat", Icons.AutoMirrored.Filled.Chat),
    MAPA("Map", Icons.Filled.Map),
    MORE("More", Icons.Filled.MoreVert)
}
