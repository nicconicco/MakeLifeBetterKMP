package com.carlosnicolaugalves.makelifebetter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.carlosnicolaugalves.makelifebetter.screens.ForgotPasswordScreen
import com.carlosnicolaugalves.makelifebetter.screens.LanguageScreen
import com.carlosnicolaugalves.makelifebetter.screens.LoginScreen
import com.carlosnicolaugalves.makelifebetter.screens.RegisterScreen
import com.carlosnicolaugalves.makelifebetter.screens.TempPasswordScreen
import com.carlosnicolaugalves.makelifebetter.screens.TermsScreen
import com.carlosnicolaugalves.makelifebetter.util.Language
import com.carlosnicolaugalves.makelifebetter.util.Translations
import com.carlosnicolaugalves.makelifebetter.viewmodel.LoginViewModel

enum class Screen {
    Login,
    Register,
    Terms,
    ForgotPassword,
    TempPassword,
    Language
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Login) }
        var termsAccepted by remember { mutableStateOf(false) }
        var currentLanguage by remember { mutableStateOf(Language.PORTUGUESE) }

        val strings = Translations.getStrings(currentLanguage)

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (currentScreen) {
                Screen.Login -> {
                    LoginScreen(
                        strings = strings,
                        language = currentLanguage,
                        onLoginClick = { username, password ->
                            // TODO: Implement login logic
                        },
                        onForgotPasswordClick = {
                            currentScreen = Screen.ForgotPassword
                        },
                        onCreateAccountClick = {
                            termsAccepted = false
                            currentScreen = Screen.Register
                        },
                        onLanguageClick = {
                            currentScreen = Screen.Language
                        }
                    )
                }
                Screen.Register -> {
                    RegisterScreen(
                        strings = strings,
                        termsAccepted = termsAccepted,
                        onRegisterClick = {
                            currentScreen = Screen.Login
                        },
                        onBackClick = {
                            currentScreen = Screen.Login
                        },
                        onTermsClick = {
                            currentScreen = Screen.Terms
                        }
                    )
                }
                Screen.Terms -> {
                    TermsScreen(
                        strings = strings,
                        onAgreeClick = {
                            termsAccepted = true
                            currentScreen = Screen.Register
                        }
                    )
                }
                Screen.ForgotPassword -> {
                    ForgotPasswordScreen(
                        strings = strings,
                        onConfirmClick = {
                            currentScreen = Screen.TempPassword
                        },
                        onBackClick = {
                            currentScreen = Screen.Login
                        }
                    )
                }
                Screen.TempPassword -> {
                    TempPasswordScreen(
                        strings = strings,
                        onConfirmClick = {
                            currentScreen = Screen.Login
                        }
                    )
                }
                Screen.Language -> {
                    LanguageScreen(
                        strings = strings,
                        onLanguageSelected = { language ->
                            currentLanguage = language
                            currentScreen = Screen.Login
                        },
                        onBackClick = {
                            currentScreen = Screen.Login
                        }
                    )
                }
            }
        }
    }
}
