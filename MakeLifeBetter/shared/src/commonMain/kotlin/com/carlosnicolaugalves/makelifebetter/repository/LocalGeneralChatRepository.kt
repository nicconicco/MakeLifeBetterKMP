package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import kotlin.time.Clock

class LocalGeneralChatRepository : GeneralChatRepository {

    private val messages = mutableListOf<ChatMessage>()

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
        return Result.success(chatMessage)
    }
}
