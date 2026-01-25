package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.EventLocation
import com.carlosnicolaugalves.makelifebetter.repository.EventLocationRepository
import com.carlosnicolaugalves.makelifebetter.repository.createEventLocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventLocationState {
    object Idle : EventLocationState()
    object Loading : EventLocationState()
    data class Success(val eventLocation: EventLocation) : EventLocationState()
    data class Error(val message: String) : EventLocationState()
}

class MapViewModel(
    private val eventLocationRepository: EventLocationRepository = createEventLocationRepository()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _eventLocationState = MutableStateFlow<EventLocationState>(EventLocationState.Idle)
    val eventLocationState: StateFlow<EventLocationState> = _eventLocationState.asStateFlow()

    private val _eventLocation = MutableStateFlow<EventLocation?>(null)
    val eventLocation: StateFlow<EventLocation?> = _eventLocation.asStateFlow()

    init {
        loadEventLocation()
    }

    fun loadEventLocation() {
        viewModelScope.launch {
            _eventLocationState.value = EventLocationState.Loading

            eventLocationRepository.getEventLocation()
                .onSuccess { location ->
                    _eventLocation.value = location
                    _eventLocationState.value = EventLocationState.Success(location)
                }
                .onFailure { exception ->
                    _eventLocationState.value = EventLocationState.Error(
                        exception.message ?: "Erro ao carregar localizacao do evento"
                    )
                }
        }
    }

    fun refreshEventLocation() {
        loadEventLocation()
    }
}
