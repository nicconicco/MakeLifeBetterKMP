package com.carlosnicolaugalves.makelifebetter.domain.event

import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository
import com.carlosnicolaugalves.makelifebetter.model.EventSection

class LoadEventSectionsUseCase(
    private val repository: EventRepository
) {
    suspend operator fun invoke(): Result<List<EventSection>> {
        return repository.getEventSections()
    }
}
