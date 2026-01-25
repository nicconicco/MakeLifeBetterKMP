package com.carlosnicolaugalves.makelifebetter.model

data class QuestionReply(
    val id: String,
    val questionId: String,
    val author: String,
    val content: String,
    val timestamp: Long
)
