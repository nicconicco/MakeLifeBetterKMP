package com.carlosnicolaugalves.makelifebetter.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig

class FirebaseRemoteConfigRepository : RemoteConfigRepository {

    companion object {
        const val KEY_LOGIN_REQUIRED = "login_required"
    }

    private val remoteConfig by lazy { Firebase.remoteConfig }

    private var cachedLoginRequired: Boolean = true // Default: login requerido

    override suspend fun fetchAndActivate(): Result<Boolean> {
        return try {
            remoteConfig.settings {
                minimumFetchIntervalInSeconds = 3600 // 1 hora
            }
            remoteConfig.setDefaults(KEY_LOGIN_REQUIRED to true)
            remoteConfig.fetchAndActivate()
            cachedLoginRequired = remoteConfig.getValue(KEY_LOGIN_REQUIRED).asBoolean()
            Result.success(true)
        } catch (e: Exception) {
            // Em caso de erro, usa o valor padrao (login requerido)
            cachedLoginRequired = true
            Result.failure(e)
        }
    }

    override fun isLoginRequired(): Boolean {
        return cachedLoginRequired
    }
}
