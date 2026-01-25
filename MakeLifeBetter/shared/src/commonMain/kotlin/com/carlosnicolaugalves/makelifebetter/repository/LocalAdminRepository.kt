package com.carlosnicolaugalves.makelifebetter.repository

class LocalAdminRepository : AdminRepository {

    override suspend fun deleteAllEvents(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteEventLocation(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteAllChatMessages(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteAllQuestions(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteAllData(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun populateSampleEvents(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun populateSampleEventLocation(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun populateAllSampleData(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun uploadEvents(events: List<Map<String, String>>): Result<Int> {
        return Result.success(0)
    }

    override suspend fun uploadLocation(location: Map<String, Any>): Result<Boolean> {
        return Result.success(false)
    }

    override suspend fun uploadContacts(contacts: List<Map<String, String>>): Result<Int> {
        return Result.success(0)
    }
}
