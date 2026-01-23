package com.carlosnicolaugalves.makelifebetter

import androidx.compose.ui.window.ComposeUIViewController
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel

fun MainViewController() = ComposeUIViewController {
    val loginViewModel = SharedLoginViewModel()
    AppView(loginViewModel)
}