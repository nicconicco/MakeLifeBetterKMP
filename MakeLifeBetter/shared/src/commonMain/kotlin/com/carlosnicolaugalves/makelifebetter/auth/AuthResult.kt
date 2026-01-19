package com.carlosnicolaugalves.makelifebetter.auth

import com.carlosnicolaugalves.makelifebetter.model.User

sealed class AuthResult {
    data object Idle : AuthResult()
    data object Loading : AuthResult()
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class RegisterResult {
    data object Idle : RegisterResult()
    data object Loading : RegisterResult()
    data object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

sealed class PasswordRecoveryResult {
    data object Idle : PasswordRecoveryResult()
    data object Loading : PasswordRecoveryResult()
    /**
     * @param message Para LocalAuth: senha temporária gerada
     *                Para Firebase: mensagem de confirmação do envio do email
     */
    data class Success(val message: String) : PasswordRecoveryResult()
    data class Error(val message: String) : PasswordRecoveryResult()
}
