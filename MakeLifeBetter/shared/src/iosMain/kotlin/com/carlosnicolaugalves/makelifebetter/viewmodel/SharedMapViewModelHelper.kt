package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.EventLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SharedMapViewModelWrapper {
    private val viewModel = MapViewModel()

    private var locationObserver: Job? = null

    // MARK: - Observe Event Location State

    fun observeEventLocationState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (EventLocation) -> Unit,
        onError: (String) -> Unit
    ) {
        locationObserver?.cancel()
        locationObserver = viewModel.eventLocationState
            .onEach { state ->
                when (state) {
                    is EventLocationState.Idle -> onIdle()
                    is EventLocationState.Loading -> onLoading()
                    is EventLocationState.Success -> onSuccess(state.eventLocation)
                    is EventLocationState.Error -> onError(state.message)
                }
            }
            .launchIn(CoroutineScope(SupervisorJob() + Dispatchers.Main))
    }

    // MARK: - Actions

    fun refreshEventLocation() {
        viewModel.refreshEventLocation()
    }

    // MARK: - Cleanup

    fun clear() {
        locationObserver?.cancel()
    }
}
