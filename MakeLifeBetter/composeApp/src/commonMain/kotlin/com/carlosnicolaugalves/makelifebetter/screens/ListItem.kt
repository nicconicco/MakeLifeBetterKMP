package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Data class representing an item in the list
 * Following clean code principles with immutable data
 */
data class ListItem(
    val id: Int,
    val title: String,
    val subtitle: String
)

/**
 * Data class representing a section with its items
 */
data class Section(
    val title: String,
    val items: List<ListItem>
)

/**
 * Main screen composable that displays sections with cards
 * Follows Material Design 3 guidelines and best practices
 */
@Composable
fun SectionedListScreen(
    sections: List<Section>,
    modifier: Modifier = Modifier,
    onItemClick: (ListItem) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        sections.forEach { section ->
            // Section Header
            item {
                SectionHeader(title = section.title)
            }

            // Section Items
            items(
                items = section.items,
                key = { item -> item.id }
            ) { item ->
                ItemCard(
                    item = item,
                    onClick = { onItemClick(item) }
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
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 20.dp)
    )
}

/**
 * Card composable for each list item
 * Follows Material Design 3 card specifications with proper elevation and padding
 */
@Composable
private fun ItemCard(
    item: ListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            sections = getSampleSections()
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
            sections = getSampleSections()
        )
    }
}

/**
 * Sample data for preview
 */
fun getSampleSections(): List<Section> {
    return listOf(
        Section(
            title = "O que tá rolando agora 🔴",
            items = listOf(
                ListItem(1, "Keynote: O Futuro da IA Generativa", "Palco Principal • Dr. Roberto Silva"),
                ListItem(2, "Workshop: Compose Multiplatform", "Sala 3B • Live Coding"),
                ListItem(3, "Espaço Networking", "Área de Café • Snacks liberados")
            )
        ),
        Section(
            title = "Ainda vai rolar 📅",
            items = listOf(
                ListItem(4, "Arquitetura Limpa em Escala", "Auditório Azul • 14:00"),
                ListItem(5, "Painel: Carreira Internacional", "Palco Principal • 15:30"),
                ListItem(6, "Happy Hour de Encerramento", "Rooftop • 18:00")
            )
        ),
        Section(
            title = "Novidades 🚀",
            items = listOf(
                ListItem(7, "Lançamento da SDK v2.0", "Visite o stand e ganhe stickers"),
                ListItem(8, "Hackathon Surpresa", "Inscrições abertas na recepção"),
                ListItem(9, "Sorteio de Livros", "Participe via QR Code no telão")
            )
        ),
        Section(
            title = "Canais de Contato 💬",
            items = listOf(
                ListItem(10, "Suporte Técnico", "Precisa de ajuda com o App ou Wi-Fi?"),
                ListItem(11, "Discord da Comunidade", "Converse com outros devs agora"),
                ListItem(12, "Staff do Evento", "Localize pessoas com camiseta laranja")
            )
        ),
        Section(
            title = "Cupons 🎟️",
            items = listOf(
                ListItem(13, "Almoço Food Truck", "15% OFF: BURGERTECH15"),
                ListItem(14, "Uber/99", "Voucher R$10: TECHRIDE2024"),
                ListItem(15, "Cursos Alura", "30% OFF na renovação anual")
            )
        )
    )
}