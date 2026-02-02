package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.Cart
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory
import com.carlosnicolaugalves.makelifebetter.store.CartResult
import com.carlosnicolaugalves.makelifebetter.store.CategoriesResult
import com.carlosnicolaugalves.makelifebetter.store.OrderResult
import com.carlosnicolaugalves.makelifebetter.store.ProductsResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SharedStoreViewModelWrapper {
    private val viewModel = SharedStoreViewModel()
    private val scope = CoroutineScope(Dispatchers.Main)

    private var productsStateObserver: Job? = null
    private var productsObserver: Job? = null
    private var categoriesStateObserver: Job? = null
    private var categoriesObserver: Job? = null
    private var cartStateObserver: Job? = null
    private var cartObserver: Job? = null
    private var orderStateObserver: Job? = null

    // MARK: - User

    fun setUserId(userId: String) {
        viewModel.setUserId(userId)
    }

    // MARK: - Products

    fun observeProductsState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        productsStateObserver?.cancel()
        productsStateObserver = viewModel.productsState
            .onEach { state ->
                when (state) {
                    is ProductsResult.Idle -> onIdle()
                    is ProductsResult.Loading -> onLoading()
                    is ProductsResult.Success -> onSuccess(state.products)
                    is ProductsResult.Error -> onError(state.message)
                }
            }
            .launchIn(scope)
    }

    fun observeProducts(callback: (List<Product>) -> Unit) {
        productsObserver?.cancel()
        productsObserver = viewModel.observeProducts(callback)
    }

    fun loadProducts() {
        viewModel.loadProducts()
    }

    fun loadProductsByCategory(categoryId: String) {
        viewModel.loadProductsByCategory(categoryId)
    }

    // MARK: - Categories

    fun observeCategoriesState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (List<ProductCategory>) -> Unit,
        onError: (String) -> Unit
    ) {
        categoriesStateObserver?.cancel()
        categoriesStateObserver = viewModel.categoriesState
            .onEach { state ->
                when (state) {
                    is CategoriesResult.Idle -> onIdle()
                    is CategoriesResult.Loading -> onLoading()
                    is CategoriesResult.Success -> onSuccess(state.categories)
                    is CategoriesResult.Error -> onError(state.message)
                }
            }
            .launchIn(scope)
    }

    fun observeCategories(callback: (List<ProductCategory>) -> Unit) {
        categoriesObserver?.cancel()
        categoriesObserver = viewModel.observeCategories(callback)
    }

    fun loadCategories() {
        viewModel.loadCategories()
    }

    fun selectCategory(category: ProductCategory?) {
        viewModel.selectCategory(category)
    }

    // MARK: - Cart

    fun observeCartState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (Cart) -> Unit,
        onError: (String) -> Unit
    ) {
        cartStateObserver?.cancel()
        cartStateObserver = viewModel.cartState
            .onEach { state ->
                when (state) {
                    is CartResult.Idle -> onIdle()
                    is CartResult.Loading -> onLoading()
                    is CartResult.Success -> onSuccess(state.cart)
                    is CartResult.Error -> onError(state.message)
                }
            }
            .launchIn(scope)
    }

    fun observeCart(callback: (Cart) -> Unit) {
        cartObserver?.cancel()
        cartObserver = viewModel.observeCart(callback)
    }

    fun loadCart() {
        viewModel.loadCart()
    }

    fun addToCart(product: Product, quantidade: Int = 1) {
        viewModel.addToCart(product, quantidade)
    }

    fun updateCartItemQuantity(itemId: String, quantidade: Int) {
        viewModel.updateCartItemQuantity(itemId, quantidade)
    }

    fun removeFromCart(itemId: String) {
        viewModel.removeFromCart(itemId)
    }

    fun clearCart() {
        viewModel.clearCart()
    }

    // MARK: - Order

    fun observeOrderState(
        onIdle: () -> Unit,
        onLoading: () -> Unit,
        onSuccess: (Order) -> Unit,
        onError: (String) -> Unit
    ) {
        orderStateObserver?.cancel()
        orderStateObserver = viewModel.observeOrderState { state ->
            when (state) {
                is OrderResult.Idle -> onIdle()
                is OrderResult.Loading -> onLoading()
                is OrderResult.Success -> onSuccess(state.order)
                is OrderResult.Error -> onError(state.message)
            }
        }
    }

    fun checkout() {
        viewModel.checkout()
    }

    fun resetOrderState() {
        viewModel.resetOrderState()
    }

    // MARK: - Cleanup

    fun clear() {
        productsStateObserver?.cancel()
        productsObserver?.cancel()
        categoriesStateObserver?.cancel()
        categoriesObserver?.cancel()
        cartStateObserver?.cancel()
        cartObserver?.cancel()
        orderStateObserver?.cancel()
        viewModel.clear()
    }
}
