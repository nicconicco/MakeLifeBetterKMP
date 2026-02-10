package com.carlosnicolaugalves.makelifebetter.data.repository

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository

/**
 * Factory para criar o repositório de autenticação apropriado para cada plataforma.
 * - Android/iOS: FirebaseAuthRepository
 * - JVM/JS/WASM: LocalAuthRepository (fallback)
 */
expect fun createAuthRepository(): AuthRepository
