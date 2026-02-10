//
//  MainScreen.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//

import SwiftUI
import Foundation
import ComposeApp

// Store screen states
private enum StoreScreen {
    case list
    case detail
    case cart
    case checkout
    case orderConfirmation
}

// More screen sub-navigation states
private enum MoreSubScreen {
    case menu
    case profile
    case alarms
    case contact
    case myOrders
}

struct MainView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    var loginViewModel: LoginViewModel
    var theme: ThemePalette = ThemeDefaults.light
    @State var eventViewModel = EventViewModel()
    @State var chatViewModel = ChatViewModel()
    @State var mapViewModel = MapViewModel()
    @State var notificationViewModel = NotificationViewModel()
    @State var storeViewModel = StoreViewModel()

    @State private var remoteConfig = RemoteConfigRepositoryWrapper()
    @State private var isLoginRequired: Bool = true
    @State private var didLoadRemoteConfig: Bool = false
    @State private var remoteConfigTimer: Timer? = nil

    @State private var selectedItem: NavigationItem = .evento
    @State private var navigationPath = NavigationPath()

    // Store navigation states
    @State private var storeScreen: StoreScreen = .list
    @State private var selectedProduct: Product? = nil

    // More screen navigation state
    @State private var moreSubScreen: MoreSubScreen = .menu

    var body: some View {
        @State var idiomaAtual: Idioma = .portugues

        var strings: AppStrings {
            Traducoes.obterStrings(idioma: idiomaAtual)
        }

        NavigationStack(path: $navigationPath) {
            Group {
                if selectedItem == .more && moreSubScreen != .menu {
                    moreSubScreenView(strings: strings)
                } else {
                    mainTabView(strings: strings)
                }
            }
            .navigationDestination(for: Event.self) { event in
                EventDetailScreen(event: event)
            }
        }
        .onAppear {
            if !didLoadRemoteConfig {
                didLoadRemoteConfig = true
                isLoginRequired = remoteConfig.isLoginRequired()
            }
            startRemoteConfigSync()
            if let user = loginViewModel.currentUser {
                storeViewModel.setUserId(user.id)
            }
        }
        .onDisappear {
            stopRemoteConfigSync()
        }
    }

    // MARK: - Remote Config Sync
    private func startRemoteConfigSync() {
        fetchAndApplyRemoteConfig()
        remoteConfigTimer?.invalidate()
        remoteConfigTimer = Timer.scheduledTimer(withTimeInterval: 300, repeats: true) { _ in
            fetchAndApplyRemoteConfig()
        }
    }

    private func stopRemoteConfigSync() {
        remoteConfigTimer?.invalidate()
        remoteConfigTimer = nil
    }

    private func fetchAndApplyRemoteConfig() {
        remoteConfig.fetchAndActivate(
            onSuccess: { _ in
                DispatchQueue.main.async {
                    isLoginRequired = remoteConfig.isLoginRequired()
                }
            },
            onError: { _ in
                DispatchQueue.main.async {
                    isLoginRequired = remoteConfig.isLoginRequired()
                }
            }
        )
    }

    // MARK: - More Sub Screen View
    @ViewBuilder
    private func moreSubScreenView(strings: AppStrings) -> some View {
        switch moreSubScreen {
        case .profile:
            Group {
                if loginViewModel.currentUser == nil {
                    GuestProfileView(
                        strings: strings,
                        onLoginClick: { currentScreen = .login },
                        onBackClick: { moreSubScreen = .menu },
                        theme: theme
                    )
                } else {
                    MeView(
                        currentScreen: $currentScreen,
                        strings: strings,
                        viewModel: loginViewModel,
                        theme: theme,
                        onMyOrdersClick: { moreSubScreen = .myOrders }
                    )
                }
            }
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: { moreSubScreen = .menu }) {
                        HStack(spacing: 4) {
                            Image(systemName: "chevron.left")
                            Text(strings.voltar)
                        }
                    }
                }
            }
        case .alarms:
            NotificationView(viewModel: notificationViewModel)
                .navigationBarBackButtonHidden(true)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(action: { moreSubScreen = .menu }) {
                            HStack(spacing: 4) {
                                Image(systemName: "chevron.left")
                                Text(strings.voltar)
                            }
                        }
                    }
                }
        case .contact:
            HireMeView()
                .navigationBarBackButtonHidden(true)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(action: { moreSubScreen = .menu }) {
                            HStack(spacing: 4) {
                                Image(systemName: "chevron.left")
                                Text(strings.voltar)
                            }
                        }
                    }
                }
        case .myOrders:
            MyOrdersView(
                viewModel: storeViewModel,
                onBackClick: { moreSubScreen = .profile }
            )
        default:
            EmptyView()
        }
    }

    // MARK: - Main Tab View
    @ViewBuilder
    private func mainTabView(strings: AppStrings) -> some View {
        TabView(selection: $selectedItem) {
            SectionedListView(
                viewModel: eventViewModel,
                onItemClick: { event in
                    navigationPath.append(event)
                }
            )
            .tabItem {
                Label("Event", systemImage: "calendar")
            }
            .tag(NavigationItem.evento)

            storeTabContent
                .tabItem {
                    Label("Store", systemImage: "cart")
                }
                .tag(NavigationItem.loja)

            Group {
                if loginViewModel.currentUser == nil {
                    GuestProfileView(
                        strings: strings,
                        onLoginClick: { currentScreen = .login },
                        onBackClick: { selectedItem = .evento }
                    )
                } else {
                    ChatView(
                        currentUsername: loginViewModel.currentUser?.username ?? "Usuario",
                        viewModel: chatViewModel
                    )
                }
            }
            .tabItem {
                Label("Chat", systemImage: "message")
            }
            .tag(NavigationItem.chat)

            MapView(viewModel: mapViewModel)
                .tabItem {
                    Label("Map", systemImage: "map")
                }
                .tag(NavigationItem.mapa)

            MoreView(
                onProfileClick: { moreSubScreen = .profile },
                onAlarmsClick: { moreSubScreen = .alarms },
                onContactClick: { moreSubScreen = .contact }
            )
            .tabItem {
                Label("More", systemImage: "ellipsis")
            }
            .tag(NavigationItem.more)
        }
        .onChange(of: selectedItem) { oldValue, newValue in
            if oldValue != newValue {
                if oldValue == .loja {
                    storeScreen = .list
                    selectedProduct = nil
                }
                if oldValue == .more {
                    moreSubScreen = .menu
                }
            }
        }
    }

    // MARK: - Store Tab Content
    @ViewBuilder
    private var storeTabContent: some View {
        switch storeScreen {
        case .list:
            StoreView(
                viewModel: storeViewModel,
                onProductClick: { product in
                    selectedProduct = product
                    storeScreen = .detail
                },
                onCartClick: {
                    storeScreen = .cart
                }
            )
        case .detail:
            if let product = selectedProduct {
                ProductDetailView(
                    product: product,
                    onBackClick: {
                        selectedProduct = nil
                        storeScreen = .list
                    },
                    onAddToCart: { prod, qty in
                        storeViewModel.addToCart(product: prod, quantidade: qty)
                        selectedProduct = nil
                        storeScreen = .list
                    }
                )
            }
        case .cart:
            CartView(
                viewModel: storeViewModel,
                onBackClick: {
                    storeScreen = .list
                },
                onCheckout: {
                    storeScreen = .checkout
                }
            )
        case .checkout:
            CheckoutView(
                viewModel: storeViewModel,
                onBackClick: {
                    storeScreen = .cart
                },
                onConfirmOrder: {
                    storeScreen = .orderConfirmation
                }
            )
        case .orderConfirmation:
            OrderConfirmationView(
                order: storeViewModel.lastOrder,
                onBackToStore: {
                    storeViewModel.resetOrderState()
                    storeScreen = .list
                }
            )
        }
    }

}
