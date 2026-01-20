package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.event.EventResult
import com.carlosnicolaugalves.makelifebetter.event.EventSectionsResult
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import com.carlosnicolaugalves.makelifebetter.repository.EventRepository
import com.carlosnicolaugalves.makelifebetter.repository.createEventRepository
import com.carlosnicolaugalves.makelifebetter.repository.getSampleEventSections
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
    private val repository: EventRepository = createEventRepository()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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

            repository.getEvents()
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

            repository.getEventsByCategory(categoria)
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

            repository.getEventSections()
                .onSuccess { sections ->
                    _eventSections.value = sections
                    _sectionsState.value = EventSectionsResult.Success(sections)
                }
                .onFailure { exception ->
                    val fallbackSections = getSampleEventSections()
                    _eventSections.value = fallbackSections
                    _sectionsState.value = EventSectionsResult.Success(fallbackSections)
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
