package com.carlosnicolaugalves.makelifebetter.repository

interface AdminRepository {
    suspend fun deleteAllEvents(): Result<Unit>
    suspend fun deleteEventLocation(): Result<Unit>
    suspend fun deleteAllChatMessages(): Result<Unit>
    suspend fun deleteAllQuestions(): Result<Unit>
    suspend fun deleteAllData(): Result<Unit>
}
