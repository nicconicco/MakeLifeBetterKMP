package com.carlosnicolaugalves.makelifebetter.util

import android.content.Context
import android.net.Uri
import android.util.Log
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

data class ImportResult(
    val eventsImported: Int = 0,
    val locationImported: Boolean = false,
    val contactsImported: Int = 0,
    val errors: List<String> = emptyList()
)

data class ParsedExcelData(
    val events: List<Map<String, String>> = emptyList(),
    val location: Map<String, Any>? = null,
    val contacts: List<Map<String, String>> = emptyList(),
    val errors: List<String> = emptyList()
)

class ExcelImportService(private val context: Context) {

    suspend fun importFromExcel(
        uri: Uri,
        uploadEvents: suspend (List<Map<String, String>>) -> Int,
        uploadLocation: suspend (Map<String, Any>) -> Boolean,
        uploadContacts: suspend (List<Map<String, String>>) -> Int
    ): Result<ImportResult> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Nao foi possivel abrir o arquivo"))

            val parsedData = parseExcel(inputStream)
            inputStream.close()

            // Upload dos dados parseados
            var eventsImported = 0
            var locationImported = false
            var contactsImported = 0

            if (parsedData.events.isNotEmpty()) {
                eventsImported = uploadEvents(parsedData.events)
            }

            if (parsedData.location != null) {
                locationImported = uploadLocation(parsedData.location)
            }

            if (parsedData.contacts.isNotEmpty()) {
                contactsImported = uploadContacts(parsedData.contacts)
            }

            Result.success(
                ImportResult(
                    eventsImported = eventsImported,
                    locationImported = locationImported,
                    contactsImported = contactsImported,
                    errors = parsedData.errors
                )
            )
        } catch (e: Exception) {
            Log.e("ExcelImportService", "Erro ao importar Excel: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun parseExcel(inputStream: InputStream): ParsedExcelData {
        val workbook = WorkbookFactory.create(inputStream)
        val errors = mutableListOf<String>()
        var events = emptyList<Map<String, String>>()
        var location: Map<String, Any>? = null
        var contacts = emptyList<Map<String, String>>()

        // Procurar aba "Eventos"
        val eventosSheet = workbook.getSheet("Eventos")
        if (eventosSheet != null) {
            events = parseEventsSheet(eventosSheet, errors)
            Log.d("ExcelImportService", "Eventos parseados: ${events.size}")
        } else {
            errors.add("Aba 'Eventos' nao encontrada")
        }

        // Procurar aba "Localizacao"
        val localizacaoSheet = workbook.getSheet("Localizacao")
        if (localizacaoSheet != null) {
            location = parseLocationSheet(localizacaoSheet, errors)
            Log.d("ExcelImportService", "Localizacao parseada: ${location != null}")
        } else {
            errors.add("Aba 'Localizacao' nao encontrada")
        }

        // Procurar aba "Contatos"
        val contatosSheet = workbook.getSheet("Contatos")
        if (contatosSheet != null) {
            contacts = parseContactsSheet(contatosSheet, errors)
            Log.d("ExcelImportService", "Contatos parseados: ${contacts.size}")
        } else {
            errors.add("Aba 'Contatos' nao encontrada")
        }

        workbook.close()

        return ParsedExcelData(
            events = events,
            location = location,
            contacts = contacts,
            errors = errors
        )
    }

    private fun parseEventsSheet(sheet: org.apache.poi.ss.usermodel.Sheet, errors: MutableList<String>): List<Map<String, String>> {
        val events = mutableListOf<Map<String, String>>()

        // Pular cabecalho (linha 0)
        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue

            try {
                val titulo = getCellValueAsString(row, 0)
                val subtitulo = getCellValueAsString(row, 1)
                val descricao = getCellValueAsString(row, 2)
                val hora = getCellValueAsString(row, 3)
                val lugar = getCellValueAsString(row, 4)
                val categoria = getCellValueAsString(row, 5)

                if (titulo.isNotBlank()) {
                    events.add(
                        mapOf(
                            "titulo" to titulo,
                            "subtitulo" to subtitulo,
                            "descricao" to descricao,
                            "hora" to hora,
                            "lugar" to lugar,
                            "categoria" to categoria
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("Erro na linha ${rowIndex + 1} de Eventos: ${e.message}")
            }
        }

        return events
    }

    private fun parseLocationSheet(sheet: org.apache.poi.ss.usermodel.Sheet, errors: MutableList<String>): Map<String, Any>? {
        return try {
            // Linha 1 (apos cabecalho) contem os dados
            val row = sheet.getRow(1) ?: return null

            val name = getCellValueAsString(row, 0)
            val address = getCellValueAsString(row, 1)
            val city = getCellValueAsString(row, 2)
            val latitude = getCellValueAsDouble(row, 3)
            val longitude = getCellValueAsDouble(row, 4)

            if (name.isNotBlank()) {
                mapOf(
                    "name" to name,
                    "address" to address,
                    "city" to city,
                    "latitude" to latitude,
                    "longitude" to longitude
                )
            } else {
                null
            }
        } catch (e: Exception) {
            errors.add("Erro ao parsear Localizacao: ${e.message}")
            null
        }
    }

    private fun parseContactsSheet(sheet: org.apache.poi.ss.usermodel.Sheet, errors: MutableList<String>): List<Map<String, String>> {
        val contacts = mutableListOf<Map<String, String>>()

        // Pular cabecalho (linha 0)
        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue

            try {
                val name = getCellValueAsString(row, 0)
                val phone = getCellValueAsString(row, 1)

                if (name.isNotBlank()) {
                    contacts.add(
                        mapOf(
                            "name" to name,
                            "phone" to phone
                        )
                    )
                }
            } catch (e: Exception) {
                errors.add("Erro na linha ${rowIndex + 1} de Contatos: ${e.message}")
            }
        }

        return contacts
    }

    private fun getCellValueAsString(row: Row, cellIndex: Int): String {
        val cell = row.getCell(cellIndex) ?: return ""
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> cell.stringCellValue
            else -> ""
        }.trim()
    }

    private fun getCellValueAsDouble(row: Row, cellIndex: Int): Double {
        val cell = row.getCell(cellIndex) ?: return 0.0
        return when (cell.cellType) {
            CellType.NUMERIC -> cell.numericCellValue
            CellType.STRING -> cell.stringCellValue.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }
}
