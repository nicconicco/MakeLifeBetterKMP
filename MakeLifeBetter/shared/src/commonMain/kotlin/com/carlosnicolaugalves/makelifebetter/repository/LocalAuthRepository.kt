package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.User
import kotlin.random.Random

class LocalAuthRepository : AuthRepository {

    private val users = mutableMapOf<String, User>()

    init {
        // Usuário padrão para testes
        val defaultUser = User(
            id = "1",
            username = "admin",
            email = "admin@example.com",
            passwordHash = hashPassword("password")
        )
        users[defaultUser.username] = defaultUser
    }

    override suspend fun login(username: String, password: String): Result<User> {
        val user = users[username]

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

        if (password.length < 6) {
            return Result.failure(Exception("Senha deve ter pelo menos 6 caracteres"))
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

        val newPassword = "123456"

        // Atualiza a senha do usuário para 123456
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

    private fun hashPassword(password: String): String {
        // Implementação simples de hash para demonstração
        // Em produção, use uma biblioteca de criptografia adequada
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

        if (newPassword.length < 6) {
            return Result.failure(Exception("Nova senha deve ter pelo menos 6 caracteres"))
        }

        // Simula sucesso para testes locais
        return Result.success("Senha alterada com sucesso")
    }
}
