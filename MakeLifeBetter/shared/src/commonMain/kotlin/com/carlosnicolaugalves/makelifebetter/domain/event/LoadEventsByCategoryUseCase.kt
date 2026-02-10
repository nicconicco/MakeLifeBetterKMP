package com.carlosnicolaugalves.makelifebetter.domain.event

import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository
import com.carlosnicolaugalves.makelifebetter.model.Event

class LoadEventsByCategoryUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(categoria: String): Result<List<Event>> {
        return repository.getEventsByCategory(categoria)
    }
}
