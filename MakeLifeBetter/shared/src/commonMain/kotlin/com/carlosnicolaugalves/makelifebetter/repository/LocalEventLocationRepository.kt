package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.EventContact
import com.carlosnicolaugalves.makelifebetter.model.EventLocation

class LocalEventLocationRepository : EventLocationRepository {

    override suspend fun getEventLocation(): Result<EventLocation> {
        // Dados locais de exemplo
        val eventLocation = EventLocation(
            id = "local_1",
            name = "Evento Local",
            address = "Rua Exemplo 123",
            city = "Curitiba, Parana, Brasil",
            latitude = -25.4284,
            longitude = -49.2733,
            contacts = listOf(
                EventContact(
                    id = "contact_1",
                    name = "Contato Exemplo",
                    phone = "+55 41 9999-9999"
                )
            )
        )
        return Result.success(eventLocation)
    }
}
