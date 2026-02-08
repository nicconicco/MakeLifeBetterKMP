package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    useSecondaryBackground: Boolean = false,
    modifier: Modifier = Modifier
) {
    val containerColor = if (useSecondaryBackground) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    val itemColors = if (useSecondaryBackground) {
        NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
            unselectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
    } else {
        NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    NavigationBar(
        modifier = modifier,
        containerColor = containerColor
    ) {
        NavigationItem.entries.forEach { navItem ->
            NavigationBarItem(
                icon = {
                    Icon(
                        navItem.icon,
                        contentDescription = "${navItem.icon}"
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
                colors = itemColors
            )
        }
    }
}
