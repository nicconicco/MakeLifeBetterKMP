package com.carlosnicolaugalves.makelifebetter.domain.auth

class AuthValidator {
    fun validateLogin(username: String, password: String): Result<Unit> {
        return if (username.isBlank() || password.isBlank()) {
            Result.failure(IllegalArgumentException("Preencha todos os campos"))
        } else {
            Result.success(Unit)
        }
    }

    fun validateRegister(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        return when {
            username.isBlank() || email.isBlank() || password.isBlank() -> {
                Result.failure(IllegalArgumentException("Preencha todos os campos"))
            }
            password.length < 8 -> {
                Result.failure(IllegalArgumentException("Senha deve ter pelo menos 8 caracteres"))
            }
            password != confirmPassword -> {
                Result.failure(IllegalArgumentException("As senhas não coincidem"))
            }
            else -> Result.success(Unit)
        }
    }

    fun validateRecoverPassword(email: String): Result<Unit> {
        return if (email.isBlank()) {
            Result.failure(IllegalArgumentException("Informe o email"))
        } else {
            Result.success(Unit)
        }
    }

    fun validateProfile(username: String, email: String): Result<Unit> {
        return if (username.isBlank() || email.isBlank()) {
            Result.failure(IllegalArgumentException("Preencha todos os campos"))
        } else {
            Result.success(Unit)
        }
    }

    fun validateChangePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): Result<Unit> {
        return when {
            currentPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank() -> {
                Result.failure(IllegalArgumentException("Preencha todos os campos"))
            }
            newPassword != confirmNewPassword -> {
                Result.failure(IllegalArgumentException("As novas senhas não coincidem"))
            }
            newPassword.length < 6 -> {
                Result.failure(IllegalArgumentException("Nova senha deve ter pelo menos 6 caracteres"))
            }
            else -> Result.success(Unit)
        }
    }
}
