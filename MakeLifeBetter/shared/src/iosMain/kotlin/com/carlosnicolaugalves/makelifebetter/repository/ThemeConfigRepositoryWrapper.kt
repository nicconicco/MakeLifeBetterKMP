package com.carlosnicolaugalves.makelifebetter.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Wrapper para facilitar o uso do ThemeConfigRepository no iOS/Swift.
 */
class ThemeConfigRepositoryWrapper {
    private val repository = ThemeConfigRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun fetchThemeColors(
        onSuccess: (ThemeColors) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                val colors = repository.fetchThemeColors()
                onSuccess(colors)
            } catch (e: Exception) {
                onError(e.message ?: "Erro ao buscar theme config")
            }
        }
    }

    fun getDefaultColors(): ThemeColors = ThemeColorDefaults.colors

    fun clear() {
        scope.cancel()
    }
}
