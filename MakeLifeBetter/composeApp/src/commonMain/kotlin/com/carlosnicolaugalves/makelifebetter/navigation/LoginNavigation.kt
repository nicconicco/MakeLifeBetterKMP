package com.carlosnicolaugalves.makelifebetter.navigation

import androidx.compose.runtime.Composable
import com.carlosnicolaugalves.makelifebetter.screens.LoginScreen
import com.carlosnicolaugalves.makelifebetter.util.AppStrings
import com.carlosnicolaugalves.makelifebetter.util.Language
import com.carlosnicolaugalves.makelifebetter.viewmodel.LoginViewModel

@Composable
fun LoginNavigation(
//    loginViewModel: LoginViewModel,
    strings: AppStrings,
    currentLanguage: Language,
    currentScreen: Screen,
    termsAccepted: Boolean
): Pair<Screen, Boolean> {
    var currentScreen1 = currentScreen
    var termsAccepted1 = termsAccepted
    LoginScreen(
        strings = strings,
        language = currentLanguage,
        onLoginClick = { username, password ->
//            loginViewModel.login(username, password)
        },
        onForgotPasswordClick = {
            currentScreen1 = Screen.ForgotPassword
        },
        onCreateAccountClick = {
            termsAccepted1 = false
            currentScreen1 = Screen.Register
        },
        onLanguageClick = {
            currentScreen1 = Screen.Language
        }
    )
    return Pair(currentScreen1, termsAccepted1)
}