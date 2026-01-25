package com.carlosnicolaugalves.makelifebetter.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseAdminRepository : AdminRepository {

    private val firestore by lazy { Firebase.firestore }

    override suspend fun deleteAllEvents(): Result<Unit> {
        return try {
            val eventsCollection = firestore.collection("eventos")
            val snapshot = eventsCollection.get()

            snapshot.documents.forEach { doc ->
                eventsCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEventLocation(): Result<Unit> {
        return try {
            val locationCollection = firestore.collection("event_location")
            val snapshot = locationCollection.get()

            snapshot.documents.forEach { doc ->
                // Deletar subcoleção de contatos primeiro
                val contactsSnapshot = locationCollection
                    .document(doc.id)
                    .collection("contacts")
                    .get()

                contactsSnapshot.documents.forEach { contactDoc ->
                    locationCollection
                        .document(doc.id)
                        .collection("contacts")
                        .document(contactDoc.id)
                        .delete()
                }

                // Deletar documento principal
                locationCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllChatMessages(): Result<Unit> {
        return try {
            val chatCollection = firestore.collection("lista_geral")
            val snapshot = chatCollection.get()

            snapshot.documents.forEach { doc ->
                chatCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllQuestions(): Result<Unit> {
        return try {
            val questionsCollection = firestore.collection("duvidas")
            val snapshot = questionsCollection.get()

            snapshot.documents.forEach { doc ->
                // Deletar subcoleção de respostas primeiro
                val repliesSnapshot = questionsCollection
                    .document(doc.id)
                    .collection("respostas")
                    .get()

                repliesSnapshot.documents.forEach { replyDoc ->
                    questionsCollection
                        .document(doc.id)
                        .collection("respostas")
                        .document(replyDoc.id)
                        .delete()
                }

                // Deletar documento principal
                questionsCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllData(): Result<Unit> {
        return try {
            deleteAllEvents()
            deleteEventLocation()
            deleteAllChatMessages()
            deleteAllQuestions()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
