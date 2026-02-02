//
//  MainScreen.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import SwiftUI
import ComposeApp

// Store screen states
private enum StoreScreenState {
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
    @State private var storeScreenState: StoreScreenState = .list
    @State private var selectedProduct: Product? = nil

    // More screen navigation state
    @State private var moreSubScreen: MoreSubScreen = .menu

    var body: some View {
        @State var idiomaAtual: Idioma = .portugues

        var strings: AppStrings {
            Traducoes.obterStrings(idioma: idiomaAtual)
        }

        // Store screen sub-navigation
        if selectedItem == .loja && storeScreenState != .list {
            switch storeScreenState {
            case .detail:
                if let product = selectedProduct {
                    ProductDetailView(
                        product: product,
                        onBackClick: {
                            selectedProduct = nil
                            storeScreenState = .list
                        },
                        onAddToCart: { prod, qty in
                            storeViewModel.addToCart(product: prod, quantidade: qty)
                            selectedProduct = nil
                            storeScreenState = .list
                        }
                    )
                }
            case .cart:
                CartView(
                    viewModel: storeViewModel,
                    onBackClick: {
                        storeScreenState = .list
                    },
                    onCheckout: {
                        storeViewModel.checkout()
                        storeScreenState = .orderConfirmation
                    }
                )
            case .orderConfirmation:
                OrderConfirmationView(
                    order: storeViewModel.lastOrder,
                    onBackToStore: {
                        storeViewModel.resetOrderState()
                        storeScreenState = .list
                    }
                )
            default:
                EmptyView()
            }
        } else if selectedItem == .more && moreSubScreen != .menu {
            // More screen sub-navigation
            switch moreSubScreen {
            case .profile:
                MeView(
                    currentScreen: $currentScreen,
                    strings: strings,
                    viewModel: loginViewModel
                )
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(action: { moreSubScreen = .menu }) {
                            Image(systemName: "chevron.left")
                        }
                    }
                }
            case .alarms:
                NotificationView(viewModel: notificationViewModel)
                    .toolbar {
                        ToolbarItem(placement: .navigationBarLeading) {
                            Button(action: { moreSubScreen = .menu }) {
                                Image(systemName: "chevron.left")
                            }
                        }
                    }
            case .contact:
                HireMeView()
                    .toolbar {
                        ToolbarItem(placement: .navigationBarLeading) {
                            Button(action: { moreSubScreen = .menu }) {
                                Image(systemName: "chevron.left")
                            }
                        }
                    }
            default:
                EmptyView()
            }
        } else {
            NavigationStack(path: $navigationPath) {
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

                    StoreView(
                        viewModel: storeViewModel,
                        onProductClick: { product in
                            selectedProduct = product
                            storeScreenState = .detail
                        },
                        onCartClick: {
                            storeScreenState = .cart
                        }
                    )
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
                        // Reset sub-navigation states when switching tabs
                        if oldValue == .loja {
                            storeScreenState = .list
                            selectedProduct = nil
                        }
                        if oldValue == .more {
                            moreSubScreen = .menu
                        }
                    }
                }
                .navigationDestination(for: Event.self) { event in
                    EventDetailScreen(event: event)
                }
            }
            .onAppear {
                // Set user ID for store when user is available
                if let user = loginViewModel.currentUser {
                    storeViewModel.setUserId(user.id)
                }
            }
        }
    }
}

// MARK: - Order Confirmation View
struct OrderConfirmationView: View {
    let order: Order?
    let onBackToStore: () -> Void

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                Spacer()

                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 80))
                    .foregroundColor(.green)

                Text("Pedido Confirmado!")
                    .font(.title)
                    .fontWeight(.bold)

                if let order = order {
                    VStack(spacing: 8) {
                        Text("Pedido: \(order.id)")
                            .font(.headline)
                            .foregroundColor(.secondary)

                        Text(String(format: "Total: R$ %.2f", order.totalPrice))
                            .font(.title2)
                            .fontWeight(.semibold)
                            .foregroundColor(.accentColor)
                    }
                }

                Text("Obrigado pela sua compra!")
                    .font(.body)
                    .foregroundColor(.secondary)

                Spacer()

                Button(action: onBackToStore) {
                    Text("Voltar para a Loja")
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .padding(.horizontal)
                .padding(.bottom)
            }
            .navigationTitle("Confirmação")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
