package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.auth.AuthResult
import com.carlosnicolaugalves.makelifebetter.domain.auth.AuthUseCases
import com.carlosnicolaugalves.makelifebetter.domain.auth.AuthValidator
import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.model.User
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedLoginViewModelTest {
    @Test
    fun login_withBlankFields_setsErrorState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = SharedLoginViewModel(
            authUseCases = AuthUseCases(FakeAuthRepository(), AuthValidator()),
            dispatcher = dispatcher
        )

        viewModel.login("", "")

        val state = viewModel.loginState.value
        assertTrue(state is AuthResult.Error)
        assertEquals("Preencha todos os campos", state.message)
    }

    @Test
    fun login_withValidFields_setsSuccessState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val user = User(
            id = "1",
            username = "user",
            email = "user@example.com",
            passwordHash = "hash"
        )
        val repository = FakeAuthRepository().apply {
            loginResult = Result.success(user)
        }
        val viewModel = SharedLoginViewModel(
            authUseCases = AuthUseCases(repository, AuthValidator()),
            dispatcher = dispatcher
        )

        viewModel.login("user", "password")
        advanceUntilIdle()

        val state = viewModel.loginState.value
        assertTrue(state is AuthResult.Success)
        assertEquals(user, state.user)
    }

    private class FakeAuthRepository : AuthRepository {
        var loginResult: Result<User> = Result.failure(IllegalStateException("Not set"))

        override suspend fun login(username: String, password: String): Result<User> {
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
