package com.carlosnicolaugalves.makelifebetter.model

data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val createdAt: Long,
    val paymentMethod: String = "",
    val paymentIntentId: String = ""
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
