package com.carlosnicolaugalves.makelifebetter.repository

actual fun createEventLocationRepository(): EventLocationRepository {
    return LocalEventLocationRepository()
}
