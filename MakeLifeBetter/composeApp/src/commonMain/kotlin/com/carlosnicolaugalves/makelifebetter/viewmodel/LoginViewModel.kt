package com.carlosnicolaugalves.makelifebetter.viewmodel// src/commonMain/kotlin/com/seuapp/LoginViewModel.kt

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun login(username: String, password: String) {
        // Aqui você pode implementar a lógica de autenticação (ex: chamar uma API)
        if (username == "admin" && password == "password") {
            _loginState.value = LoginState.Success
        } else {
            _loginState.value = LoginState.Error("Usuário ou senha inválidos.")
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
}