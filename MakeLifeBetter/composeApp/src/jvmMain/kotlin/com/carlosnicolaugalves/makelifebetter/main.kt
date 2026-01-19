package com.carlosnicolaugalves.makelifebetter

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel

fun main() = application {
    val loginViewModel = SharedLoginViewModel()
    Window(
        onCloseRequest = ::exitApplication,
        title = "MakeLifeBetter",
    ) {
        App(loginViewModel)
    }
}