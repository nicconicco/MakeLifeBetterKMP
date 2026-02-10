package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.model.User

class LoginUseCase(
    private val repository: AuthRepository,
    private val validator: AuthValidator
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        validator.validateLogin(username, password).onFailure { return Result.failure(it) }
        return repository.login(username, password)
    }
}
