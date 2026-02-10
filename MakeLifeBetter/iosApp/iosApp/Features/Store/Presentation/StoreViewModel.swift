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

@Observable
class StoreViewModel {
    private let sharedViewModel: SharedStoreViewModelWrapper

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
    }

    // MARK: - Actions

    func setUserId(_ userId: String) {
        sharedViewModel.setUserId(userId: userId)
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
        sharedViewModel.addToCart(product: product, quantidade: quantidade)
    }

    func updateQuantity(itemId: String, quantidade: Int32) {
        sharedViewModel.updateCartItemQuantity(itemId: itemId, quantidade: quantidade)
    }

    func removeFromCart(itemId: String) {
        sharedViewModel.removeFromCart(itemId: itemId)
    }

    func clearCart() {
        sharedViewModel.clearCart()
    }

    func checkout() {
        sharedViewModel.checkout()
    }

    func resetOrderState() {
        lastOrder = nil
        sharedViewModel.resetOrderState()
    }

    func loadOrders() {
        sharedViewModel.loadOrders()
    }

    func checkoutWithInfo(address: Address, payment: PaymentInfo) {
        sharedViewModel.checkoutWithInfo(address: address, payment: payment)
    }

    func clearError() {
        errorMessage = nil
    }

    deinit {
        sharedViewModel.clear()
    }
}
