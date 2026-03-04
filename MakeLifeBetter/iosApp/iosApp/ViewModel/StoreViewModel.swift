//
//  StoreViewModel.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves
//

import Foundation
import Observation
import ComposeApp

enum OrdersLoadingState: Equatable {
    case idle
    case loading
    case success
    case error(String)
}

enum PaymentIntentState: Equatable {
    case idle
    case loading
    case success
    case error(String)
}

struct PaymentIntentInfo {
    let clientSecret: String
    let ephemeralKey: String
    let customerId: String
}

@Observable
class StoreViewModel {
    private let sharedViewModel: SharedStoreViewModelWrapper

    private var currentUserId: String? = nil
    private var guestCartItems: [CartItem] = []

    var products: [Product] = []
    var categories: [ProductCategory] = []
    var selectedCategory: ProductCategory? = nil
    var cart: Cart = Cart.companion.empty()
    var isProductsLoading: Bool = true
    var isCategoriesLoading: Bool = true
    var isCartLoading: Bool = false
    var isCheckingOut: Bool = false
    var errorMessage: String? = nil
    var lastOrder: Order? = nil
    var orders: [Order] = []
    var ordersState: OrdersLoadingState = .idle
    var paymentIntentState: PaymentIntentState = .idle
    var paymentIntentInfo: PaymentIntentInfo? = nil

    var isGuestUser: Bool {
        currentUserId == nil
    }

    init() {
        sharedViewModel = SharedStoreViewModelWrapper()
        setupObservers()
    }

    private func setupObservers() {
        // Products state
        sharedViewModel.observeProductsState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isProductsLoading = false }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.isProductsLoading = true }
            },
            onSuccess: { [weak self] _ in
                DispatchQueue.main.async { self?.isProductsLoading = false }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.isProductsLoading = false
                    self?.errorMessage = message
                }
            }
        )

        // Products list
        sharedViewModel.observeProducts { [weak self] kotlinProducts in
            DispatchQueue.main.async {
                self?.products = kotlinProducts
            }
        }

        // Categories state
        sharedViewModel.observeCategoriesState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isCategoriesLoading = false }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.isCategoriesLoading = true }
            },
            onSuccess: { [weak self] _ in
                DispatchQueue.main.async { self?.isCategoriesLoading = false }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.isCategoriesLoading = false
                    self?.errorMessage = message
                }
            }
        )

        // Categories list
        sharedViewModel.observeCategories { [weak self] kotlinCategories in
            DispatchQueue.main.async {
                self?.categories = kotlinCategories
            }
        }

        // Cart state
        sharedViewModel.observeCartState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isCartLoading = false }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.isCartLoading = true }
            },
            onSuccess: { [weak self] _ in
                DispatchQueue.main.async { self?.isCartLoading = false }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.isCartLoading = false
                    self?.errorMessage = message
                }
            }
        )

        // Cart
        sharedViewModel.observeCart { [weak self] kotlinCart in
            DispatchQueue.main.async {
                self?.cart = kotlinCart
            }
        }

        // Order state
        sharedViewModel.observeOrderState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.isCheckingOut = false }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.isCheckingOut = true }
            },
            onSuccess: { [weak self] order in
                DispatchQueue.main.async {
                    self?.isCheckingOut = false
                    self?.lastOrder = order
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.isCheckingOut = false
                    self?.errorMessage = message
                }
            }
        )

        // Orders list state
        sharedViewModel.observeOrdersState(
            onIdle: { [weak self] in
                DispatchQueue.main.async { self?.ordersState = .idle }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async { self?.ordersState = .loading }
            },
            onSuccess: { [weak self] _ in
                DispatchQueue.main.async { self?.ordersState = .success }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.ordersState = .error(message)
                }
            }
        )

        // Orders list
        sharedViewModel.observeOrders { [weak self] kotlinOrders in
            DispatchQueue.main.async {
                self?.orders = kotlinOrders
            }
        }

        // Payment intent state
        sharedViewModel.observePaymentIntentState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.paymentIntentState = .idle
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.paymentIntentState = .loading
                }
            },
            onSuccess: { [weak self] data in
                DispatchQueue.main.async {
                    self?.paymentIntentInfo = PaymentIntentInfo(
                        clientSecret: data.paymentIntentClientSecret,
                        ephemeralKey: data.ephemeralKey,
                        customerId: data.customerId
                    )
                    self?.paymentIntentState = .success
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.paymentIntentState = .error(message)
                }
            }
        )
    }

    // MARK: - Actions

    func setUserId(_ userId: String) {
        currentUserId = userId
        sharedViewModel.setUserId(userId: userId)
        syncGuestCartIfNeeded()
    }

    func enableGuestMode() {
        currentUserId = nil
        isCheckingOut = false
        lastOrder = nil
        orders = []
        ordersState = .idle
        errorMessage = nil
        updateGuestCart(items: guestCartItems)
    }

    func loadProducts() {
        sharedViewModel.loadProducts()
    }

    func loadCategories() {
        sharedViewModel.loadCategories()
    }

    func selectCategory(_ category: ProductCategory?) {
        selectedCategory = category
        sharedViewModel.selectCategory(category: category)
    }

    func addToCart(product: Product, quantidade: Int32 = 1) {
        if isGuestUser {
            addToGuestCart(product: product, quantidade: quantidade)
            return
        }
        sharedViewModel.addToCart(product: product, quantidade: quantidade)
    }

    func updateQuantity(itemId: String, quantidade: Int32) {
        if isGuestUser {
            updateGuestQuantity(itemId: itemId, quantidade: quantidade)
            return
        }
        sharedViewModel.updateCartItemQuantity(itemId: itemId, quantidade: quantidade)
    }

    func removeFromCart(itemId: String) {
        if isGuestUser {
            removeGuestItem(itemId: itemId)
            return
        }
        sharedViewModel.removeFromCart(itemId: itemId)
    }

    func clearCart() {
        if isGuestUser {
            updateGuestCart(items: [])
            return
        }
        sharedViewModel.clearCart()
    }

    func checkout() {
        if isGuestUser {
            errorMessage = "Faca login para finalizar a compra"
            return
        }
        sharedViewModel.checkout()
    }

    func resetOrderState() {
        lastOrder = nil
        sharedViewModel.resetOrderState()
    }

    func loadOrders() {
        if isGuestUser {
            orders = []
            ordersState = .idle
            errorMessage = "Faca login para ver seus pedidos"
            return
        }
        sharedViewModel.loadOrders()
    }

    func checkoutWithInfo(address: Address, payment: PaymentInfo) {
        if isGuestUser {
            errorMessage = "Faca login para finalizar a compra"
            return
        }
        sharedViewModel.checkoutWithInfo(address: address, payment: payment)
    }

    func createPaymentIntent() {
        if isGuestUser {
            errorMessage = "Faca login para finalizar a compra"
            return
        }
        sharedViewModel.createPaymentIntent()
    }

    func checkoutAfterPayment(address: Address, stripePaymentIntentId: String) {
        sharedViewModel.checkoutAfterPayment(address: address, stripePaymentIntentId: stripePaymentIntentId)
    }

    func resetPaymentIntentState() {
        paymentIntentState = .idle
        paymentIntentInfo = nil
        sharedViewModel.resetPaymentIntentState()
    }

    func clearError() {
        errorMessage = nil
    }

    // MARK: - Guest Cart

    private func updateGuestCart(items: [CartItem]) {
        guestCartItems = items
        cart = Cart(items: items)
        isCartLoading = false
    }

    private func addToGuestCart(product: Product, quantidade: Int32) {
        var items = guestCartItems
        if let index = items.firstIndex(where: { $0.productId == product.id }) {
            let existing = items[index]
            let newQuantity = existing.quantidade + quantidade
            items[index] = CartItem(
                id: existing.id,
                productId: existing.productId,
                product: existing.product,
                quantidade: newQuantity,
                addedAt: existing.addedAt
            )
        } else {
            let item = CartItem(
                id: UUID().uuidString,
                productId: product.id,
                product: product,
                quantidade: quantidade,
                addedAt: Int64(Date().timeIntervalSince1970 * 1000)
            )
            items.append(item)
        }
        updateGuestCart(items: items)
    }

    private func updateGuestQuantity(itemId: String, quantidade: Int32) {
        var items = guestCartItems
        guard let index = items.firstIndex(where: { $0.id == itemId }) else { return }

        if quantidade <= 0 {
            items.remove(at: index)
        } else {
            let existing = items[index]
            items[index] = CartItem(
                id: existing.id,
                productId: existing.productId,
                product: existing.product,
                quantidade: quantidade,
                addedAt: existing.addedAt
            )
        }
        updateGuestCart(items: items)
    }

    private func removeGuestItem(itemId: String) {
        let items = guestCartItems.filter { $0.id != itemId }
        updateGuestCart(items: items)
    }

    private func syncGuestCartIfNeeded() {
        guard !guestCartItems.isEmpty else { return }

        let itemsToSync = guestCartItems
        guestCartItems = []
        cart = Cart.companion.empty()

        for item in itemsToSync {
            sharedViewModel.addToCart(product: item.product, quantidade: item.quantidade)
        }
    }

    deinit {
        sharedViewModel.clear()
    }
}

// MARK: - Product Extension for Display
extension Product {
    var formattedPrice: String {
        return String(format: "R$ %.2f", preco)
    }
}
