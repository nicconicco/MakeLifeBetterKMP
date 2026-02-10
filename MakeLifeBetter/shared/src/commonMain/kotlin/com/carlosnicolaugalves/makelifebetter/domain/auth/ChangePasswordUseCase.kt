package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository

class ChangePasswordUseCase(
    private val repository: AuthRepository,
    private val validator: AuthValidator
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<String> {
        validator.validateChangePassword(currentPassword, newPassword, confirmNewPassword)
            .onFailure { return Result.failure(it) }

        return repository.changePassword(currentPassword, newPassword)
    }
}
