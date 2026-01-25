package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.components.MapView
import com.carlosnicolaugalves.makelifebetter.model.EventContact
import com.carlosnicolaugalves.makelifebetter.model.EventLocation
import com.carlosnicolaugalves.makelifebetter.viewmodel.EventLocationState
import com.carlosnicolaugalves.makelifebetter.viewmodel.MapViewModel

// Coordenadas padrao (Curitiba)
const val DEFAULT_LAT = -25.4284
const val DEFAULT_LNG = -49.2733

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = remember { MapViewModel() }
) {
    val eventLocation by mapViewModel.eventLocation.collectAsState()
    val eventLocationState by mapViewModel.eventLocationState.collectAsState()

    when (eventLocationState) {
        is EventLocationState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is EventLocationState.Error -> {
            // Mostrar dados padrao em caso de erro
            MapScreenContent(
                modifier = modifier,
                eventLocation = EventLocation(
                    id = "default",
                    name = "Evento",
                    address = "Endereco nao disponivel",
                    city = "Curitiba, Parana, Brasil",
                    latitude = DEFAULT_LAT,
                    longitude = DEFAULT_LNG,
                    contacts = emptyList()
                )
            )
        }
        else -> {
            MapScreenContent(
                modifier = modifier,
                eventLocation = eventLocation ?: EventLocation(
                    id = "default",
                    name = "Evento",
                    address = "Carregando...",
                    city = "Curitiba, Parana, Brasil",
                    latitude = DEFAULT_LAT,
                    longitude = DEFAULT_LNG,
                    contacts = emptyList()
                )
            )
        }
    }
}

@Composable
private fun MapScreenContent(
    modifier: Modifier = Modifier,
    eventLocation: EventLocation
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Localizacao do Evento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = eventLocation.city,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        // Map
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            MapView(
                latitude = eventLocation.latitude,
                longitude = eventLocation.longitude,
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        // Endereco
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = eventLocation.address,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Contatos
        if (eventLocation.contacts.isNotEmpty()) {
            eventLocation.contacts.forEachIndexed { index, contact ->
                ContactCard(
                    index = index + 1,
                    contact = contact
                )
            }
        } else {
            // Mostrar placeholder se nao houver contatos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Contatos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nenhum contato disponivel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactCard(
    index: Int,
    contact: EventContact
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                text = "$index - ${contact.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contact.phone,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}


@Preview
@Composable
fun MapScreenPreview() {
    MapScreenContent(
        eventLocation = EventLocation(
            id = "preview",
            name = "Evento Preview",
            address = "Rua Exemplo 123 - Curitiba",
            city = "Curitiba, Parana, Brasil",
            latitude = DEFAULT_LAT,
            longitude = DEFAULT_LNG,
            contacts = listOf(
                EventContact(
                    id = "1",
                    name = "Contato 1",
                    phone = "+55 41 9999-9999"
                ),
                EventContact(
                    id = "2",
                    name = "Contato 2",
                    phone = "+55 41 8888-8888"
                )
            )
        )
    )
}