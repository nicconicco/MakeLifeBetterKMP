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

    override suspend fun populateSampleEvents(): Result<Unit> {
        return try {
            val eventsCollection = firestore.collection("eventos")

            // Eventos de exemplo
            val sampleEvents = listOf(
                mapOf(
                    "titulo" to "Cerimonia de Abertura",
                    "subtitulo" to "Bem-vindos ao evento",
                    "descricao" to "Cerimonia oficial de abertura com discursos e apresentacoes especiais.",
                    "hora" to "09:00",
                    "lugar" to "Salao Principal",
                    "categoria" to "cerimonia"
                ),
                mapOf(
                    "titulo" to "Coffee Break",
                    "subtitulo" to "Pausa para cafe",
                    "descricao" to "Momento de networking e degustacao de cafe e lanches.",
                    "hora" to "10:30",
                    "lugar" to "Area de Convivencia",
                    "categoria" to "intervalo"
                ),
                mapOf(
                    "titulo" to "Palestra Principal",
                    "subtitulo" to "Tema especial do dia",
                    "descricao" to "Palestra inspiradora sobre inovacao e tecnologia.",
                    "hora" to "11:00",
                    "lugar" to "Auditorio",
                    "categoria" to "palestra"
                ),
                mapOf(
                    "titulo" to "Almoco",
                    "subtitulo" to "Refeicao",
                    "descricao" to "Almoco servido no restaurante do local.",
                    "hora" to "12:30",
                    "lugar" to "Restaurante",
                    "categoria" to "refeicao"
                ),
                mapOf(
                    "titulo" to "Workshop",
                    "subtitulo" to "Atividade pratica",
                    "descricao" to "Workshop interativo com atividades em grupo.",
                    "hora" to "14:00",
                    "lugar" to "Sala de Treinamento",
                    "categoria" to "workshop"
                ),
                mapOf(
                    "titulo" to "Encerramento",
                    "subtitulo" to "Despedida",
                    "descricao" to "Cerimonia de encerramento e agradecimentos.",
                    "hora" to "17:00",
                    "lugar" to "Salao Principal",
                    "categoria" to "cerimonia"
                )
            )

            sampleEvents.forEach { event ->
                eventsCollection.add(event)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun populateSampleEventLocation(): Result<Unit> {
        return try {
            val locationCollection = firestore.collection("event_location")

            // Criar documento de localizacao
            val locationDoc = locationCollection.document("main_location")
            locationDoc.set(
                mapOf(
                    "name" to "Centro de Convencoes",
                    "address" to "Av. Principal, 1000",
                    "city" to "Sao Paulo - SP",
                    "latitude" to -23.550520,
                    "longitude" to -46.633308
                )
            )

            // Adicionar contatos na subcoleção
            val contactsCollection = locationDoc.collection("contacts")

            val contacts = listOf(
                mapOf(
                    "name" to "Recepcao",
                    "phone" to "(11) 1234-5678"
                ),
                mapOf(
                    "name" to "Organizacao",
                    "phone" to "(11) 9876-5432"
                ),
                mapOf(
                    "name" to "Emergencia",
                    "phone" to "(11) 9999-9999"
                )
            )

            contacts.forEach { contact ->
                contactsCollection.add(contact)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun populateAllSampleData(): Result<Unit> {
        return try {
            populateSampleEvents()
            populateSampleEventLocation()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadEvents(events: List<Map<String, String>>): Result<Int> {
        return try {
            var count = 0
            val eventsCollection = firestore.collection("eventos")

            events.forEach { event ->
                try {
                    eventsCollection.add(event)
                    count++
                } catch (e: Exception) {
                    // Ignora erros individuais
                }
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadLocation(location: Map<String, Any>): Result<Boolean> {
        return try {
            val locationDoc = firestore.collection("event_location").document("main_location")
            locationDoc.set(location)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadContacts(contacts: List<Map<String, String>>): Result<Int> {
        return try {
            var count = 0
            val contactsCollection = firestore
                .collection("event_location")
                .document("main_location")
                .collection("contacts")

            contacts.forEach { contact ->
                try {
                    contactsCollection.add(contact)
                    count++
                } catch (e: Exception) {
                    // Ignora erros individuais
                }
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
