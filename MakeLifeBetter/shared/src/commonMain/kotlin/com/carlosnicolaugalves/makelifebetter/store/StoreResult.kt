package com.carlosnicolaugalves.makelifebetter.store

import com.carlosnicolaugalves.makelifebetter.model.Cart
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory

sealed class ProductsResult {
    data object Idle : ProductsResult()
    data object Loading : ProductsResult()
    data class Success(val products: List<Product>) : ProductsResult()
    data class Error(val message: String) : ProductsResult()
}

sealed class CategoriesResult {
    data object Idle : CategoriesResult()
    data object Loading : CategoriesResult()
    data class Success(val categories: List<ProductCategory>) : CategoriesResult()
    data class Error(val message: String) : CategoriesResult()
}

sealed class CartResult {
    data object Idle : CartResult()
    data object Loading : CartResult()
    data class Success(val cart: Cart) : CartResult()
    data class Error(val message: String) : CartResult()
}

sealed class OrderResult {
    data object Idle : OrderResult()
    data object Loading : OrderResult()
    data class Success(val order: Order) : OrderResult()
    data class Error(val message: String) : OrderResult()
}
