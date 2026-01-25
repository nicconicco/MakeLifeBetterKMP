package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.repository.AdminRepository
import com.carlosnicolaugalves.makelifebetter.repository.createAdminRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DeleteDataState {
    object Idle : DeleteDataState()
    object Deleting : DeleteDataState()
    object Success : DeleteDataState()
    data class Error(val message: String) : DeleteDataState()
}

class AdminViewModel(
    private val adminRepository: AdminRepository = createAdminRepository()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _deleteDataState = MutableStateFlow<DeleteDataState>(DeleteDataState.Idle)
    val deleteDataState: StateFlow<DeleteDataState> = _deleteDataState.asStateFlow()

    fun deleteAllData() {
        viewModelScope.launch {
            _deleteDataState.value = DeleteDataState.Deleting

            adminRepository.deleteAllData()
                .onSuccess {
                    _deleteDataState.value = DeleteDataState.Success
                }
                .onFailure { exception ->
                    _deleteDataState.value = DeleteDataState.Error(
                        exception.message ?: "Erro ao deletar dados"
                    )
                }
        }
    }

    fun deleteEvents() {
        viewModelScope.launch {
            _deleteDataState.value = DeleteDataState.Deleting

            adminRepository.deleteAllEvents()
                .onSuccess {
                    _deleteDataState.value = DeleteDataState.Success
                }
                .onFailure { exception ->
                    _deleteDataState.value = DeleteDataState.Error(
                        exception.message ?: "Erro ao deletar eventos"
                    )
                }
        }
    }

    fun deleteEventLocation() {
        viewModelScope.launch {
            _deleteDataState.value = DeleteDataState.Deleting

            adminRepository.deleteEventLocation()
                .onSuccess {
                    _deleteDataState.value = DeleteDataState.Success
                }
                .onFailure { exception ->
                    _deleteDataState.value = DeleteDataState.Error(
                        exception.message ?: "Erro ao deletar localizacao"
                    )
                }
        }
    }

    fun deleteChatMessages() {
        viewModelScope.launch {
            _deleteDataState.value = DeleteDataState.Deleting

            adminRepository.deleteAllChatMessages()
                .onSuccess {
                    _deleteDataState.value = DeleteDataState.Success
                }
                .onFailure { exception ->
                    _deleteDataState.value = DeleteDataState.Error(
                        exception.message ?: "Erro ao deletar mensagens"
                    )
                }
        }
    }

    fun deleteQuestions() {
        viewModelScope.launch {
            _deleteDataState.value = DeleteDataState.Deleting

            adminRepository.deleteAllQuestions()
                .onSuccess {
                    _deleteDataState.value = DeleteDataState.Success
                }
                .onFailure { exception ->
                    _deleteDataState.value = DeleteDataState.Error(
                        exception.message ?: "Erro ao deletar perguntas"
                    )
                }
        }
    }

    fun resetState() {
        _deleteDataState.value = DeleteDataState.Idle
    }
}
