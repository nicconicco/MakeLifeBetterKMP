package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    item: ListItem,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eventDetails = getEventDetails(item.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(
                            text = "← Voltar",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header com emoji grande
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = eventDetails.emoji,
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            // Informacoes do evento
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informacoes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    InfoRow(label = "Data", value = eventDetails.date)
                    InfoRow(label = "Horario", value = eventDetails.time)
                    InfoRow(label = "Local", value = eventDetails.location)
                    InfoRow(label = "Capacidade", value = eventDetails.capacity)
                }
            }

            // Descricao
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Descricao",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = eventDetails.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            // Palestrante/Responsavel
            if (eventDetails.speaker.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Palestrante",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.size(48.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = eventDetails.speaker.firstOrNull()?.uppercase() ?: "?",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = eventDetails.speaker,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = eventDetails.speakerRole,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // Tags
            if (eventDetails.tags.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            eventDetails.tags.forEach { tag ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = tag,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botao de acao
            Button(
                onClick = { /* Acao futura */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(eventDetails.actionButtonText)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

data class EventDetails(
    val emoji: String,
    val date: String,
    val time: String,
    val location: String,
    val capacity: String,
    val description: String,
    val speaker: String,
    val speakerRole: String,
    val tags: List<String>,
    val actionButtonText: String
)

fun getEventDetails(itemId: Int): EventDetails {
    return when (itemId) {
        1 -> EventDetails(
            emoji = "🎤",
            date = "15 de Janeiro, 2026",
            time = "09:00 - 10:30",
            location = "Palco Principal - Centro de Convencoes",
            capacity = "500 pessoas",
            description = "Uma palestra imperdivel sobre o futuro da Inteligencia Artificial Generativa e como ela vai transformar o mercado de trabalho nos proximos anos. Venha descobrir as tendencias e oportunidades que estao surgindo nesse campo revolucionario.",
            speaker = "Dr. Roberto Silva",
            speakerRole = "PhD em IA - MIT",
            tags = listOf("IA", "Futuro", "Tech"),
            actionButtonText = "Reservar lugar"
        )
        2 -> EventDetails(
            emoji = "💻",
            date = "15 de Janeiro, 2026",
            time = "11:00 - 13:00",
            location = "Sala 3B - Ala de Workshops",
            capacity = "30 pessoas",
            description = "Workshop pratico onde voce vai aprender a criar aplicativos multiplataforma usando Compose Multiplatform. Traga seu notebook e saia com um app funcionando em Android, iOS e Desktop!",
            speaker = "Ana Costa",
            speakerRole = "Senior Developer - JetBrains",
            tags = listOf("Kotlin", "Mobile", "Workshop"),
            actionButtonText = "Inscrever-se"
        )
        3 -> EventDetails(
            emoji = "☕",
            date = "15 de Janeiro, 2026",
            time = "10:30 - 11:00",
            location = "Area de Cafe - Terreo",
            capacity = "Ilimitado",
            description = "Momento para relaxar, tomar um cafe e fazer networking com outros participantes do evento. Aproveite para trocar ideias e fazer novas conexoes profissionais.",
            speaker = "",
            speakerRole = "",
            tags = listOf("Networking", "Coffee"),
            actionButtonText = "Ver cardapio"
        )
        4 -> EventDetails(
            emoji = "🏗️",
            date = "15 de Janeiro, 2026",
            time = "14:00 - 15:30",
            location = "Auditorio Azul - 2o Andar",
            capacity = "200 pessoas",
            description = "Aprenda como grandes empresas implementam arquitetura limpa em sistemas de grande escala. Cases reais e licoes aprendidas de projetos com milhoes de usuarios.",
            speaker = "Carlos Mendes",
            speakerRole = "Tech Lead - Nubank",
            tags = listOf("Arquitetura", "Backend"),
            actionButtonText = "Adicionar a agenda"
        )
        5 -> EventDetails(
            emoji = "🌍",
            date = "15 de Janeiro, 2026",
            time = "15:30 - 17:00",
            location = "Palco Principal - Centro de Convencoes",
            capacity = "500 pessoas",
            description = "Painel com profissionais que trabalham em empresas internacionais compartilhando suas experiencias, desafios e dicas para quem quer construir uma carreira global em tecnologia.",
            speaker = "Diversos palestrantes",
            speakerRole = "Profissionais de Google, Meta e Microsoft",
            tags = listOf("Carreira", "Internacional"),
            actionButtonText = "Participar"
        )
        6 -> EventDetails(
            emoji = "🍺",
            date = "15 de Janeiro, 2026",
            time = "18:00 - 22:00",
            location = "Rooftop - Ultimo Andar",
            capacity = "150 pessoas",
            description = "Encerramento do evento com musica ao vivo, open bar e muito networking em um ambiente descontraido. Vista panoramica da cidade e clima de celebracao!",
            speaker = "",
            speakerRole = "",
            tags = listOf("Social", "Party"),
            actionButtonText = "Confirmar presenca"
        )
        7 -> EventDetails(
            emoji = "🚀",
            date = "15 de Janeiro, 2026",
            time = "O dia todo",
            location = "Stand da SDK Company - Hall Principal",
            capacity = "Ilimitado",
            description = "Conheca em primeira mao a nova versao da SDK com suporte a IA generativa, performance 3x mais rapida e novos componentes de UI. Ganhe stickers exclusivos visitando o stand!",
            speaker = "Equipe SDK Company",
            speakerRole = "Product Team",
            tags = listOf("SDK", "Lancamento"),
            actionButtonText = "Visitar stand"
        )
        8 -> EventDetails(
            emoji = "⚡",
            date = "15 de Janeiro, 2026",
            time = "19:00 - 23:59",
            location = "Laboratorio Tech - Subsolo",
            capacity = "50 times (3 pessoas)",
            description = "Hackathon surpresa com tema revelado na hora! Monte seu time, desenvolva uma solucao inovadora em 5 horas e concorra a premios incriveis. Alimentacao e energeticos inclusos!",
            speaker = "",
            speakerRole = "",
            tags = listOf("Hackathon", "Premios"),
            actionButtonText = "Inscrever time"
        )
        9 -> EventDetails(
            emoji = "📚",
            date = "15 de Janeiro, 2026",
            time = "Sorteio as 17:00",
            location = "Palco Principal",
            capacity = "Todos os participantes",
            description = "Participe do sorteio de livros de tecnologia das principais editoras! Escaneie o QR Code no telao para se inscrever. Serao sorteados 20 livros de autores renomados.",
            speaker = "",
            speakerRole = "",
            tags = listOf("Sorteio", "Livros"),
            actionButtonText = "Escanear QR Code"
        )
        10 -> EventDetails(
            emoji = "🛠️",
            date = "15 de Janeiro, 2026",
            time = "08:00 - 22:00",
            location = "Balcao de Atendimento - Entrada",
            capacity = "Atendimento continuo",
            description = "Problemas com o app do evento? Wi-Fi nao conecta? Precisa de tomada para carregar o notebook? Nossa equipe de suporte tecnico esta disponivel durante todo o evento para ajudar!",
            speaker = "Equipe de Suporte",
            speakerRole = "TI do Evento",
            tags = listOf("Suporte", "Ajuda"),
            actionButtonText = "Abrir chamado"
        )
        11 -> EventDetails(
            emoji = "💬",
            date = "Permanente",
            time = "24/7",
            location = "Online",
            capacity = "Ilimitado",
            description = "Entre no servidor do Discord da comunidade e continue as conversas apos o evento! Canais tematicos, oportunidades de emprego e muito mais. A comunidade que nao para!",
            speaker = "",
            speakerRole = "",
            tags = listOf("Discord", "Comunidade"),
            actionButtonText = "Entrar no Discord"
        )
        12 -> EventDetails(
            emoji = "👕",
            date = "15 de Janeiro, 2026",
            time = "08:00 - 22:00",
            location = "Por todo o evento",
            capacity = "20 pessoas",
            description = "Precisa de ajuda? Procure as pessoas com camiseta laranja - sao nossos voluntarios e staff! Eles podem ajudar com informacoes, direcoes e qualquer duvida sobre o evento.",
            speaker = "",
            speakerRole = "",
            tags = listOf("Staff", "Ajuda"),
            actionButtonText = "Ver mapa do evento"
        )
        13 -> EventDetails(
            emoji = "🍔",
            date = "15 de Janeiro, 2026",
            time = "11:30 - 14:30",
            location = "Area de Food Trucks - Estacionamento",
            capacity = "Ilimitado",
            description = "Use o cupom BURGERTECH15 e ganhe 15% de desconto em qualquer lanche dos food trucks parceiros! Valido apenas no dia do evento. Nao acumulativo com outras promocoes.",
            speaker = "",
            speakerRole = "",
            tags = listOf("Cupom", "Comida"),
            actionButtonText = "Copiar cupom"
        )
        14 -> EventDetails(
            emoji = "🚗",
            date = "15 de Janeiro, 2026",
            time = "Valido ate meia-noite",
            location = "App Uber/99",
            capacity = "Primeiros 200 usos",
            description = "Voucher de R$10 para sua corrida de volta para casa! Use o codigo TECHRIDE2024 no app da Uber ou 99. Valido para corridas com destino partindo do evento.",
            speaker = "",
            speakerRole = "",
            tags = listOf("Cupom", "Transporte"),
            actionButtonText = "Copiar codigo"
        )
        15 -> EventDetails(
            emoji = "🎓",
            date = "Valido ate 31/01/2026",
            time = "Online",
            location = "alura.com.br",
            capacity = "Ilimitado",
            description = "Exclusivo para participantes do evento! 30% de desconto na renovacao ou primeira assinatura anual da Alura. Acesso a mais de 1500 cursos de tecnologia, design e negocios.",
            speaker = "",
            speakerRole = "",
            tags = listOf("Cupom", "Cursos"),
            actionButtonText = "Resgatar desconto"
        )
        else -> EventDetails(
            emoji = "📌",
            date = "A definir",
            time = "A definir",
            location = "A definir",
            capacity = "A definir",
            description = "Mais informacoes em breve.",
            speaker = "",
            speakerRole = "",
            tags = emptyList(),
            actionButtonText = "Saiba mais"
        )
    }
}
