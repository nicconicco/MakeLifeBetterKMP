package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.navigation.NavigationItem
import com.carlosnicolaugalves.makelifebetter.repository.createRemoteConfigRepository
import com.carlosnicolaugalves.makelifebetter.screens.event.EventDetailScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.EventListScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.GuestProfileScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.ProfileScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.SecretScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.CartScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.CheckoutScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.MyOrdersScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.OrderConfirmationScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.ProductDetailScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.StoreScreen
import com.carlosnicolaugalves.makelifebetter.screens.more.HireMeScreen
import com.carlosnicolaugalves.makelifebetter.screens.more.NotificationScreen
import com.carlosnicolaugalves.makelifebetter.util.PlatformBackHandler
import com.carlosnicolaugalves.makelifebetter.viewmodel.MapViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedChatViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedEventViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedNotificationViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedStoreViewModel

// Store screen states
private enum class StoreScreenState {
    LIST,
    DETAIL,
    CART,
    CHECKOUT,
    ORDER_CONFIRMATION
}

// More screen sub-navigation states
private enum class MoreSubScreen {
    MENU,
    PROFILE,
    ALARMS,
    CONTACT,
    MY_ORDERS
}

@Composable
fun MainScreen(
    viewModel: SharedLoginViewModel,
    eventViewModel: SharedEventViewModel = remember { SharedEventViewModel() },
    notificationViewModel: SharedNotificationViewModel = remember { SharedNotificationViewModel() },
    chatViewModel: SharedChatViewModel = remember { SharedChatViewModel() },
    storeViewModel: SharedStoreViewModel = remember { SharedStoreViewModel() },
    mapViewModel: MapViewModel = remember { MapViewModel() },
    isLoginRequired: Boolean = true,
    onLoginClick: () -> Unit = {},
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf(NavigationItem.EVENTO) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showSecretScreen by remember { mutableStateOf(false) }

    // Store navigation states
    var storeScreenState by remember { mutableStateOf(StoreScreenState.LIST) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    // More screen navigation state
    var moreSubScreen by remember { mutableStateOf(MoreSubScreen.MENU) }

    val currentUser by viewModel.currentUser.collectAsState()
    val profileUpdateState by viewModel.profileUpdateState.collectAsState()
    val passwordChangeState by viewModel.passwordChangeState.collectAsState()

    val eventSections by eventViewModel.eventSections.collectAsState()
    val sectionsState by eventViewModel.sectionsState.collectAsState()
    val shouldRequestPermission by notificationViewModel.shouldRequestPermission.collectAsState()

    val isLoading = sectionsState is EventSectionsResult.Loading

    val remoteConfigRepository = remember { createRemoteConfigRepository() }
    var isLoginRequired by remember { mutableStateOf(true) }

    // Handle system back button - navigate within app instead of going to background
    PlatformBackHandler(enabled = true) {
        when {
            // If showing secret screen, go back to normal
            showSecretScreen -> {
                showSecretScreen = false
                eventViewModel.refreshSections()
            }
            // If showing event detail, go back to list
            selectedEvent != null -> {
                selectedEvent = null
            }
            // If in store sub-screen, navigate back in store hierarchy
            selectedItem == NavigationItem.LOJA && storeScreenState != StoreScreenState.LIST -> {
                when (storeScreenState) {
                    StoreScreenState.DETAIL -> {
                        selectedProduct = null
                        storeScreenState = StoreScreenState.LIST
                    }
                    StoreScreenState.CART -> {
                        storeScreenState = StoreScreenState.LIST
                    }
                    StoreScreenState.CHECKOUT -> {
                        storeScreenState = StoreScreenState.CART
                    }
                    StoreScreenState.ORDER_CONFIRMATION -> {
                        storeScreenState = StoreScreenState.LIST
                    }
                    else -> {}
                }
            }
            // If in more sub-screen, navigate back in more hierarchy
            selectedItem == NavigationItem.MORE && moreSubScreen != MoreSubScreen.MENU -> {
                when (moreSubScreen) {
                    MoreSubScreen.MY_ORDERS -> {
                        moreSubScreen = MoreSubScreen.PROFILE
                    }
                    else -> {
                        moreSubScreen = MoreSubScreen.MENU
                    }
                }
            }
            // If on a non-home tab, go back to home tab (EVENTO)
            selectedItem != NavigationItem.EVENTO -> {
                // Reset sub-navigation states when going back
                if (selectedItem == NavigationItem.LOJA) {
                    storeScreenState = StoreScreenState.LIST
                    selectedProduct = null
                }
                if (selectedItem == NavigationItem.MORE) {
                    moreSubScreen = MoreSubScreen.MENU
                }
                selectedItem = NavigationItem.EVENTO
            }
            // On home tab (EVENTO), do nothing - stay on screen, don't go to background
        }
    }

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

    // Busca configuracao do Remote Config ao iniciar
    LaunchedEffect(Unit) {
        isLoginRequired = remoteConfigRepository.isLoginRequired()
    }

    // Se tiver a tela secreta ativa, mostra ela
    if (showSecretScreen) {
        SecretScreen(
            onBackClick = {
                showSecretScreen = false
                // Recarregar eventos ao voltar da tela secreta
                eventViewModel.refreshSections()
            }
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

    // Store screen navigation
    if (selectedItem == NavigationItem.LOJA && storeScreenState != StoreScreenState.LIST) {
        when (storeScreenState) {
            StoreScreenState.DETAIL -> {
                selectedProduct?.let { product ->
                    val allProducts by storeViewModel.products.collectAsState()
                    ProductDetailScreen(
                        product = product,
                        onBackClick = {
                            selectedProduct = null
                            storeScreenState = StoreScreenState.LIST
                        },
                        onAddToCart = { prod, qty ->
                            storeViewModel.addToCart(prod, qty)
                            selectedProduct = null
                            storeScreenState = StoreScreenState.LIST
                        },
                        allProducts = allProducts,
                        onProductClick = { clickedProduct ->
                            selectedProduct = clickedProduct
                        }
                    )
                }
            }

            StoreScreenState.CART -> {
                CartScreen(
                    viewModel = storeViewModel,
                    onBackClick = {
                        storeScreenState = StoreScreenState.LIST
                    },
                    onCheckout = {
                        storeScreenState = StoreScreenState.CHECKOUT
                    }
                )
            }

            StoreScreenState.CHECKOUT -> {
                CheckoutScreen(
                    viewModel = storeViewModel,
                    onBackClick = {
                        storeScreenState = StoreScreenState.CART
                    },
                    onConfirmOrder = {
                        storeScreenState = StoreScreenState.ORDER_CONFIRMATION
                    }
                )
            }

            StoreScreenState.ORDER_CONFIRMATION -> {
                OrderConfirmationScreen(
                    viewModel = storeViewModel,
                    onBackToStore = {
                        storeScreenState = StoreScreenState.LIST
                    }
                )
            }

            else -> {}
        }
        return
    }

    // More screen sub-navigation
    if (selectedItem == NavigationItem.MORE && moreSubScreen != MoreSubScreen.MENU) {
        when (moreSubScreen) {
            MoreSubScreen.PROFILE -> {
                if (currentUser == null && isLoginRequired) {
                    // Modo visitante: mostra tela para fazer login
                    GuestProfileScreen(
                        onLoginClick = onLoginClick,
                        onBackClick = {
                            moreSubScreen = MoreSubScreen.MENU
                        }
                    )
                } else {
                    // Modo normal: mostra perfil completo
                    ProfileScreen(
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
                        onMyOrdersClick = {
                            moreSubScreen = MoreSubScreen.MY_ORDERS
                        },
                        onSecretAccessGranted = {
                            showSecretScreen = true
                        },
                        onBackClick = {
                            moreSubScreen = MoreSubScreen.MENU
                        }
                    )
                }
            }

            MoreSubScreen.ALARMS -> {
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onBackClick = {
                        moreSubScreen = MoreSubScreen.MENU
                    }
                )
            }

            MoreSubScreen.CONTACT -> {
                HireMeScreen(
                    onBackClick = {
                        moreSubScreen = MoreSubScreen.MENU
                    }
                )
            }

            MoreSubScreen.MY_ORDERS -> {
                MyOrdersScreen(
                    viewModel = storeViewModel,
                    onBackClick = {
                        moreSubScreen = MoreSubScreen.PROFILE
                    }
                )
            }

            else -> {}
        }
        return
    }

    // Set user ID for store when user is available
    LaunchedEffect(currentUser) {
        currentUser?.let {
            storeViewModel.setUserId(it.id)
        }
    }

    val useSecondaryBars = selectedItem == NavigationItem.LOJA || selectedItem == NavigationItem.MORE

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { newItem ->
                    if (newItem != selectedItem) {
                        // Reset sub-navigation states when switching tabs
                        if (selectedItem == NavigationItem.LOJA) {
                            storeScreenState = StoreScreenState.LIST
                            selectedProduct = null
                        }
                        if (selectedItem == NavigationItem.MORE) {
                            moreSubScreen = MoreSubScreen.MENU
                        }
                    }
                    selectedItem = newItem
                },
                useSecondaryBackground = useSecondaryBars,
                modifier = Modifier.fillMaxWidth()
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedItem) {
                NavigationItem.EVENTO -> EventListScreen(
                    sections = eventSections,
                    isLoading = isLoading,
                    onItemClick = { event ->
                        selectedEvent = event
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                )

                NavigationItem.LOJA -> StoreScreen(
                    viewModel = storeViewModel,
                    isAdmin = currentUser?.isAdmin ?: false,
                    onProductClick = { product ->
                        selectedProduct = product
                        storeScreenState = StoreScreenState.DETAIL
                    },
                    onCartClick = {
                        storeScreenState = StoreScreenState.CART
                    }
                )

                NavigationItem.CHAT -> {
                    if (currentUser == null && isLoginRequired) {
                        GuestProfileScreen(
                            onLoginClick = onLoginClick,
                            onBackClick = {
                                moreSubScreen = MoreSubScreen.MENU
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.statusBars)
                        ) {
                            ChatScreen(
                                currentUsername = currentUser?.username ?: "Usuario",
                                chatViewModel = chatViewModel
                            )
                        }
                    }
                }

                NavigationItem.MAPA -> MapScreen(
                    mapViewModel = mapViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                )
                NavigationItem.MORE -> MoreScreen(
                    onMenuItemClick = { menuItem ->
                        when (menuItem) {
                            MoreMenuItem.PROFILE -> moreSubScreen = MoreSubScreen.PROFILE
                            MoreMenuItem.ALARMS -> moreSubScreen = MoreSubScreen.ALARMS
                            MoreMenuItem.CONTACT -> moreSubScreen = MoreSubScreen.CONTACT
                        }
                    }
                )
            }
        }
    }
}
