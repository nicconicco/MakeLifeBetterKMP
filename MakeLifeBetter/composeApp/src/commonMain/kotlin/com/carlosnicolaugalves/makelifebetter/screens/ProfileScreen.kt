package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlosnicolaugalves.makelifebetter.auth.PasswordChangeResult
import com.carlosnicolaugalves.makelifebetter.auth.ProfileUpdateResult
import com.carlosnicolaugalves.makelifebetter.model.User

private const val SECRET_PASSWORD = "0000"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    currentUser: User?,
    profileUpdateState: ProfileUpdateResult,
    passwordChangeState: PasswordChangeResult,
    onSaveClick: (username: String, email: String) -> Unit,
    onChangePasswordClick: (currentPassword: String, newPassword: String, confirmPassword: String) -> Unit,
    onLogoutClick: () -> Unit,
    onSecretAccessGranted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var username by remember(currentUser) { mutableStateOf(currentUser?.username ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var showProfileSuccess by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var showPasswordSuccess by remember { mutableStateOf(false) }

    // Secret access dialog state
    var showSecretDialog by remember { mutableStateOf(false) }
    var secretPassword by remember { mutableStateOf("") }
    var secretPasswordError by remember { mutableStateOf(false) }

    // Secret password dialog
    if (showSecretDialog) {
        AlertDialog(
            onDismissRequest = {
                showSecretDialog = false
                secretPassword = ""
                secretPasswordError = false
            },
            title = {
                Text(
                    text = "Acesso Restrito",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Digite a senha para continuar:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = secretPassword,
                        onValueChange = {
                            secretPassword = it
                            secretPasswordError = false
                        },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        isError = secretPasswordError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (secretPasswordError) {
                        Text(
                            text = "Senha incorreta",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (secretPassword == SECRET_PASSWORD) {
                            showSecretDialog = false
                            secretPassword = ""
                            secretPasswordError = false
                            onSecretAccessGranted()
                        } else {
                            secretPasswordError = true
                        }
                    }
                ) {
                    Text("Entrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSecretDialog = false
                        secretPassword = ""
                        secretPasswordError = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(profileUpdateState) {
        if (profileUpdateState is ProfileUpdateResult.Success) {
            showProfileSuccess = true
        }
    }

    LaunchedEffect(passwordChangeState) {
        if (passwordChangeState is PasswordChangeResult.Success) {
            showPasswordSuccess = true
            currentPassword = ""
            newPassword = ""
            confirmNewPassword = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Avatar com long press para acesso secreto
        Surface(
            modifier = Modifier
                .size(100.dp)
                .combinedClickable(
                    onClick = { },
                    onLongClick = { showSecretDialog = true }
                ),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = currentUser?.username?.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }


        Text(
            text = "Meu Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Profile Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Informacoes pessoais",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nome de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = profileUpdateState !is ProfileUpdateResult.Loading
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = profileUpdateState !is ProfileUpdateResult.Loading
                )

                if (profileUpdateState is ProfileUpdateResult.Error) {
                    Text(
                        text = profileUpdateState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (showProfileSuccess) {
                    Text(
                        text = "Perfil atualizado com sucesso!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        showProfileSuccess = false
                        onSaveClick(username, email)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = profileUpdateState !is ProfileUpdateResult.Loading
                ) {
                    if (profileUpdateState is ProfileUpdateResult.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Salvar perfil")
                    }
                }
            }
        }

        // Password Change Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Alterar senha",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Senha atual") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = passwordChangeState !is PasswordChangeResult.Loading
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nova senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = passwordChangeState !is PasswordChangeResult.Loading
                )

                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    label = { Text("Confirmar nova senha") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = passwordChangeState !is PasswordChangeResult.Loading
                )

                if (passwordChangeState is PasswordChangeResult.Error) {
                    Text(
                        text = passwordChangeState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (showPasswordSuccess) {
                    Text(
                        text = "Senha alterada com sucesso!",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedButton(
                    onClick = {
                        showPasswordSuccess = false
                        onChangePasswordClick(currentPassword, newPassword, confirmNewPassword)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = passwordChangeState !is PasswordChangeResult.Loading
                ) {
                    if (passwordChangeState is PasswordChangeResult.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Alterar senha")
                    }
                }
            }
        }

        // ID info (read-only)
        if (currentUser != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ID da conta",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currentUser.id,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Logout button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Sair da conta")
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {

    val user = User("1", "Usuario Test", "email@gmail.com", "123456")
    ProfileScreen(
        currentUser = user,
        ProfileUpdateResult.Success(user),
        PasswordChangeResult.Success("ok"),
        onSaveClick = { _, _ -> },
        onChangePasswordClick = { _, _, _ -> },
        onLogoutClick = {}
    )
}
