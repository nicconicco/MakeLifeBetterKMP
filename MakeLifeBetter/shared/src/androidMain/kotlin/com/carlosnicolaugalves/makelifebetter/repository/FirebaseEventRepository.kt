package com.carlosnicolaugalves.makelifebetter.repository

import android.util.Log
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseEventRepository : EventRepository {

    private val firestore by lazy { Firebase.firestore }
    private val eventsCollection by lazy { firestore.collection("eventos") }

    override suspend fun getEvents(): Result<List<Event>> {
        val sampleEvents = getSampleEvents()

        return try {
            val querySnapshot = eventsCollection.get()

            val firebaseEvents = querySnapshot.documents.mapNotNull { doc ->
                try {
                    Event(
                        id = doc.id,
                        titulo = doc.get<String>("titulo"),
                        subtitulo = doc.get<String>("subtitulo"),
                        descricao = doc.get<String>("descricao"),
                        hora = doc.get<String>("hora"),
                        lugar = doc.get<String>("lugar"),
                        categoria = doc.get<String?>("categoria") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (firebaseEvents.isNotEmpty()) {
                Result.success(firebaseEvents)
            } else {
                // Se nao encontrou dados, sobe os de exemplo para o Firebase
                uploadSampleEventsToFirebase(sampleEvents)
                Result.success(sampleEvents)
            }
        } catch (e: Exception) {
            // Em caso de erro, tenta subir os dados de exemplo
            try {
                uploadSampleEventsToFirebase(sampleEvents)
            } catch (uploadError: Exception) {
                // Ignora erro de upload, apenas retorna os dados locais
            }
            Result.success(sampleEvents)
        }
    }

    private suspend fun uploadSampleEventsToFirebase(events: List<Event>) {
        events.forEach { event ->
            try {
                eventsCollection.document(event.id).set(
                    mapOf(
                        "titulo" to event.titulo,
                        "subtitulo" to event.subtitulo,
                        "descricao" to event.descricao,
                        "hora" to event.hora,
                        "lugar" to event.lugar,
                        "categoria" to event.categoria
                    )
                )
            } catch (e: Exception) {
                Log.e("uploadSampleEventsToFirebase", e.message.toString())
            }
        }
    }

    override suspend fun getEventsByCategory(categoria: String): Result<List<Event>> {
        val sampleEvents = getSampleEvents().filter { it.categoria == categoria }

        return try {
            val querySnapshot = eventsCollection
                .where { "categoria" equalTo categoria }
                .get()

            val firebaseEvents = querySnapshot.documents.mapNotNull { doc ->
                try {
                    Event(
                        id = doc.id,
                        titulo = doc.get<String>("titulo"),
                        subtitulo = doc.get<String>("subtitulo"),
                        descricao = doc.get<String>("descricao"),
                        hora = doc.get<String>("hora"),
                        lugar = doc.get<String>("lugar"),
                        categoria = doc.get<String?>("categoria") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (firebaseEvents.isNotEmpty()) {
                Result.success(firebaseEvents)
            } else {
                Result.success(sampleEvents)
            }
        } catch (e: Exception) {
            Result.success(sampleEvents)
        }
    }

    override suspend fun getEventSections(): Result<List<EventSection>> {
        // Primeiro prepara os dados de exemplo
        val sampleSections = getSampleEventSections()

        return try {
            val eventsResult = getEvents()

            eventsResult.fold(
                onSuccess = { events ->
                    val sections = createSectionsFromEvents(events)
                    if (sections.isNotEmpty()) {
                        Result.success(sections)
                    } else {
                        Result.success(sampleSections)
                    }
                },
                onFailure = {
                    Result.success(sampleSections)
                }
            )
        } catch (e: Exception) {
            Result.success(sampleSections)
        }
    }

    private fun createSectionsFromEvents(events: List<Event>): List<EventSection> {
        val sectionMap = mapOf(
            "agora" to "O que ta rolando agora",
            "programado" to "Ainda vai rolar",
            "novidade" to "Novidades",
            "contato" to "Canais de Contato",
            "cupom" to "Cupons"
        )

        return sectionMap.mapNotNull { (categoria, titulo) ->
            val categoryEvents = events.filter { it.categoria == categoria }
            if (categoryEvents.isNotEmpty()) {
                EventSection(titulo = titulo, eventos = categoryEvents)
            } else {
                null
            }
        }
    }
}
