package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ExcelImportResult(
    val eventsImported: Int = 0,
    val locationImported: Boolean = false,
    val contactsImported: Int = 0,
    val errors: List<String> = emptyList()
)

@Composable
expect fun ImportExcelSection(
    onImportSuccess: (ExcelImportResult) -> Unit,
    onImportError: (String) -> Unit,
    modifier: Modifier = Modifier
)
