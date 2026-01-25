package com.carlosnicolaugalves.makelifebetter.repository

interface AdminRepository {
    suspend fun deleteAllEvents(): Result<Unit>
    suspend fun deleteEventLocation(): Result<Unit>
    suspend fun deleteAllChatMessages(): Result<Unit>
    suspend fun deleteAllQuestions(): Result<Unit>
    suspend fun deleteAllData(): Result<Unit>

    // Populate sample data
    suspend fun populateSampleEvents(): Result<Unit>
    suspend fun populateSampleEventLocation(): Result<Unit>
    suspend fun populateAllSampleData(): Result<Unit>

    // Import from Excel
    suspend fun uploadEvents(events: List<Map<String, String>>): Result<Int>
    suspend fun uploadLocation(location: Map<String, Any>): Result<Boolean>
    suspend fun uploadContacts(contacts: List<Map<String, String>>): Result<Int>
}
