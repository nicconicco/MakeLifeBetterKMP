package com.carlosnicolaugalves.makelifebetter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass

class LoginViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass.isInstance(LoginViewModel::class)) {
            return LoginViewModel() as T // Adicione parâmetros se necessário
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}