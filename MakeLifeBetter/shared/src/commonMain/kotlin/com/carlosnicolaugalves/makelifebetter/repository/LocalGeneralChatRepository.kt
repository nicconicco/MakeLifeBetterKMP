package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock

class LocalGeneralChatRepository : GeneralChatRepository {

    private val messages = mutableListOf<ChatMessage>()
    private val _messagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())

    override suspend fun getMessages(): Result<List<ChatMessage>> {
        return Result.success(messages.toList())
    }

    override suspend fun sendMessage(author: String, message: String): Result<ChatMessage> {
        val chatMessage = ChatMessage(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            author = author,
            message = message,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        messages.add(chatMessage)
        _messagesFlow.value = messages.toList()
        return Result.success(chatMessage)
    }

    override fun observeMessages(): Flow<List<ChatMessage>> = _messagesFlow.asStateFlow()
}
