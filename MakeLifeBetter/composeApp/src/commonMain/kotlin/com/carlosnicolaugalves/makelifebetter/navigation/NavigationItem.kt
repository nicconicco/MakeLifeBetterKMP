package com.carlosnicolaugalves.makelifebetter.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(
    val title: String,
    val icon: ImageVector
) {
    EVENTO("Event", Icons.Filled.Event),
    MAPA("Map", Icons.Filled.Map),
    PERFIL("Me", Icons.Filled.Person),
    CHAT("Chat", Icons.AutoMirrored.Filled.Chat),
    NOTIFICACOES("Alarm", Icons.Filled.Notifications),
    CONTRATE("Contact", Icons.Filled.Work)
}