package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.model.User

class UpdateProfileUseCase(
    private val repository: AuthRepository,
    private val validator: AuthValidator
) {
    suspend operator fun invoke(userId: String, username: String, email: String): Result<User> {
        validator.validateProfile(username, email).onFailure { return Result.failure(it) }
        return repository.updateProfile(userId, username, email)
    }
}
