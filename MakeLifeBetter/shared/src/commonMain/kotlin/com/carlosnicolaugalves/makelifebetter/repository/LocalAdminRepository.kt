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
}
