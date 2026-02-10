package com.carlosnicolaugalves.makelifebetter.repository

interface RemoteConfigRepository {
    suspend fun fetchAndActivate(): Result<Boolean>
    fun isLoginRequired(): Boolean
    fun getAccessCode(): String
}
