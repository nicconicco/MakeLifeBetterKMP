package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.Address
import com.carlosnicolaugalves.makelifebetter.model.Cart
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.PaymentInfo
import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory
import com.carlosnicolaugalves.makelifebetter.repository.CartRepository
import com.carlosnicolaugalves.makelifebetter.repository.OrderRepository
import com.carlosnicolaugalves.makelifebetter.repository.ProductRepository
import com.carlosnicolaugalves.makelifebetter.repository.createCartRepository
import com.carlosnicolaugalves.makelifebetter.repository.createOrderRepository
import com.carlosnicolaugalves.makelifebetter.repository.createProductRepository
import com.carlosnicolaugalves.makelifebetter.store.CartResult
import com.carlosnicolaugalves.makelifebetter.store.CategoriesResult
import com.carlosnicolaugalves.makelifebetter.store.OrderResult
import com.carlosnicolaugalves.makelifebetter.store.OrdersResult
import com.carlosnicolaugalves.makelifebetter.store.ProductsResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SharedStoreViewModel(
    private val productRepository: ProductRepository = createProductRepository(),
    private val cartRepository: CartRepository = createCartRepository(),
    private val orderRepository: OrderRepository = createOrderRepository()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Products state
    private val _productsState = MutableStateFlow<ProductsResult>(ProductsResult.Idle)
    val productsState: StateFlow<ProductsResult> = _productsState.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Categories state
    private val _categoriesState = MutableStateFlow<CategoriesResult>(CategoriesResult.Idle)
    val categoriesState: StateFlow<CategoriesResult> = _categoriesState.asStateFlow()

    private val _categories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val categories: StateFlow<List<ProductCategory>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: StateFlow<ProductCategory?> = _selectedCategory.asStateFlow()

    // Cart state
    private val _cartState = MutableStateFlow<CartResult>(CartResult.Idle)
    val cartState: StateFlow<CartResult> = _cartState.asStateFlow()

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()

    // Order state
    private val _orderState = MutableStateFlow<OrderResult>(OrderResult.Idle)
    val orderState: StateFlow<OrderResult> = _orderState.asStateFlow()

    // Orders list state
    private val _ordersState = MutableStateFlow<OrdersResult>(OrdersResult.Idle)
    val ordersState: StateFlow<OrdersResult> = _ordersState.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Selected product for detail screen
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // User ID (simplified - in real app would come from auth)
    private var currentUserId: String = "anonymous"

    init {
        loadCategories()
        loadProducts()
    }

    fun setUserId(userId: String) {
        currentUserId = userId
        loadCart()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _productsState.value = ProductsResult.Loading

            productRepository.getProducts()
                .onSuccess { productList ->
                    _products.value = productList
                    _productsState.value = ProductsResult.Success(productList)
                }
                .onFailure { exception ->
                    _productsState.value = ProductsResult.Error(
                        exception.message ?: "Erro ao carregar produtos"
                    )
                }
        }
    }

    fun loadProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            _productsState.value = ProductsResult.Loading

            productRepository.getProductsByCategory(categoryId)
                .onSuccess { productList ->
                    _products.value = productList
                    _productsState.value = ProductsResult.Success(productList)
                }
                .onFailure { exception ->
                    _productsState.value = ProductsResult.Error(
                        exception.message ?: "Erro ao carregar produtos"
                    )
                }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = CategoriesResult.Loading

            productRepository.getCategories()
                .onSuccess { categoryList ->
                    _categories.value = categoryList
                    _categoriesState.value = CategoriesResult.Success(categoryList)
                }
                .onFailure { exception ->
                    _categoriesState.value = CategoriesResult.Error(
                        exception.message ?: "Erro ao carregar categorias"
                    )
                }
        }
    }

    fun selectCategory(category: ProductCategory?) {
        _selectedCategory.value = category
        if (category != null) {
            loadProductsByCategory(category.id)
        } else {
            loadProducts()
        }
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.value = product
    }

    fun loadCart() {
        viewModelScope.launch {
            _cartState.value = CartResult.Loading

            cartRepository.getCart(currentUserId)
                .onSuccess { cartData ->
                    _cart.value = cartData
                    _cartState.value = CartResult.Success(cartData)
                }
                .onFailure { exception ->
                    _cartState.value = CartResult.Error(
                        exception.message ?: "Erro ao carregar carrinho"
                    )
                }
        }
    }

    fun addToCart(product: Product, quantidade: Int = 1) {
        viewModelScope.launch {
            cartRepository.addToCart(currentUserId, product, quantidade)
                .onSuccess { cartData ->
                    _cart.value = cartData
                    _cartState.value = CartResult.Success(cartData)
                }
                .onFailure { exception ->
                    _cartState.value = CartResult.Error(
                        exception.message ?: "Erro ao adicionar ao carrinho"
                    )
                }
        }
    }

    fun updateCartItemQuantity(itemId: String, quantidade: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(currentUserId, itemId, quantidade)
                .onSuccess { cartData ->
                    _cart.value = cartData
                    _cartState.value = CartResult.Success(cartData)
                }
                .onFailure { exception ->
                    _cartState.value = CartResult.Error(
                        exception.message ?: "Erro ao atualizar quantidade"
                    )
                }
        }
    }

    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(currentUserId, itemId)
                .onSuccess { cartData ->
                    _cart.value = cartData
                    _cartState.value = CartResult.Success(cartData)
                }
                .onFailure { exception ->
                    _cartState.value = CartResult.Error(
                        exception.message ?: "Erro ao remover do carrinho"
                    )
                }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart(currentUserId)
                .onSuccess {
                    _cart.value = Cart()
                    _cartState.value = CartResult.Success(Cart())
                }
                .onFailure { exception ->
                    _cartState.value = CartResult.Error(
                        exception.message ?: "Erro ao limpar carrinho"
                    )
                }
        }
    }

    fun checkout() {
        viewModelScope.launch {
            _orderState.value = OrderResult.Loading

            cartRepository.checkout(currentUserId)
                .onSuccess { order ->
                    _cart.value = Cart()
                    _cartState.value = CartResult.Success(Cart())
                    _orderState.value = OrderResult.Success(order)
                }
                .onFailure { exception ->
                    _orderState.value = OrderResult.Error(
                        exception.message ?: "Erro ao finalizar pedido"
                    )
                }
        }
    }

    fun checkoutWithInfo(address: Address, payment: PaymentInfo) {
        viewModelScope.launch {
            _orderState.value = OrderResult.Loading

            cartRepository.checkoutWithInfo(currentUserId, address, payment)
                .onSuccess { order ->
                    _cart.value = Cart()
                    _cartState.value = CartResult.Success(Cart())
                    _orderState.value = OrderResult.Success(order)
                }
                .onFailure { exception ->
                    _orderState.value = OrderResult.Error(
                        exception.message ?: "Erro ao finalizar pedido"
                    )
                }
        }
    }

    fun resetOrderState() {
        _orderState.value = OrderResult.Idle
    }

    fun loadOrders() {
        viewModelScope.launch {
            _ordersState.value = OrdersResult.Loading

            orderRepository.getOrders(currentUserId)
                .onSuccess { ordersList ->
                    _orders.value = ordersList
                    _ordersState.value = OrdersResult.Success(ordersList)
                }
                .onFailure { exception ->
                    _ordersState.value = OrdersResult.Error(
                        exception.message ?: "Erro ao carregar pedidos"
                    )
                }
        }
    }

    // iOS bridge methods
    fun observeProducts(callback: (List<Product>) -> Unit): Job {
        return products.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observeCategories(callback: (List<ProductCategory>) -> Unit): Job {
        return categories.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observeCart(callback: (Cart) -> Unit): Job {
        return cart.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observeOrderState(callback: (OrderResult) -> Unit): Job {
        return orderState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observeOrders(callback: (List<Order>) -> Unit): Job {
        return orders.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observeOrdersState(callback: (OrdersResult) -> Unit): Job {
        return ordersState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun clear() {
        viewModelScope.launch {
            // Cleanup if needed
        }
    }
}
