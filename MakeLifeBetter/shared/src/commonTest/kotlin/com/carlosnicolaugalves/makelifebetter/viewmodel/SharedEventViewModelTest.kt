package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.domain.event.EventUseCases
import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository
import com.carlosnicolaugalves.makelifebetter.event.EventSectionsResult
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedEventViewModelTest {
    @Test
    fun init_loadsEventSections() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val sections = listOf(
            EventSection(
                titulo = "Agora",
                eventos = listOf(
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
            )
        )
        val repository = FakeEventRepository().apply {
            sectionsResult = Result.success(sections)
        }
        val viewModel = SharedEventViewModel(
            eventUseCases = EventUseCases(repository),
            dispatcher = dispatcher
        )

        advanceUntilIdle()

        val state = viewModel.sectionsState.value
        assertTrue(state is EventSectionsResult.Success)
        assertEquals(sections, state.sections)
        assertEquals(sections, viewModel.eventSections.value)
    }

    private class FakeEventRepository : EventRepository {
        var sectionsResult: Result<List<EventSection>> = Result.failure(IllegalStateException("Not set"))

        override suspend fun getEvents(): Result<List<Event>> {
            return Result.failure(IllegalStateException("Not implemented"))
        }

        override suspend fun getEventsByCategory(categoria: String): Result<List<Event>> {
            return Result.failure(IllegalStateException("Not implemented"))
        }

        override suspend fun getEventSections(): Result<List<EventSection>> {
            return sectionsResult
        }
    }
}
