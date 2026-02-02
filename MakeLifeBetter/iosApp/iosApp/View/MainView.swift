//
//  MainScreen.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//

import SwiftUI
import ComposeApp

// Store screen states
private enum StoreScreen {
    case list
    case detail
    case cart
    case orderConfirmation
}

// More screen sub-navigation states
private enum MoreSubScreen {
    case menu
    case profile
    case alarms
    case contact
}

struct MainView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    var loginViewModel: LoginViewModel
    @State var eventViewModel = EventViewModel()
    @State var chatViewModel = ChatViewModel()
    @State var mapViewModel = MapViewModel()
    @State var notificationViewModel = NotificationViewModel()
    @State var storeViewModel = StoreViewModel()

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
            if let user = loginViewModel.currentUser {
                storeViewModel.setUserId(user.id)
            }
        }
    }

    // MARK: - More Sub Screen View
    @ViewBuilder
    private func moreSubScreenView(strings: AppStrings) -> some View {
        switch moreSubScreen {
        case .profile:
            MeView(
                currentScreen: $currentScreen,
                strings: strings,
                viewModel: loginViewModel
            )
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: { moreSubScreen = .menu }) {
                        HStack(spacing: 4) {
                            Image(systemName: "chevron.left")
                            Text("Voltar")
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
                                Text("Voltar")
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
                                Text("Voltar")
                            }
                        }
                    }
                }
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

            ChatView(
                currentUsername: loginViewModel.currentUser?.username ?? "Usuario",
                viewModel: chatViewModel
            )
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
                    storeViewModel.checkout()
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
