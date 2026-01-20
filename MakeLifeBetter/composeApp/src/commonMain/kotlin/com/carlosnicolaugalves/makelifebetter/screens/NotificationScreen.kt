package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class for notification
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String
)

// Main composable for notification list
@Composable
fun NotificationListView(
    notifications: List<Notification>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = notifications,
            key = { it.id }
        ) { notification ->
            NotificationCard(
                notification = notification,
                onDismiss = { onDismiss(notification.id) }
            )
        }
    }
}

// Individual notification card with close button
@Composable
fun NotificationCard(
    notification: Notification,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp) // Space for close button
            ) {
                Text(
                    text = notification.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.timestamp,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            // Close button in top right corner
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .semantics { contentDescription = "Dismiss notification" }
            ) {
                Text(
                    text = "✕",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Example usage with state management
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    // State to hold notifications
    var notifications by remember {
        mutableStateOf(
            buildList {
                repeat(10) { index ->
                    add(
                        Notification(
                            id = (index + 1).toString(),
                            title = "New Message",
                            message = "You have received a new message from John Doe",
                            timestamp = "2 minutes ago"
                        )
                    )
                }
            }
        )
    }

    // Handler for dismissing notifications
    val handleDismiss: (String) -> Unit = { notificationId ->
        notifications = notifications.filter { it.id != notificationId }
    }

    if (notifications.isEmpty()) {
        // Empty state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No notifications",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        NotificationListView(
            notifications = notifications,
            onDismiss = handleDismiss
        )
    }
}

// Preview for development
@Preview(showBackground = true)
@Composable
fun NotificationCardPreview() {
    MaterialTheme {
        NotificationCard(
            notification = Notification(
                id = "1",
                title = "New Message",
                message = "You have received a new message from John Doe",
                timestamp = "2 minutes ago"
            ),
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    MaterialTheme {
        NotificationScreen()
    }
}
