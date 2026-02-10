package com.carlosnicolaugalves.makelifebetter.repository

/**
 * Implementacao local do RemoteConfigRepository para plataformas sem Firebase.
 * Retorna valores padrao (login sempre requerido).
 */
class LocalRemoteConfigRepository : RemoteConfigRepository {

    override suspend fun fetchAndActivate(): Result<Boolean> {
        return Result.success(true)
    }

    override fun isLoginRequired(): Boolean {
        return true // Por padrao, login e requerido
    }

    override fun getAccessCode(): String {
        return "" // Sem codigo de acesso por padrao
    }
}
