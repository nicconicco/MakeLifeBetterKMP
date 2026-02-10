package com.carlosnicolaugalves.makelifebetter.domain.event

import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository

class EventUseCases(
    repository: EventRepository
) {
    val loadEvents = LoadEventsUseCase(repository)
    val loadEventsByCategory = LoadEventsByCategoryUseCase(repository)
    val loadEventSections = LoadEventSectionsUseCase(repository)
}
