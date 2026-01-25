package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.carlosnicolaugalves.makelifebetter.components.NotificationPermissionHandler
import com.carlosnicolaugalves.makelifebetter.event.EventSectionsResult
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.navigation.NavigationItem
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedChatViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedEventViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedNotificationViewModel

@Composable
fun MainScreen(
    viewModel: SharedLoginViewModel,
    eventViewModel: SharedEventViewModel = remember { SharedEventViewModel() },
    notificationViewModel: SharedNotificationViewModel = remember { SharedNotificationViewModel() },
    chatViewModel: SharedChatViewModel = remember { SharedChatViewModel() },
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf(NavigationItem.EVENTO) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showSecretScreen by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val profileUpdateState by viewModel.profileUpdateState.collectAsState()
    val passwordChangeState by viewModel.passwordChangeState.collectAsState()

    val eventSections by eventViewModel.eventSections.collectAsState()
    val sectionsState by eventViewModel.sectionsState.collectAsState()
    val shouldRequestPermission by notificationViewModel.shouldRequestPermission.collectAsState()

    val isLoading = sectionsState is EventSectionsResult.Loading

    // Handle notification permission request
    NotificationPermissionHandler(
        shouldRequest = shouldRequestPermission,
        onPermissionResult = { granted ->
            notificationViewModel.onPermissionResult(granted)
        },
        onRequestHandled = {
            notificationViewModel.onPermissionRequestHandled()
        }
    )

    // Schedule notifications when events are loaded
    LaunchedEffect(sectionsState) {
        if (sectionsState is EventSectionsResult.Success) {
            val allEvents = eventSections.flatMap { it.eventos }
            notificationViewModel.scheduleNotificationsForEvents(allEvents)
        }
    }

    // Se tiver a tela secreta ativa, mostra ela
    if (showSecretScreen) {
        SecretScreen(
            onBackClick = { showSecretScreen = false }
        )
        return
    }

    // Se tiver um evento selecionado, mostra a tela de detalhes
    if (selectedEvent != null) {
        EventDetailScreen(
            event = selectedEvent!!,
            onBackClick = { selectedEvent = null }
        )
        return
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedItem) {
                NavigationItem.EVENTO -> SectionedListScreen(
                    sections = eventSections,
                    isLoading = isLoading,
                    onItemClick = { event ->
                        selectedEvent = event
                    }
                )
                NavigationItem.MAPA -> MapScreen()
                NavigationItem.PERFIL -> ProfileScreen(
                    currentUser = currentUser,
                    profileUpdateState = profileUpdateState,
                    passwordChangeState = passwordChangeState,
                    onSaveClick = { username, email ->
                        viewModel.updateProfile(username, email)
                    },
                    onChangePasswordClick = { current, new, confirm ->
                        viewModel.changePassword(current, new, confirm)
                    },
                    onLogoutClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    onSecretAccessGranted = {
                        showSecretScreen = true
                    }
                )
                NavigationItem.CHAT -> ChatScreen(
                    currentUsername = currentUser?.username ?: "Usuario",
                    chatViewModel = chatViewModel
                )
                NavigationItem.NOTIFICACOES -> NotificationScreen(viewModel = notificationViewModel)
                NavigationItem.CONTRATE -> HireMeScreen()
            }
        }
    }
}