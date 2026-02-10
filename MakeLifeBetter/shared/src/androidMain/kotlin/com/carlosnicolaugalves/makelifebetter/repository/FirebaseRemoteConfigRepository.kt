package com.carlosnicolaugalves.makelifebetter.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig

class FirebaseRemoteConfigRepository : RemoteConfigRepository {

    companion object {
        const val KEY_LOGIN_REQUIRED = "login_required"
        const val KEY_ACCESS_CODE = "access_code"
        const val DEFAULT_ACCESS_CODE = "" // Empty string means no access code required
    }

    private val remoteConfig by lazy { Firebase.remoteConfig }

    private var cachedLoginRequired: Boolean = true // Default: login requerido
    private var cachedAccessCode: String = DEFAULT_ACCESS_CODE

    override suspend fun fetchAndActivate(): Result<Boolean> {
        return try {
            remoteConfig.settings {
                minimumFetchIntervalInSeconds = 3600 // 1 hora
            }
            remoteConfig.setDefaults(
                KEY_LOGIN_REQUIRED to true,
                KEY_ACCESS_CODE to DEFAULT_ACCESS_CODE
            )
            remoteConfig.fetchAndActivate()
            cachedLoginRequired = remoteConfig.getValue(KEY_LOGIN_REQUIRED).asBoolean()
            cachedAccessCode = remoteConfig.getValue(KEY_ACCESS_CODE).asString()
            Result.success(true)
        } catch (e: Exception) {
            // Em caso de erro, usa os valores padrao
            cachedLoginRequired = true
            cachedAccessCode = DEFAULT_ACCESS_CODE
            Result.failure(e)
        }
    }

    override fun isLoginRequired(): Boolean {
        return cachedLoginRequired
    }

    override fun getAccessCode(): String {
        return cachedAccessCode
    }
}
