package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}
