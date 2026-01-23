package com.carlosnicolaugalves.makelifebetter

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val loginViewModel = SharedLoginViewModel()
    ComposeViewport {
        AppView(loginViewModel)
    }
}