package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Order

class LocalOrderRepository : OrderRepository {
    override suspend fun getOrders(userId: String): Result<List<Order>> {
        return Result.success(emptyList())
    }

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return Result.failure(Exception("Pedido nao encontrado"))
    }
}
