package com.carlosnicolaugalves.makelifebetter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.carlosnicolaugalves.makelifebetter.auth.AuthResult
import com.carlosnicolaugalves.makelifebetter.auth.PasswordRecoveryResult
import com.carlosnicolaugalves.makelifebetter.auth.RegisterResult
import com.carlosnicolaugalves.makelifebetter.navigation.Screen
import com.carlosnicolaugalves.makelifebetter.screens.ForgotPasswordScreen
import com.carlosnicolaugalves.makelifebetter.screens.LanguageScreen
import com.carlosnicolaugalves.makelifebetter.screens.LoginScreen
import com.carlosnicolaugalves.makelifebetter.screens.MainScreen
import com.carlosnicolaugalves.makelifebetter.screens.RegisterScreen
import com.carlosnicolaugalves.makelifebetter.screens.SectionedListScreen
import com.carlosnicolaugalves.makelifebetter.screens.TempPasswordScreen
import com.carlosnicolaugalves.makelifebetter.screens.TermsScreen
import com.carlosnicolaugalves.makelifebetter.screens.getSampleSections
import com.carlosnicolaugalves.makelifebetter.util.Language
import com.carlosnicolaugalves.makelifebetter.util.Translations
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel

@Composable
@Preview
fun App(viewModel: SharedLoginViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.Login) }
    var termsAccepted by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(Language.PORTUGUESE) }


    val loginState by viewModel.loginState.collectAsState()
    val registerState by viewModel.registerState.collectAsState()
    val passwordRecoveryState by viewModel.passwordRecoveryState.collectAsState()

    when(registerState) {
        is RegisterResult.Success -> {
            currentScreen = Screen.Home
        }
        is RegisterResult.Error -> {

        }
        else -> {}
    }

    when (loginState) {
        is AuthResult.Success -> {
            currentScreen = Screen.Home
        }
        is AuthResult.Error -> {

        }
        else -> {}
    }

    MaterialTheme {
        val strings = Translations.getStrings(currentLanguage)

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (currentScreen) {
                Screen.Login -> {
                    LoginScreen(
                        strings = strings,
                        language = currentLanguage,
                        onLoginClick = { username, password ->
                            viewModel.login(username, password)
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
                Screen.SectionedListScreen -> {
                    SectionedListScreen(
                        sections = getSampleSections()
                    )
                }
                Screen.Home -> {
                    MainScreen(
                        viewModel = viewModel,
                        onLogout = {
                            currentScreen = Screen.Login
                        }
                    )
                }
                Screen.Register -> {
                    RegisterScreen(
                        strings = strings,
                        termsAccepted = termsAccepted,
                        registerState = registerState,
                        onRegisterClick = { username, email, password ->
                            viewModel.register(username = username, email = email, password = password, confirmPassword = password)
                        },
                        onBackClick = {
                            viewModel.resetRegisterState()
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
                        passwordRecoveryState = passwordRecoveryState,
                        onConfirmClick = { email ->
                            viewModel.recoverPassword(email)
                        },
                        onBackClick = {
                            viewModel.resetPasswordRecoveryState()
                            currentScreen = Screen.Login
                        },
                        onSuccess = {
                            currentScreen = Screen.TempPassword
                        }
                    )
                }
                Screen.TempPassword -> {
                    val message = (passwordRecoveryState as? PasswordRecoveryResult.Success)?.message ?: ""
                    TempPasswordScreen(
                        strings = strings,
                        successMessage = message,
                        onConfirmClick = {
                            viewModel.resetPasswordRecoveryState()
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
