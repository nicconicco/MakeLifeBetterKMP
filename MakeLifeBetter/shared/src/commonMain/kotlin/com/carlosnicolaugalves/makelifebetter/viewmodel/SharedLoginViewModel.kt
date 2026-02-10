package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.auth.AuthResult
import com.carlosnicolaugalves.makelifebetter.auth.PasswordChangeResult
import com.carlosnicolaugalves.makelifebetter.auth.PasswordRecoveryResult
import com.carlosnicolaugalves.makelifebetter.auth.ProfileUpdateResult
import com.carlosnicolaugalves.makelifebetter.auth.RegisterResult
import com.carlosnicolaugalves.makelifebetter.di.provideAuthUseCases
import com.carlosnicolaugalves.makelifebetter.domain.auth.AuthUseCases
import com.carlosnicolaugalves.makelifebetter.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SharedLoginViewModel(
    private val authUseCases: AuthUseCases = provideAuthUseCases(),
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + dispatcher)

    // Estado de login
    private val _loginState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val loginState: StateFlow<AuthResult> = _loginState.asStateFlow()

    // Estado de registro
    private val _registerState = MutableStateFlow<RegisterResult>(RegisterResult.Idle)
    val registerState: StateFlow<RegisterResult> = _registerState.asStateFlow()

    // Estado de recuperação de senha
    private val _passwordRecoveryState = MutableStateFlow<PasswordRecoveryResult>(PasswordRecoveryResult.Idle)
    val passwordRecoveryState: StateFlow<PasswordRecoveryResult> = _passwordRecoveryState.asStateFlow()

    // Estado de atualização de perfil
    private val _profileUpdateState = MutableStateFlow<ProfileUpdateResult>(ProfileUpdateResult.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateResult> = _profileUpdateState.asStateFlow()

    // Estado de alteração de senha
    private val _passwordChangeState = MutableStateFlow<PasswordChangeResult>(PasswordChangeResult.Idle)
    val passwordChangeState: StateFlow<PasswordChangeResult> = _passwordChangeState.asStateFlow()

    // Usuário atual logado
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * Realiza login com usuário e senha
     */
    fun login(username: String, password: String) {
        // Reseta para Idle primeiro para garantir que a transição seja detectada
        _loginState.value = AuthResult.Idle

        authUseCases.validateLogin(username, password).onFailure { exception ->
            _loginState.value = AuthResult.Error(exception.message ?: "Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthResult.Loading

            authUseCases.login(username, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _loginState.value = AuthResult.Success(user)
                }
                .onFailure { exception ->
                    _loginState.value = AuthResult.Error(exception.message ?: "Erro ao fazer login")
                }
        }
    }

    /**
     * Registra um novo usuário
     */
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        // Reseta para Idle primeiro para garantir que a transição seja detectada
        _registerState.value = RegisterResult.Idle

        authUseCases.validateRegister(username, email, password, confirmPassword).onFailure { exception ->
            _registerState.value = RegisterResult.Error(exception.message ?: "Erro ao registrar")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterResult.Loading

            authUseCases.register(username, email, password, confirmPassword)
                .onSuccess {
                    _registerState.value = RegisterResult.Success
                }
                .onFailure { exception ->
                    _registerState.value = RegisterResult.Error(exception.message ?: "Erro ao registrar")
                }
        }
    }

    /**
     * Recupera senha enviando um link de redefinicao
     */
    fun recoverPassword(email: String) {
        // Reseta para Idle primeiro para garantir que a transição seja detectada
        _passwordRecoveryState.value = PasswordRecoveryResult.Idle

        authUseCases.validateRecoverPassword(email).onFailure { exception ->
            _passwordRecoveryState.value = PasswordRecoveryResult.Error(exception.message ?: "Informe o email")
            return
        }

        viewModelScope.launch {
            _passwordRecoveryState.value = PasswordRecoveryResult.Loading

            authUseCases.recoverPassword(email)
                .onSuccess { message ->
                    _passwordRecoveryState.value = PasswordRecoveryResult.Success(message)
                }
                .onFailure { exception ->
                    _passwordRecoveryState.value = PasswordRecoveryResult.Error(
                        exception.message ?: "Erro ao recuperar senha"
                    )
                }
        }
    }

    /**
     * Atualiza o perfil do usuário
     */
    fun updateProfile(username: String, email: String) {
        _profileUpdateState.value = ProfileUpdateResult.Idle

        val user = _currentUser.value
        if (user == null) {
            _profileUpdateState.value = ProfileUpdateResult.Error("Usuario nao logado")
            return
        }

        authUseCases.validateProfile(username, email).onFailure { exception ->
            _profileUpdateState.value = ProfileUpdateResult.Error(exception.message ?: "Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _profileUpdateState.value = ProfileUpdateResult.Loading

            authUseCases.updateProfile(user.id, username, email)
                .onSuccess { updatedUser ->
                    _currentUser.value = updatedUser
                    _profileUpdateState.value = ProfileUpdateResult.Success(updatedUser)
                }
                .onFailure { exception ->
                    _profileUpdateState.value = ProfileUpdateResult.Error(
                        exception.message ?: "Erro ao atualizar perfil"
                    )
                }
        }
    }

    /**
     * Reseta o estado de atualização de perfil para Idle
     */
    fun resetProfileUpdateState() {
        _profileUpdateState.value = ProfileUpdateResult.Idle
    }

    /**
     * Altera a senha do usuário
     */
    fun changePassword(currentPassword: String, newPassword: String, confirmNewPassword: String) {
        _passwordChangeState.value = PasswordChangeResult.Idle

        if (_currentUser.value == null) {
            _passwordChangeState.value = PasswordChangeResult.Error("Usuario nao logado")
            return
        }

        authUseCases.validateChangePassword(currentPassword, newPassword, confirmNewPassword).onFailure { exception ->
            _passwordChangeState.value = PasswordChangeResult.Error(
                exception.message ?: "Erro ao alterar senha"
            )
            return
        }

        viewModelScope.launch {
            _passwordChangeState.value = PasswordChangeResult.Loading

            authUseCases.changePassword(currentPassword, newPassword, confirmNewPassword)
                .onSuccess { message ->
                    _passwordChangeState.value = PasswordChangeResult.Success(message)
                }
                .onFailure { exception ->
                    _passwordChangeState.value = PasswordChangeResult.Error(
                        exception.message ?: "Erro ao alterar senha"
                    )
                }
        }
    }

    /**
     * Reseta o estado de alteração de senha para Idle
     */
    fun resetPasswordChangeState() {
        _passwordChangeState.value = PasswordChangeResult.Idle
    }

    /**
     * Realiza logout do usuário atual
     */
    fun logout() {
        viewModelScope.launch {
            authUseCases.logout()
            _currentUser.value = null
            _loginState.value = AuthResult.Idle
            _profileUpdateState.value = ProfileUpdateResult.Idle
            _passwordChangeState.value = PasswordChangeResult.Idle
        }
    }

    /**
     * Reseta o estado de login para Idle
     */
    fun resetLoginState() {
        _loginState.value = AuthResult.Idle
    }

    /**
     * Reseta o estado de registro para Idle
     */
    fun resetRegisterState() {
        _registerState.value = RegisterResult.Idle
    }

    /**
     * Reseta o estado de recuperação de senha para Idle
     */
    fun resetPasswordRecoveryState() {
        _passwordRecoveryState.value = PasswordRecoveryResult.Idle
    }

    /**
     * Verifica se há um usuário logado
     */
    fun isLoggedIn(): Boolean = _currentUser.value != null

    // ============ Métodos para iOS (com callbacks) ============

    /**
     * Observa mudanças no estado de login (para iOS)
     * @return Job que pode ser cancelado para parar de observar
     */
    fun observeLoginState(callback: (AuthResult) -> Unit): Job {
        return loginState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    /**
     * Observa mudanças no estado de registro (para iOS)
     * @return Job que pode ser cancelado para parar de observar
     */
    fun observeRegisterState(callback: (RegisterResult) -> Unit): Job {
        return registerState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    /**
     * Observa mudanças no estado de recuperação de senha (para iOS)
     * @return Job que pode ser cancelado para parar de observar
     */
    fun observePasswordRecoveryState(callback: (PasswordRecoveryResult) -> Unit): Job {
        return passwordRecoveryState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    /**
     * Observa mudanças no estado de atualização de perfil (para iOS)
     * @return Job que pode ser cancelado para parar de observar
     */
    fun observeProfileUpdateState(callback: (ProfileUpdateResult) -> Unit): Job {
        return profileUpdateState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    /**
     * Observa mudanças no estado de alteração de senha (para iOS)
     * @return Job que pode ser cancelado para parar de observar
     */
    fun observePasswordChangeState(callback: (PasswordChangeResult) -> Unit): Job {
        return passwordChangeState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    /**
     * Cancela todas as observações e limpa recursos
     */
    fun clear() {
        viewModelScope.launch {
            // Cancela o scope filho
        }
    }
}
