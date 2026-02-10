package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.model.User

class RegisterUseCase(
    private val repository: AuthRepository,
    private val validator: AuthValidator
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        validator.validateRegister(username, email, password, confirmPassword)
            .onFailure { return Result.failure(it) }

        return repository.register(username, email, password)
    }
}
