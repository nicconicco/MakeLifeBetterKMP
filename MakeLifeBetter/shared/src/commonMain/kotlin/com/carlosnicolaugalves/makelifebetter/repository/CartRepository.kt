package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Cart
import com.carlosnicolaugalves.makelifebetter.model.CartItem
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.Product

interface CartRepository {
    suspend fun getCart(userId: String): Result<Cart>
    suspend fun addToCart(userId: String, product: Product, quantidade: Int): Result<Cart>
    suspend fun updateQuantity(userId: String, itemId: String, quantidade: Int): Result<Cart>
    suspend fun removeFromCart(userId: String, itemId: String): Result<Cart>
    suspend fun clearCart(userId: String): Result<Unit>
    suspend fun checkout(userId: String): Result<Order>
}
