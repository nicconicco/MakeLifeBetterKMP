package com.carlosnicolaugalves.makelifebetter.domain.repository

import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getEventsByCategory(categoria: String): Result<List<Event>>
    suspend fun getEventSections(): Result<List<EventSection>>
}
