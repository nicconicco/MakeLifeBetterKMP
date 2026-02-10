package com.carlosnicolaugalves.makelifebetter.data.repository

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository

actual fun createAuthRepository(): AuthRepository = FirebaseAuthRepository()
