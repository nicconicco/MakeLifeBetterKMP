package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock

class FirebaseGeneralChatRepository : GeneralChatRepository {

    private val firestore by lazy { Firebase.firestore }
    private val messagesCollection by lazy { firestore.collection("lista_geral") }

    override suspend fun getMessages(): Result<List<ChatMessage>> {
        return try {
            val querySnapshot = messagesCollection
                .orderBy("timestamp", Direction.ASCENDING)
                .get()

            val messages = querySnapshot.documents.mapNotNull { doc ->
                try {
                    ChatMessage(
                        id = doc.id,
                        author = doc.get<String>("author"),
                        message = doc.get<String>("message"),
                        timestamp = doc.get<Long>("timestamp")
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(author: String, message: String): Result<ChatMessage> {
        return try {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val docRef = messagesCollection.add(
                mapOf(
                    "author" to author,
                    "message" to message,
                    "timestamp" to timestamp
                )
            )

            val chatMessage = ChatMessage(
                id = docRef.id,
                author = author,
                message = message,
                timestamp = timestamp
            )

            Result.success(chatMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
