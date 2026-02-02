package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface GeneralChatRepository {
    suspend fun getMessages(): Result<List<ChatMessage>>
    suspend fun sendMessage(author: String, message: String): Result<ChatMessage>
    fun observeMessages(): Flow<List<ChatMessage>>
}
