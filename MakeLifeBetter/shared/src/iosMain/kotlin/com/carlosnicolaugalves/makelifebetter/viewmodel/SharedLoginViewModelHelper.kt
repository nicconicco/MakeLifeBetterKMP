package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.auth.AuthResult
import com.carlosnicolaugalves.makelifebetter.auth.PasswordChangeResult
import com.carlosnicolaugalves.makelifebetter.auth.PasswordRecoveryResult
import com.carlosnicolaugalves.makelifebetter.auth.ProfileUpdateResult
import com.carlosnicolaugalves.makelifebetter.auth.RegisterResult
import com.carlosnicolaugalves.makelifebetter.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Factory function to create SharedLoginViewModel for iOS
 */
fun createSharedLoginViewModel(): SharedLoginViewModel = SharedLoginViewModel()

/**
 * Helper class para facilitar o uso do SharedLoginViewModel no iOS/Swift
 */
class SharedLoginViewModelWrapper {
    private val viewModel = SharedLoginViewModel()

    // Observadores ativos
    private var loginObserver: Job? = null
    private var registerObserver: Job? = null
    private var passwordRecoveryObserver: Job? = null
    private var profileUpdateObserver: Job? = null
    private var passwordChangeObserver: Job? = null
    private var currentUserObserver: Job? = null

    // MARK: - Login

    fun login(username: String, password: String) {
        viewModel.login(username, password)
    }

    fun observeLoginState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        loginObserver?.cancel()
        loginObserver = viewModel.observeLoginState { result ->
            when (result) {
                is AuthResult.Idle -> onIdle()
                is AuthResult.Loading -> onLoading()
                is AuthResult.Success -> onSuccess(result.user)
                is AuthResult.Error -> onError(result.message)
            }
        }
    }

    fun resetLoginState() {
        viewModel.resetLoginState()
    }

    // MARK: - Register

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        viewModel.register(username, email, password, confirmPassword)
    }

    fun observeRegisterState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        registerObserver?.cancel()
        registerObserver = viewModel.observeRegisterState { result ->
            when (result) {
                is RegisterResult.Idle -> onIdle()
                is RegisterResult.Loading -> onLoading()
                is RegisterResult.Success -> onSuccess()
                is RegisterResult.Error -> onError(result.message)
            }
        }
    }

    fun resetRegisterState() {
        viewModel.resetRegisterState()
    }

    // MARK: - Password Recovery

    fun recoverPassword(email: String) {
        viewModel.recoverPassword(email)
    }

    fun observePasswordRecoveryState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        passwordRecoveryObserver?.cancel()
        passwordRecoveryObserver = viewModel.observePasswordRecoveryState { result ->
            when (result) {
                is PasswordRecoveryResult.Idle -> onIdle()
                is PasswordRecoveryResult.Loading -> onLoading()
                is PasswordRecoveryResult.Success -> onSuccess(result.message)
                is PasswordRecoveryResult.Error -> onError(result.message)
            }
        }
    }

    fun resetPasswordRecoveryState() {
        viewModel.resetPasswordRecoveryState()
    }

    // MARK: - Profile Update

    fun updateProfile(username: String, email: String) {
        viewModel.updateProfile(username, email)
    }

    fun observeProfileUpdateState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        profileUpdateObserver?.cancel()
        profileUpdateObserver = viewModel.observeProfileUpdateState { result ->
            when (result) {
                is ProfileUpdateResult.Idle -> onIdle()
                is ProfileUpdateResult.Loading -> onLoading()
                is ProfileUpdateResult.Success -> onSuccess(result.user)
                is ProfileUpdateResult.Error -> onError(result.message)
            }
        }
    }

    fun resetProfileUpdateState() {
        viewModel.resetProfileUpdateState()
    }

    // MARK: - Password Change

    fun changePassword(currentPassword: String, newPassword: String, confirmNewPassword: String) {
        viewModel.changePassword(currentPassword, newPassword, confirmNewPassword)
    }

    fun observePasswordChangeState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        passwordChangeObserver?.cancel()
        passwordChangeObserver = viewModel.observePasswordChangeState { result ->
            when (result) {
                is PasswordChangeResult.Idle -> onIdle()
                is PasswordChangeResult.Loading -> onLoading()
                is PasswordChangeResult.Success -> onSuccess(result.message)
                is PasswordChangeResult.Error -> onError(result.message)
            }
        }
    }

    fun resetPasswordChangeState() {
        viewModel.resetPasswordChangeState()
    }

    // MARK: - Current User

    fun observeCurrentUser(callback: (User?) -> Unit) {
        currentUserObserver?.cancel()
        currentUserObserver = viewModel.currentUser
            .onEach { callback(it) }
            .launchIn(CoroutineScope(SupervisorJob() + Dispatchers.Main))
    }

    fun getCurrentUser(): User? = viewModel.currentUser.value

    fun isLoggedIn(): Boolean = viewModel.isLoggedIn()

    // MARK: - Logout

    fun logout() {
        viewModel.logout()
    }

    // MARK: - Cleanup

    fun clear() {
        loginObserver?.cancel()
        registerObserver?.cancel()
        passwordRecoveryObserver?.cancel()
        profileUpdateObserver?.cancel()
        passwordChangeObserver?.cancel()
        currentUserObserver?.cancel()
        viewModel.clear()
    }
}
