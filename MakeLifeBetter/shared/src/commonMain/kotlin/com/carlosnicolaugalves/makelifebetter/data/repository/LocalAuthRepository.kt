package com.carlosnicolaugalves.makelifebetter.data.repository

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.model.User
import kotlin.random.Random

/**
 * LOCAL DEVELOPMENT ONLY - DO NOT USE IN PRODUCTION!
 *
 * This repository is for local testing and development purposes only.
 * For production, use FirebaseAuthRepository which provides secure authentication.
 *
 * Security warnings:
 * - Uses weak hashCode() for password hashing (NOT cryptographically secure)
 * - Stores passwords in memory (no persistence)
 * - No rate limiting or brute force protection
 */
class LocalAuthRepository : AuthRepository {

    private val users = mutableMapOf<String, User>()

    // No default users - users must register first
    // This prevents hardcoded credentials in the codebase

    override suspend fun login(username: String, password: String): Result<User> {
        val user = if (username.contains("@")) {
            users.values.find { it.email == username }
        } else {
            users[username]
        }

        return when {
            user == null -> Result.failure(Exception("Usuário não encontrado"))
            user.passwordHash != hashPassword(password) -> Result.failure(Exception("Senha incorreta"))
            else -> Result.success(user)
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        if (users.containsKey(username)) {
            return Result.failure(Exception("Nome de usuário já existe"))
        }

        if (users.values.any { it.email == email }) {
            return Result.failure(Exception("Email já cadastrado"))
        }

        if (username.length < 3) {
            return Result.failure(Exception("Nome de usuário deve ter pelo menos 3 caracteres"))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Senha deve ter pelo menos 8 caracteres"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(Exception("Email inválido"))
        }

        val newUser = User(
            id = generateId(),
            username = username,
            email = email,
            passwordHash = hashPassword(password)
        )

        users[username] = newUser
        return Result.success(newUser)
    }

    override suspend fun recoverPassword(email: String): Result<String> {
        val user = users.values.find { it.email == email }
            ?: return Result.failure(Exception("Email não encontrado"))

        // Generate a random temporary password
        val newPassword = generateTemporaryPassword()

        val updatedUser = user.copy(passwordHash = hashPassword(newPassword))
        users[user.username] = updatedUser

        return Result.success("Senha redefinida para: $newPassword")
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return users[username]
    }

    /**
     * WARNING: This is NOT a secure password hash!
     * hashCode() is NOT cryptographically secure and should NEVER be used in production.
     * For production, use bcrypt, Argon2, PBKDF2, or scrypt.
     * This implementation is only for local testing purposes.
     */
    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }

    private fun generateId(): String {
        return Random.nextLong(1000000, 9999999).toString()
    }

    private fun generateTemporaryPassword(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789"
        return (1..8).map { chars.random() }.joinToString("")
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    override suspend fun updateProfile(userId: String, username: String, email: String): Result<User> {
        val existingUser = users.values.find { it.id == userId }
            ?: return Result.failure(Exception("Usuario nao encontrado"))

        if (username.length < 3) {
            return Result.failure(Exception("Nome de usuario deve ter pelo menos 3 caracteres"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(Exception("Email invalido"))
        }

        // Verifica se o novo username ja existe (exceto para o proprio usuario)
        if (users.containsKey(username) && users[username]?.id != userId) {
            return Result.failure(Exception("Nome de usuario ja existe"))
        }

        // Verifica se o novo email ja existe (exceto para o proprio usuario)
        if (users.values.any { it.email == email && it.id != userId }) {
            return Result.failure(Exception("Email ja cadastrado"))
        }

        // Remove o usuario antigo pelo username antigo
        users.remove(existingUser.username)

        val updatedUser = existingUser.copy(
            username = username,
            email = email
        )

        users[username] = updatedUser
        return Result.success(updatedUser)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
        // Para LocalAuthRepository, precisamos de um usuario logado
        // Como nao temos acesso ao usuario atual aqui, vamos apenas simular
        // Em uma implementacao real, isso seria tratado de forma diferente

        if (newPassword.length < 8) {
            return Result.failure(Exception("Nova senha deve ter pelo menos 8 caracteres"))
        }

        // Simula sucesso para testes locais
        return Result.success("Senha alterada com sucesso")
    }

    override suspend fun logout(): Result<Unit> {
        // Nada a fazer no modo local, mas mantemos a assinatura consistente
        return Result.success(Unit)
    }
}
