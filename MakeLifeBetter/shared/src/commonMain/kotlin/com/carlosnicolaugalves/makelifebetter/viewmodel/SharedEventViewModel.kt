package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.event.EventResult
import com.carlosnicolaugalves.makelifebetter.event.EventSectionsResult
import com.carlosnicolaugalves.makelifebetter.di.provideEventUseCases
import com.carlosnicolaugalves.makelifebetter.domain.event.EventUseCases
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SharedEventViewModel(
    private val eventUseCases: EventUseCases = provideEventUseCases(),
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _eventsState = MutableStateFlow<EventResult>(EventResult.Idle)
    val eventsState: StateFlow<EventResult> = _eventsState.asStateFlow()

    private val _sectionsState = MutableStateFlow<EventSectionsResult>(EventSectionsResult.Idle)
    val sectionsState: StateFlow<EventSectionsResult> = _sectionsState.asStateFlow()

    private val _eventSections = MutableStateFlow<List<EventSection>>(emptyList())
    val eventSections: StateFlow<List<EventSection>> = _eventSections.asStateFlow()

    init {
        loadEventSections()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _eventsState.value = EventResult.Loading

            eventUseCases.loadEvents()
                .onSuccess { events ->
                    _eventsState.value = EventResult.Success(events)
                }
                .onFailure { exception ->
                    _eventsState.value = EventResult.Error(exception.message ?: "Erro ao carregar eventos")
                }
        }
    }

    fun loadEventsByCategory(categoria: String) {
        viewModelScope.launch {
            _eventsState.value = EventResult.Loading

            eventUseCases.loadEventsByCategory(categoria)
                .onSuccess { events ->
                    _eventsState.value = EventResult.Success(events)
                }
                .onFailure { exception ->
                    _eventsState.value = EventResult.Error(exception.message ?: "Erro ao carregar eventos")
                }
        }
    }

    fun loadEventSections() {
        viewModelScope.launch {
            _sectionsState.value = EventSectionsResult.Loading

            eventUseCases.loadEventSections()
                .onSuccess { sections ->
                    _eventSections.value = sections
                    _sectionsState.value = EventSectionsResult.Success(sections)
                }
                .onFailure { exception ->
                    _sectionsState.value = EventSectionsResult.Error(
                        exception.message ?: "Erro ao carregar secoes"
                    )
                }
        }
    }

    fun refreshSections() {
        loadEventSections()
    }

    fun resetEventsState() {
        _eventsState.value = EventResult.Idle
    }

    fun resetSectionsState() {
        _sectionsState.value = EventSectionsResult.Idle
    }

    fun observeEventsState(callback: (EventResult) -> Unit): Job {
        return eventsState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observeSectionsState(callback: (EventSectionsResult) -> Unit): Job {
        return sectionsState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observeEventSections(callback: (List<EventSection>) -> Unit): Job {
        return eventSections.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun clear() {
        viewModelScope.launch {
            // Cleanup if needed
        }
    }
}
