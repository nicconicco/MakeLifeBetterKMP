package com.carlosnicolaugalves.makelifebetter.model

data class AppNotification(
    val id: String,
    val eventId: String,
    val title: String,
    val message: String,
    val scheduledTime: Long,
    val eventTime: Long,
    val createdAt: Long,
    val isRead: Boolean = false,
    val isFired: Boolean = false
)
