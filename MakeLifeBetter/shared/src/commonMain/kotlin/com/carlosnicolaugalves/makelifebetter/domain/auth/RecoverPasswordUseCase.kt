package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository

class RecoverPasswordUseCase(
    private val repository: AuthRepository,
    private val validator: AuthValidator
) {
    suspend operator fun invoke(email: String): Result<String> {
        validator.validateRecoverPassword(email).onFailure { return Result.failure(it) }
        return repository.recoverPassword(email)
    }
}
