package com.carlosnicolaugalves.makelifebetter.domain.event

import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventUseCasesTest {
    @Test
    fun loadEvents_returnsRepositoryData() = runTest {
        val events = listOf(
            Event(
                id = "1",
                titulo = "Event",
                subtitulo = "Subtitle",
                descricao = "Desc",
                hora = "10:00",
                lugar = "Room",
                categoria = "agora"
            )
        )
        val repository = FakeEventRepository().apply {
            eventsResult = Result.success(events)
        }
        val useCases = EventUseCases(repository)

        val result = useCases.loadEvents()

        assertTrue(result.isSuccess)
        assertEquals(events, result.getOrNull())
    }

    private class FakeEventRepository : EventRepository {
        var eventsResult: Result<List<Event>> = Result.failure(IllegalStateException("Not set"))

        override suspend fun getEvents(): Result<List<Event>> {
            return eventsResult
        }

        override suspend fun getEventsByCategory(categoria: String): Result<List<Event>> {
            return Result.failure(IllegalStateException("Not implemented"))
        }

        override suspend fun getEventSections(): Result<List<EventSection>> {
            return Result.failure(IllegalStateException("Not implemented"))
        }
    }
}
