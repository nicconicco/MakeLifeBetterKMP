package com.carlosnicolaugalves.makelifebetter.domain.event

import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository
import com.carlosnicolaugalves.makelifebetter.model.Event

class LoadEventsUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(): Result<List<Event>> {
        return repository.getEvents()
    }
}
