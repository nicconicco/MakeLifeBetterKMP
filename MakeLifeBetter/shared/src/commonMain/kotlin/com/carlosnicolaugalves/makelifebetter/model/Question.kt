package com.carlosnicolaugalves.makelifebetter.model

data class Question(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val replies: Int,
    val timestamp: Long
)
