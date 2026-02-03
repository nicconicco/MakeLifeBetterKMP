package com.carlosnicolaugalves.makelifebetter.repository

/**
 * Factory para criar o repositório de Remote Config apropriado para cada plataforma.
 * - Android/iOS: FirebaseRemoteConfigRepository
 * - JVM/JS/WASM: LocalRemoteConfigRepository (fallback)
 */
expect fun createRemoteConfigRepository(): RemoteConfigRepository
