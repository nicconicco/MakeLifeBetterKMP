package com.carlosnicolaugalves.makelifebetter.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Wrapper para facilitar o uso do RemoteConfigRepository no iOS/Swift.
 */
class RemoteConfigRepositoryWrapper {
    private val repository: RemoteConfigRepository = createRemoteConfigRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun fetchAndActivate(
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            val result = repository.fetchAndActivate()
            result.onSuccess { onSuccess(it) }
                .onFailure { onError(it.message ?: "Erro ao buscar Remote Config") }
        }
    }

    fun isLoginRequired(): Boolean {
        return repository.isLoginRequired()
    }

    fun clear() {
        scope.cancel()
    }
}
