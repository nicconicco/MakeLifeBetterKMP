package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Cart
import com.carlosnicolaugalves.makelifebetter.model.CartItem
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.OrderStatus
import com.carlosnicolaugalves.makelifebetter.model.Product
import kotlin.time.Clock
import kotlin.random.Random

class LocalCartRepository : CartRepository {

    private val carts = mutableMapOf<String, MutableList<CartItem>>()

    override suspend fun getCart(userId: String): Result<Cart> {
        val items = carts[userId] ?: mutableListOf()
        return Result.success(Cart(items = items.toList()))
    }

    override suspend fun addToCart(userId: String, product: Product, quantidade: Int): Result<Cart> {
        val items = carts.getOrPut(userId) { mutableListOf() }

        val existingItem = items.find { it.productId == product.id }
        if (existingItem != null) {
            val index = items.indexOf(existingItem)
            items[index] = existingItem.copy(quantidade = existingItem.quantidade + quantidade)
        } else {
            val newItem = CartItem(
                id = Random.nextLong().toString(),
                productId = product.id,
                product = product,
                quantidade = quantidade,
                addedAt = Clock.System.now().toEpochMilliseconds()
            )
            items.add(newItem)
        }

        return Result.success(Cart(items = items.toList()))
    }

    override suspend fun updateQuantity(userId: String, itemId: String, quantidade: Int): Result<Cart> {
        val items = carts[userId] ?: return Result.success(Cart())

        val index = items.indexOfFirst { it.id == itemId }
        if (index != -1) {
            if (quantidade <= 0) {
                items.removeAt(index)
            } else {
                items[index] = items[index].copy(quantidade = quantidade)
            }
        }

        return Result.success(Cart(items = items.toList()))
    }

    override suspend fun removeFromCart(userId: String, itemId: String): Result<Cart> {
        val items = carts[userId] ?: return Result.success(Cart())
        items.removeAll { it.id == itemId }
        return Result.success(Cart(items = items.toList()))
    }

    override suspend fun clearCart(userId: String): Result<Unit> {
        carts[userId]?.clear()
        return Result.success(Unit)
    }

    override suspend fun checkout(userId: String): Result<Order> {
        val items = carts[userId]?.toList() ?: emptyList()
        if (items.isEmpty()) {
            return Result.failure(Exception("Carrinho vazio"))
        }

        val order = Order(
            id = "ORD-${Random.nextLong(100000, 999999)}",
            userId = userId,
            items = items,
            totalPrice = items.sumOf { it.product.preco * it.quantidade },
            status = OrderStatus.CONFIRMED,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )

        carts[userId]?.clear()

        return Result.success(order)
    }
}
