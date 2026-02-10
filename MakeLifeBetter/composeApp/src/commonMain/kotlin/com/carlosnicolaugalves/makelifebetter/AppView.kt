package com.carlosnicolaugalves.makelifebetter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.auth.AuthResult
import com.carlosnicolaugalves.makelifebetter.auth.PasswordRecoveryResult
import com.carlosnicolaugalves.makelifebetter.auth.RegisterResult
import com.carlosnicolaugalves.makelifebetter.navigation.Screen
import com.carlosnicolaugalves.makelifebetter.repository.createRemoteConfigRepository
import com.carlosnicolaugalves.makelifebetter.screens.login.ErrorDialog
import com.carlosnicolaugalves.makelifebetter.screens.login.ForgotPasswordScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.LanguageScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.LoginScreen
import com.carlosnicolaugalves.makelifebetter.screens.MainScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.RegisterFormData
import com.carlosnicolaugalves.makelifebetter.screens.login.RegisterScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.TempPasswordScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.TermsScreen
import com.carlosnicolaugalves.makelifebetter.util.Language
import com.carlosnicolaugalves.makelifebetter.util.Translations
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel
import makelifebetter.composeapp.generated.resources.Res
import makelifebetter.composeapp.generated.resources.ic_launcher_final_round
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun AppView(viewModel: SharedLoginViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.Loading) }
    var termsAccepted by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(Language.PORTUGUESE) }
    var registerFormData by remember { mutableStateOf(RegisterFormData()) }
    var isLoginRequired by remember { mutableStateOf(true) }
    var validAccessCode by remember { mutableStateOf("") }

    val remoteConfigRepository = remember { createRemoteConfigRepository() }

    val loginState by viewModel.loginState.collectAsState()
    val registerState by viewModel.registerState.collectAsState()
    val passwordRecoveryState by viewModel.passwordRecoveryState.collectAsState()

    // Busca configuracao do Remote Config ao iniciar
    LaunchedEffect(Unit) {
        remoteConfigRepository.fetchAndActivate()
        isLoginRequired = remoteConfigRepository.isLoginRequired()
        validAccessCode = remoteConfigRepository.getAccessCode()
        currentScreen = if (isLoginRequired) Screen.Login else Screen.Home
    }

    val strings = Translations.getStrings(currentLanguage)

    when(registerState) {
        is RegisterResult.Success -> {
            currentScreen = Screen.Login
        }
        is RegisterResult.Error -> {
            ErrorDialog(
                title = strings.registerError,
                message = (registerState as RegisterResult.Error).message + "\n\n" + strings.errorMessage,
                buttonText = strings.tryAgain,
                onDismiss = { viewModel.resetRegisterState() }
            )
        }
        else -> {}
    }

    when (loginState) {
        is AuthResult.Success -> {
            currentScreen = Screen.Home
        }
        is AuthResult.Error -> {
            ErrorDialog(
                title = strings.loginError,
                message = (loginState as AuthResult.Error).message + "\n\n" + strings.errorMessage,
                buttonText = strings.tryAgain,
                onDismiss = { viewModel.resetLoginState() }
            )
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
                Screen.Loading -> {
                    LoadingScreen()
                }
                Screen.Login -> {
                    LoginScreen(
                        strings = strings,
                        language = currentLanguage,
                        validAccessCode = validAccessCode,
                        onLoginClick = { username, password ->
                            viewModel.login(username, password)
                        },
                        onForgotPasswordClick = {
                            currentScreen = Screen.ForgotPassword
                        },
                        onCreateAccountClick = {
                            termsAccepted = false
                            registerFormData = RegisterFormData()
                            currentScreen = Screen.Register
                        },
                        onLanguageClick = {
                            currentScreen = Screen.Language
                        }
                    )
                }
                Screen.Home -> {
                    MainScreen(
                        viewModel = viewModel,
                        strings = strings,
                        isLoginRequired = isLoginRequired,
                        onLoginClick = {
                            currentScreen = Screen.Login
                        },
                        onLogout = {
                            currentScreen = if (isLoginRequired) Screen.Login else Screen.Home
                        }
                    )
                }
                Screen.Register -> {
                    RegisterScreen(
                        strings = strings,
                        termsAccepted = termsAccepted,
                        registerState = registerState,
                        initialFormData = registerFormData,
                        requiredAccessCode = validAccessCode,
                        onRegisterClick = { username, email, password ->
                            viewModel.register(username = username, email = email, password = password, confirmPassword = password)
                        },
                        onBackClick = {
                            viewModel.resetRegisterState()
                            registerFormData = RegisterFormData()
                            termsAccepted = false
                            currentScreen = Screen.Login
                        },
                        onTermsClick = { formData ->
                            registerFormData = formData
                            currentScreen = Screen.Terms
                        },
                        onTermsCheckedChange = { checked ->
                            termsAccepted = checked
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
                        requiredAccessCode = validAccessCode,
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

                else -> {}
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_launcher_final_round),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}
