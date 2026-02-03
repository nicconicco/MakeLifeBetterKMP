package com.carlosnicolaugalves.makelifebetter.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemData(
    val productId: String = "",
    val productName: String = "",
    val quantidade: Int = 1,
    val preco: Double = 0.0
)
