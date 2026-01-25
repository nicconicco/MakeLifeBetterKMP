package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.EventContact
import com.carlosnicolaugalves.makelifebetter.model.EventLocation
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseEventLocationRepository : EventLocationRepository {

    private val firestore by lazy { Firebase.firestore }
    private val eventLocationCollection by lazy { firestore.collection("event_location") }

    override suspend fun getEventLocation(): Result<EventLocation> {
        return try {
            val querySnapshot = eventLocationCollection.get()

            val doc = querySnapshot.documents.firstOrNull()
                ?: return Result.failure(Exception("Nenhuma localizacao encontrada"))

            // Buscar contatos da subcoleção
            val contactsSnapshot = eventLocationCollection
                .document(doc.id)
                .collection("contacts")
                .get()

            val contacts = contactsSnapshot.documents.mapNotNull { contactDoc ->
                try {
                    EventContact(
                        id = contactDoc.id,
                        name = contactDoc.get<String>("name"),
                        phone = contactDoc.get<String>("phone")
                    )
                } catch (e: Exception) {
                    null
                }
            }

            val eventLocation = EventLocation(
                id = doc.id,
                name = doc.get<String>("name"),
                address = doc.get<String>("address"),
                city = doc.get<String>("city"),
                latitude = doc.get<Double>("latitude"),
                longitude = doc.get<Double>("longitude"),
                contacts = contacts
            )

            Result.success(eventLocation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
