package com.carlosnicolaugalves.makelifebetter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.carlosnicolaugalves.makelifebetter.components.NotificationPermissionHandler
import com.carlosnicolaugalves.makelifebetter.data.event.getSampleEventSections
import com.carlosnicolaugalves.makelifebetter.domain.auth.AuthUseCases
import com.carlosnicolaugalves.makelifebetter.domain.auth.AuthValidator
import com.carlosnicolaugalves.makelifebetter.domain.event.EventUseCases
import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository
import com.carlosnicolaugalves.makelifebetter.event.EventSectionsResult
import com.carlosnicolaugalves.makelifebetter.model.ChatMessage
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.OrderStatus
import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory
import com.carlosnicolaugalves.makelifebetter.model.Question
import com.carlosnicolaugalves.makelifebetter.model.QuestionReply
import com.carlosnicolaugalves.makelifebetter.model.User
import com.carlosnicolaugalves.makelifebetter.navigation.NavigationItem
import com.carlosnicolaugalves.makelifebetter.notification.NotificationScheduler
import com.carlosnicolaugalves.makelifebetter.repository.GeneralChatRepository
import com.carlosnicolaugalves.makelifebetter.repository.LocalCartRepository
import com.carlosnicolaugalves.makelifebetter.repository.OrderRepository
import com.carlosnicolaugalves.makelifebetter.repository.ProductRepository
import com.carlosnicolaugalves.makelifebetter.repository.QuestionRepository
import com.carlosnicolaugalves.makelifebetter.repository.createRemoteConfigRepository
import com.carlosnicolaugalves.makelifebetter.screens.event.EventDetailScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.EventListScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.CartScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.CheckoutScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.MyOrdersScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.OrderConfirmationScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.ProductDetailScreen
import com.carlosnicolaugalves.makelifebetter.screens.event.store.StoreScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.GuestProfileScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.LoginRequiredDialog
import com.carlosnicolaugalves.makelifebetter.screens.login.ProfileScreen
import com.carlosnicolaugalves.makelifebetter.screens.login.SecretScreen
import com.carlosnicolaugalves.makelifebetter.screens.more.HireMeScreen
import com.carlosnicolaugalves.makelifebetter.screens.more.NotificationScreen
import com.carlosnicolaugalves.makelifebetter.util.AppStrings
import com.carlosnicolaugalves.makelifebetter.util.Language
import com.carlosnicolaugalves.makelifebetter.util.Translations
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedChatViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedEventViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedNotificationViewModel
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedStoreViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
    strings: AppStrings = Translations.getStrings(Language.PORTUGUESE),
    eventViewModel: SharedEventViewModel = remember { SharedEventViewModel() },
    notificationViewModel: SharedNotificationViewModel = remember { SharedNotificationViewModel() },
    chatViewModel: SharedChatViewModel = remember { SharedChatViewModel() },
    storeViewModel: SharedStoreViewModel = remember { SharedStoreViewModel() },
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

    // Login required dialog for checkout
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val profileUpdateState by viewModel.profileUpdateState.collectAsState()
    val passwordChangeState by viewModel.passwordChangeState.collectAsState()

    val eventSections by eventViewModel.eventSections.collectAsState()
    val sectionsState by eventViewModel.sectionsState.collectAsState()
    val shouldRequestPermission by notificationViewModel.shouldRequestPermission.collectAsState()

    val isLoading = sectionsState is EventSectionsResult.Loading

    val remoteConfigRepository = remember { createRemoteConfigRepository() }
    val isPreview = LocalInspectionMode.current
    var isLoginRequiredState by remember { mutableStateOf(isLoginRequired) }

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
        if (!isPreview) {
            isLoginRequiredState = remoteConfigRepository.isLoginRequired()
        }
    }

    // Se tiver a tela secreta ativa, mostra ela
    if (showSecretScreen) {
        SecretScreen(
            onBackClick = {
                showSecretScreen = false
                // Recarregar eventos ao voltar da tela secreta
                eventViewModel.refreshSections()
            },
            isAdmin = currentUser?.isAdmin == true
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
                        // Check if user is logged in before allowing checkout
                        if (currentUser == null && isLoginRequiredState) {
                            showLoginRequiredDialog = true
                        } else {
                            storeScreenState = StoreScreenState.CHECKOUT
                        }
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

        // Login required dialog for checkout (must be before return)
        if (showLoginRequiredDialog) {
            LoginRequiredDialog(
                strings = strings,
                onLoginClick = {
                    showLoginRequiredDialog = false
                    onLoginClick()
                },
                onDismiss = {
                    showLoginRequiredDialog = false
                }
            )
        }
        return
    }

    // More screen sub-navigation
    if (selectedItem == NavigationItem.MORE && moreSubScreen != MoreSubScreen.MENU) {
        when (moreSubScreen) {
            MoreSubScreen.PROFILE -> {
                if (currentUser == null && isLoginRequiredState) {
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
                        strings = strings,
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
                    strings = strings,
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
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (selectedItem) {
                NavigationItem.EVENTO -> EventListScreen(
                    sections = eventSections,
                    isLoading = isLoading,
                    onItemClick = { event ->
                        selectedEvent = event
                    }
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
                    if (currentUser == null && isLoginRequiredState) {
                        GuestProfileScreen(
                            onLoginClick = onLoginClick,
                            onBackClick = {
                                moreSubScreen = MoreSubScreen.MENU
                            }
                        )
                    } else {
                        ChatScreen(
                            strings = strings,
                            currentUsername = currentUser?.username ?: "Usuario",
                            chatViewModel = chatViewModel
                        )
                    }
                }

                NavigationItem.MAPA -> MapScreen()
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    val sampleUser = User(
        id = "preview_user",
        username = "Usuario Preview",
        email = "preview@makelifebetter.app",
        passwordHash = "hash"
    )

    val authRepository = object : AuthRepository {
        override suspend fun login(username: String, password: String): Result<User> =
            Result.success(sampleUser)

        override suspend fun register(
            username: String,
            email: String,
            password: String
        ): Result<User> =
            Result.success(sampleUser)

        override suspend fun recoverPassword(email: String): Result<String> =
            Result.success("ok")

        override suspend fun getUserByEmail(email: String): User? = sampleUser

        override suspend fun getUserByUsername(username: String): User? = sampleUser

        override suspend fun updateProfile(
            userId: String,
            username: String,
            email: String
        ): Result<User> =
            Result.success(sampleUser.copy(username = username, email = email))

        override suspend fun changePassword(
            currentPassword: String,
            newPassword: String
        ): Result<String> =
            Result.success("ok")

        override suspend fun logout(): Result<Unit> = Result.success(Unit)
    }

    val loginViewModel = remember {
        SharedLoginViewModel(
            authUseCases = AuthUseCases(authRepository, AuthValidator())
        )
    }

    val eventRepository = object : EventRepository {
        override suspend fun getEvents(): Result<List<Event>> =
            Result.success(getSampleEventSections().flatMap { it.eventos })

        override suspend fun getEventsByCategory(categoria: String): Result<List<Event>> =
            Result.success(getSampleEventSections().flatMap { it.eventos }
                .filter { it.categoria == categoria })

        override suspend fun getEventSections(): Result<List<EventSection>> =
            Result.success(getSampleEventSections())
    }

    val eventViewModel = remember {
        SharedEventViewModel(
            eventUseCases = EventUseCases(eventRepository)
        )
    }

    val notificationScheduler = object : NotificationScheduler {
        override suspend fun schedule(
            notification: com.carlosnicolaugalves.makelifebetter.model.AppNotification
        ): Boolean = true

        override suspend fun cancel(notificationId: String) {}
        override suspend fun cancelAll() {}
        override suspend fun hasPermission(): Boolean = true
        override suspend fun requestPermission(): Boolean = true
    }

    val notificationViewModel = remember {
        SharedNotificationViewModel(notificationScheduler)
    }

    val productCategories = listOf(
        ProductCategory(id = "cat_1", nome = "Camisetas", ordem = 1),
        ProductCategory(id = "cat_2", nome = "Acessorios", ordem = 2),
        ProductCategory(id = "cat_3", nome = "Colecionaveis", ordem = 3)
    )

    val products = listOf(
        Product(
            id = "prod_1",
            nome = "Camiseta MakeLifeBetter",
            descricao = "Algodao premium, unissex",
            preco = 79.9,
            imagem = "",
            categoria = "cat_1"
        ),
        Product(
            id = "prod_2",
            nome = "Caneca Dev",
            descricao = "Perfeita para cafe",
            preco = 39.9,
            imagem = "",
            categoria = "cat_2"
        ),
        Product(
            id = "prod_3",
            nome = "Adesivo Pack",
            descricao = "Colecao limitada",
            preco = 19.9,
            imagem = "",
            categoria = "cat_3"
        )
    )

    val productRepository = object : ProductRepository {
        override suspend fun getProducts(): Result<List<Product>> = Result.success(products)
        override suspend fun getProductById(id: String): Result<Product?> =
            Result.success(products.find { it.id == id })

        override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> =
            Result.success(products.filter { it.categoria == categoryId })

        override suspend fun getCategories(): Result<List<ProductCategory>> =
            Result.success(productCategories)
    }

    val orderRepository = object : OrderRepository {
        override suspend fun getOrders(userId: String): Result<List<Order>> =
            Result.success(emptyList())

        override suspend fun getOrderById(orderId: String): Result<Order> =
            Result.success(
                Order(
                    id = orderId,
                    userId = sampleUser.id,
                    items = emptyList(),
                    totalPrice = 0.0,
                    status = OrderStatus.CONFIRMED,
                    createdAt = 0L
                )
            )
    }

    val storeViewModel = remember {
        SharedStoreViewModel(
            productRepository = productRepository,
            firebaseCartRepository = LocalCartRepository(),
            orderRepository = orderRepository
        )
    }

    val chatRepository = object : GeneralChatRepository {
        override suspend fun getMessages(): Result<List<ChatMessage>> =
            Result.success(listOf(ChatMessage("1", "Equipe", "Bem-vindo ao chat!", 0L)))

        override suspend fun sendMessage(author: String, message: String): Result<ChatMessage> =
            Result.success(ChatMessage("2", author, message, 0L))

        override fun observeMessages(): Flow<List<ChatMessage>> =
            flowOf(listOf(ChatMessage("1", "Equipe", "Bem-vindo ao chat!", 0L)))
    }

    val questionRepository = object : QuestionRepository {
        override suspend fun getQuestions(): Result<List<Question>> =
            Result.success(
                listOf(
                    Question(
                        id = "q1",
                        title = "Como acessar as palestras?",
                        description = "Onde encontro a agenda completa?",
                        author = "Participante",
                        replies = 2,
                        timestamp = 0L
                    )
                )
            )

        override suspend fun addQuestion(
            author: String,
            title: String,
            description: String
        ): Result<Question> =
            Result.success(Question("q_new", title, description, author, 0, 0L))

        override suspend fun deleteQuestion(questionId: String): Result<Unit> = Result.success(Unit)

        override suspend fun getReplies(questionId: String): Result<List<QuestionReply>> =
            Result.success(emptyList())

        override suspend fun addReply(
            questionId: String,
            author: String,
            content: String
        ): Result<QuestionReply> =
            Result.success(QuestionReply("r1", questionId, author, content, 0L))

        override suspend fun deleteReply(questionId: String, replyId: String): Result<Unit> =
            Result.success(Unit)
    }

    val chatViewModel = remember {
        SharedChatViewModel(
            chatRepository = chatRepository,
            questionRepository = questionRepository
        )
    }

    LaunchedEffect(Unit) {
        loginViewModel.login("preview", "preview")
    }

    MaterialTheme {
        MainScreen(
            viewModel = loginViewModel,
            eventViewModel = eventViewModel,
            notificationViewModel = notificationViewModel,
            chatViewModel = chatViewModel,
            storeViewModel = storeViewModel,
            isLoginRequired = false,
            onLoginClick = {},
            onLogout = {}
        )
    }
}
