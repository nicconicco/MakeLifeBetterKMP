package com.carlosnicolaugalves.makelifebetter.model

data class ChatMessage(
    val id: String,
    val author: String,
    val message: String,
    val timestamp: Long
)
