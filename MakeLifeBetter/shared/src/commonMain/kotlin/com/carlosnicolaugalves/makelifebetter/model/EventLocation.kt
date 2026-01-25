package com.carlosnicolaugalves.makelifebetter.model

data class EventLocation(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val contacts: List<EventContact>
)

data class EventContact(
    val id: String,
    val name: String,
    val phone: String
)
