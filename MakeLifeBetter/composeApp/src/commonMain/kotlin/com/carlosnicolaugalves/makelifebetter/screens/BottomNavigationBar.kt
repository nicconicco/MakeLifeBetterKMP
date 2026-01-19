package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.carlosnicolaugalves.makelifebetter.navigation.NavigationItem

/**
 * Bottom Navigation Bar component
 * Material Design 3 compliant navigation bar
 */
/**
 * Bottom Navigation Bar component
 * Material Design 3 compliant navigation bar
 */
@Composable
fun BottomNavigationBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationItem.entries.forEach { navItem ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = navItem.emoji,
                        fontSize = 24.sp
                    )
                },
                label = {
                    Text(
                        text = navItem.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedItem == navItem,
                onClick = { onItemSelected(navItem) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}