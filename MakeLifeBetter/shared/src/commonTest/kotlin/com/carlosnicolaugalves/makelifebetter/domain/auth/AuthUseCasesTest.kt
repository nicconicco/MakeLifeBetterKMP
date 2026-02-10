package com.carlosnicolaugalves.makelifebetter.domain.auth

import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.model.User
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthUseCasesTest {
    @Test
    fun login_withBlankFields_returnsFailureWithoutCallingRepository() = runTest {
        val fakeRepository = FakeAuthRepository()
        val useCases = AuthUseCases(fakeRepository, AuthValidator())

        val result = useCases.login("", "")

        assertTrue(result.isFailure)
        assertFalse(fakeRepository.loginCalled)
        assertEquals("Preencha todos os campos", result.exceptionOrNull()?.message)
    }

    @Test
    fun login_withValidFields_callsRepository() = runTest {
        val fakeRepository = FakeAuthRepository().apply {
            loginResult = Result.success(sampleUser())
        }
        val useCases = AuthUseCases(fakeRepository, AuthValidator())

        val result = useCases.login("user", "password")

        assertTrue(fakeRepository.loginCalled)
        assertTrue(result.isSuccess)
    }

    private fun sampleUser(): User {
        return User(
            id = "1",
            username = "user",
            email = "user@example.com",
            passwordHash = "hash"
        )
    }

    private class FakeAuthRepository : AuthRepository {
        var loginCalled = false
        var loginResult: Result<User> = Result.failure(IllegalStateException("Not set"))

        override suspend fun login(username: String, password: String): Result<User> {
            loginCalled = true
            return loginResult
        }

        override suspend fun register(username: String, email: String, password: String): Result<User> {
            return Result.failure(IllegalStateException("Not implemented"))
        }

        override suspend fun recoverPassword(email: String): Result<String> {
            return Result.failure(IllegalStateException("Not implemented"))
        }

        override suspend fun getUserByEmail(email: String): User? {
            return null
        }

        override suspend fun getUserByUsername(username: String): User? {
            return null
        }

        override suspend fun updateProfile(userId: String, username: String, email: String): Result<User> {
            return Result.failure(IllegalStateException("Not implemented"))
        }

        override suspend fun changePassword(currentPassword: String, newPassword: String): Result<String> {
            return Result.failure(IllegalStateException("Not implemented"))
        }

        override suspend fun logout(): Result<Unit> {
            return Result.success(Unit)
        }
    }
}
