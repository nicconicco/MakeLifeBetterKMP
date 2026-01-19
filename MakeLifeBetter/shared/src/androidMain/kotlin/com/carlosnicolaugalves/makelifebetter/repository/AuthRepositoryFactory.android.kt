package com.carlosnicolaugalves.makelifebetter.repository

actual fun createAuthRepository(): AuthRepository = FirebaseAuthRepository()
