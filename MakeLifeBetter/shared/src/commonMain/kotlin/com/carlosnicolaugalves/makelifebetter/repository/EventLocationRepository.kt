package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.EventLocation

interface EventLocationRepository {
    suspend fun getEventLocation(): Result<EventLocation>
}
