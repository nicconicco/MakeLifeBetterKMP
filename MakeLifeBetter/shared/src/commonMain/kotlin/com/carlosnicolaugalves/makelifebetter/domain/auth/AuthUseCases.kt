package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository

class AuthUseCases(
    repository: AuthRepository,
    private val validator: AuthValidator = AuthValidator()
) {
    val login = LoginUseCase(repository, validator)
    val register = RegisterUseCase(repository, validator)
    val recoverPassword = RecoverPasswordUseCase(repository, validator)
    val updateProfile = UpdateProfileUseCase(repository, validator)
    val changePassword = ChangePasswordUseCase(repository, validator)
    val logout = LogoutUseCase(repository)

    fun validateLogin(username: String, password: String): Result<Unit> {
        return validator.validateLogin(username, password)
    }

    fun validateRegister(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        return validator.validateRegister(username, email, password, confirmPassword)
    }

    fun validateRecoverPassword(email: String): Result<Unit> {
        return validator.validateRecoverPassword(email)
    }

    fun validateProfile(username: String, email: String): Result<Unit> {
        return validator.validateProfile(username, email)
    }

    fun validateChangePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<Unit> {
        return validator.validateChangePassword(currentPassword, newPassword, confirmNewPassword)
    }
}
