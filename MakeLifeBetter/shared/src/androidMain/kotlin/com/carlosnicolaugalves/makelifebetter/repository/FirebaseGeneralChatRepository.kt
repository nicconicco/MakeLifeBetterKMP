package com.carlosnicolaugalves.makelifebetter.repository

import android.util.Log
import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.DataSnapshot
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock

class FirebaseGeneralChatRepository : GeneralChatRepository {

    private val database by lazy { Firebase.database }
    private val messagesRef by lazy { database.reference("lista_geral") }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun parseSnapshot(snapshot: DataSnapshot): List<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()
        snapshot.children.forEach { child ->
            try {
                val id = child.key ?: return@forEach
                val data = child.value as? Map<*, *> ?: return@forEach
                val author = data["author"]?.toString() ?: return@forEach
                val message = data["message"]?.toString() ?: return@forEach
                val timestamp = (data["timestamp"] as? Number)?.toLong() ?: return@forEach

                messages.add(
                    ChatMessage(
                        id = id,
                        author = author,
                        message = message,
                        timestamp = timestamp
                    )
                )
            } catch (e: Exception) {
                Log.e("FirebaseChatRepo", "Error parsing message: ${e.message}")
            }
        }
        return messages.sortedBy { it.timestamp }
    }

    override suspend fun getMessages(): Result<List<ChatMessage>> {
        return try {
            // Use valueEvents to get the current snapshot
            val snapshot = messagesRef.valueEvents.first()
            val messages = parseSnapshot(snapshot)
            Result.success(messages)
        } catch (e: Exception) {
            Log.e("FirebaseChatRepo", "Error getting messages: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(author: String, message: String): Result<ChatMessage> {
        return try {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val newMessageRef = messagesRef.push()

            newMessageRef.setValue(
                mapOf(
                    "author" to author,
                    "message" to message,
                    "timestamp" to timestamp
                )
            )

            val chatMessage = ChatMessage(
                id = newMessageRef.key ?: "",
                author = author,
                message = message,
                timestamp = timestamp
            )

            Result.success(chatMessage)
        } catch (e: Exception) {
            Log.e("FirebaseChatRepo", "Error sending message: ${e.message}")
            Result.failure(e)
        }
    }

    override fun observeMessages(): Flow<List<ChatMessage>> = callbackFlow {
        scope.launch {
            try {
                messagesRef.valueEvents.collect { snapshot ->
                    val messages = parseSnapshot(snapshot)
                    trySend(messages)
                }
            } catch (e: Exception) {
                Log.e("FirebaseChatRepo", "Error observing messages: ${e.message}")
            }
        }

        awaitClose { }
    }
}
