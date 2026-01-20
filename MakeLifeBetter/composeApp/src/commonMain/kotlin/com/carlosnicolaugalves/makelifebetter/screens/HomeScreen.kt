package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.event.EventSectionsResult
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.navigation.NavigationItem
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedEventViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel

@Composable
fun MainScreen(
    viewModel: SharedLoginViewModel,
    eventViewModel: SharedEventViewModel = remember { SharedEventViewModel() },
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf(NavigationItem.EVENTO) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    val currentUser by viewModel.currentUser.collectAsState()
    val profileUpdateState by viewModel.profileUpdateState.collectAsState()
    val passwordChangeState by viewModel.passwordChangeState.collectAsState()

    val eventSections by eventViewModel.eventSections.collectAsState()
    val sectionsState by eventViewModel.sectionsState.collectAsState()

    val isLoading = sectionsState is EventSectionsResult.Loading

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
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
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
                    }
                )
                NavigationItem.CHAT -> PlaceholderScreen("Chat")
                NavigationItem.NOTIFICACOES -> NotificationScreen()
                NavigationItem.CONTRATE -> HireMeScreen()
            }
        }
    }
}