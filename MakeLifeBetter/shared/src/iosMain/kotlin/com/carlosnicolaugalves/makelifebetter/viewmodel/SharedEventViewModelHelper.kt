package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.event.EventResult
import com.carlosnicolaugalves.makelifebetter.event.EventSectionsResult
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun createSharedEventViewModel(): SharedEventViewModel = SharedEventViewModel()

class SharedEventViewModelWrapper {
    private val viewModel = SharedEventViewModel()

    private var eventsObserver: Job? = null
    private var sectionsObserver: Job? = null
    private var eventSectionsObserver: Job? = null

    // MARK: - Load Events

    fun loadEvents() {
        viewModel.loadEvents()
    }

    fun loadEventsByCategory(categoria: String) {
        viewModel.loadEventsByCategory(categoria)
    }

    fun loadEventSections() {
        viewModel.loadEventSections()
    }

    fun refreshSections() {
        viewModel.refreshSections()
    }

    // MARK: - Observe Events State

    fun observeEventsState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (List<Event>) -> Unit,
        onError: (String) -> Unit
    ) {
        eventsObserver?.cancel()
        eventsObserver = viewModel.observeEventsState { result ->
            when (result) {
                is EventResult.Idle -> onIdle()
                is EventResult.Loading -> onLoading()
                is EventResult.Success -> onSuccess(result.events)
                is EventResult.Error -> onError(result.message)
            }
        }
    }

    // MARK: - Observe Sections State

    fun observeSectionsState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (List<EventSection>) -> Unit,
        onError: (String) -> Unit
    ) {
        sectionsObserver?.cancel()
        sectionsObserver = viewModel.observeSectionsState { result ->
            when (result) {
                is EventSectionsResult.Idle -> onIdle()
                is EventSectionsResult.Loading -> onLoading()
                is EventSectionsResult.Success -> onSuccess(result.sections)
                is EventSectionsResult.Error -> onError(result.message)
            }
        }
    }

    // MARK: - Observe Event Sections (direct list)

    fun observeEventSections(callback: (List<EventSection>) -> Unit) {
        eventSectionsObserver?.cancel()
        eventSectionsObserver = viewModel.eventSections
            .onEach { callback(it) }
            .launchIn(CoroutineScope(SupervisorJob() + Dispatchers.Main))
    }

    fun getEventSections(): List<EventSection> = viewModel.eventSections.value

    // MARK: - Reset States

    fun resetEventsState() {
        viewModel.resetEventsState()
    }

    fun resetSectionsState() {
        viewModel.resetSectionsState()
    }

    // MARK: - Cleanup

    fun clear() {
        eventsObserver?.cancel()
        sectionsObserver?.cancel()
        eventSectionsObserver?.cancel()
        viewModel.clear()
    }
}
