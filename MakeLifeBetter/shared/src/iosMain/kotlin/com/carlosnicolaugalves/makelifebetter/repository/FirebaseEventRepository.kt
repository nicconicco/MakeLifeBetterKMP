package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseEventRepository : EventRepository {

    private val firestore by lazy { Firebase.firestore }
    private val eventsCollection by lazy { firestore.collection("eventos") }

    override suspend fun getEvents(): Result<List<Event>> {
        return try {
            // Primeiro tenta buscar do servidor
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
                // Se nao encontrou dados no servidor, usa dados locais como fallback
                Result.success(getSampleEvents())
            }
        } catch (e: Exception) {
            // Em caso de erro de conexao, usa dados locais como fallback
            Result.success(getSampleEvents())
        }
    }

    override suspend fun getEventsByCategory(categoria: String): Result<List<Event>> {
        return try {
            // Primeiro tenta buscar do servidor
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
                // Fallback para dados locais filtrados por categoria
                Result.success(getSampleEvents().filter { it.categoria == categoria })
            }
        } catch (e: Exception) {
            // Fallback para dados locais filtrados por categoria
            Result.success(getSampleEvents().filter { it.categoria == categoria })
        }
    }

    override suspend fun getEventSections(): Result<List<EventSection>> {
        return try {
            val eventsResult = getEvents()

            eventsResult.fold(
                onSuccess = { events ->
                    val sections = createSectionsFromEvents(events)
                    if (sections.isNotEmpty()) {
                        Result.success(sections)
                    } else {
                        // Fallback para secoes locais
                        Result.success(getSampleEventSections())
                    }
                },
                onFailure = {
                    // Fallback para secoes locais
                    Result.success(getSampleEventSections())
                }
            )
        } catch (e: Exception) {
            // Fallback para secoes locais
            Result.success(getSampleEventSections())
        }
    }

    private fun createSectionsFromEvents(events: List<Event>): List<EventSection> {
        val sectionMap = mapOf(
            "agora" to "O que ta rolando agora",
            "programado" to "Ainda vai rolar",
            "novidade" to "Novidades",
            "contato" to "Canais de Contato",
            "cupom" to "Cupons",
            "cerimonia" to "Cerimonias",
            "intervalo" to "Intervalos",
            "palestra" to "Palestras",
            "refeicao" to "Refeicoes",
            "workshop" to "Workshops"
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
