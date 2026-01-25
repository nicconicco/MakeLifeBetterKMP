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

sealed class PopulateDataState {
    object Idle : PopulateDataState()
    object Populating : PopulateDataState()
    object Success : PopulateDataState()
    data class Error(val message: String) : PopulateDataState()
}

class AdminViewModel(
    private val adminRepository: AdminRepository = createAdminRepository()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _deleteDataState = MutableStateFlow<DeleteDataState>(DeleteDataState.Idle)
    val deleteDataState: StateFlow<DeleteDataState> = _deleteDataState.asStateFlow()

    private val _populateDataState = MutableStateFlow<PopulateDataState>(PopulateDataState.Idle)
    val populateDataState: StateFlow<PopulateDataState> = _populateDataState.asStateFlow()

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

    fun resetPopulateState() {
        _populateDataState.value = PopulateDataState.Idle
    }

    fun populateAllSampleData() {
        viewModelScope.launch {
            _populateDataState.value = PopulateDataState.Populating

            adminRepository.populateAllSampleData()
                .onSuccess {
                    _populateDataState.value = PopulateDataState.Success
                }
                .onFailure { exception ->
                    _populateDataState.value = PopulateDataState.Error(
                        exception.message ?: "Erro ao popular dados"
                    )
                }
        }
    }

    fun populateSampleEvents() {
        viewModelScope.launch {
            _populateDataState.value = PopulateDataState.Populating

            adminRepository.populateSampleEvents()
                .onSuccess {
                    _populateDataState.value = PopulateDataState.Success
                }
                .onFailure { exception ->
                    _populateDataState.value = PopulateDataState.Error(
                        exception.message ?: "Erro ao popular eventos"
                    )
                }
        }
    }

    fun populateSampleEventLocation() {
        viewModelScope.launch {
            _populateDataState.value = PopulateDataState.Populating

            adminRepository.populateSampleEventLocation()
                .onSuccess {
                    _populateDataState.value = PopulateDataState.Success
                }
                .onFailure { exception ->
                    _populateDataState.value = PopulateDataState.Error(
                        exception.message ?: "Erro ao popular localizacao"
                    )
                }
        }
    }
}
