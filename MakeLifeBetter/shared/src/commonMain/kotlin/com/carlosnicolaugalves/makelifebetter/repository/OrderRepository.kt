package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Order

interface OrderRepository {
    suspend fun getOrders(userId: String): Result<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order>
}
