package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.repository.createAdminRepository
import com.carlosnicolaugalves.makelifebetter.util.ExcelImportService
import com.carlosnicolaugalves.makelifebetter.util.rememberExcelFilePicker
import kotlinx.coroutines.launch

@Composable
actual fun ImportExcelSection(
    onImportSuccess: (ExcelImportResult) -> Unit,
    onImportError: (String) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isImporting by remember { mutableStateOf(false) }

    val excelImportService = remember { ExcelImportService(context) }
    val adminRepository = remember { createAdminRepository() }

    val pickExcelFile = rememberExcelFilePicker { uri ->
        scope.launch {
            isImporting = true
            excelImportService.importFromExcel(
                uri = uri,
                uploadEvents = { events ->
                    adminRepository.uploadEvents(events).getOrDefault(0)
                },
                uploadLocation = { location ->
                    adminRepository.uploadLocation(location).getOrDefault(false)
                },
                uploadContacts = { contacts ->
                    adminRepository.uploadContacts(contacts).getOrDefault(0)
                }
            )
                .onSuccess { result ->
                    isImporting = false
                    onImportSuccess(
                        ExcelImportResult(
                            eventsImported = result.eventsImported,
                            locationImported = result.locationImported,
                            contactsImported = result.contactsImported,
                            errors = result.errors
                        )
                    )
                }
                .onFailure { error ->
                    isImporting = false
                    onImportError(error.message ?: "Erro desconhecido ao importar")
                }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Importar Excel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Importe dados de uma planilha Excel (.xlsx) com as abas: Eventos, Localizacao, Contatos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { pickExcelFile() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                enabled = !isImporting
            ) {
                if (isImporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onTertiary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Importando...")
                } else {
                    Icon(
                        Icons.Default.Upload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Selecionar Arquivo Excel")
                }
            }
        }
    }
}
