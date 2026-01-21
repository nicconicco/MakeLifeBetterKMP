package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import com.carlosnicolaugalves.makelifebetter.repository.getSampleEventSections

/**
 * Main screen composable that displays sections with cards
 * Follows Material Design 3 guidelines and best practices
 */
@Composable
fun SectionedListScreen(
    sections: List<EventSection>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onItemClick: (Event) -> Unit = {}
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sections.forEach { section ->
            // Section Header
            item {
                SectionHeader(title = section.titulo)
            }

            // Section Items
            items(
                items = section.eventos,
                key = { event -> event.id }
            ) { event ->
                EventCard(
                    event = event,
                    onClick = { onItemClick(event) }
                )
            }
        }
    }
}

/**
 * Section header composable
 * Displays the section title with Material Design 3 styling
 */
@Composable
private fun SectionHeader(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
    )
}

/**
 * Card composable for each event
 * Follows Material Design 3 card specifications with proper elevation and padding
 */
@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = event.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = event.subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.hora,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = event.lugar,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Preview with sample data
 * Helps visualize the component during development
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SectionedListScreenPreview() {
    MaterialTheme {
        SectionedListScreen(
            sections = getSampleEventSections()
        )
    }
}

/**
 * Preview for dark theme
 */
@Preview
@Composable
private fun SectionedListScreenDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        SectionedListScreen(
            sections = getSampleEventSections()
        )
    }
}