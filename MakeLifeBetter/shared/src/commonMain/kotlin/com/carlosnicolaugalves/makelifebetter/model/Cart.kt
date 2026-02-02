package com.carlosnicolaugalves.makelifebetter.model

data class CartItem(
    val id: String,
    val productId: String,
    val product: Product,
    val quantidade: Int,
    val addedAt: Long
)

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    val totalItems: Int
        get() = items.sumOf { it.quantidade }

    val totalPrice: Double
        get() = items.sumOf { it.product.preco * it.quantidade }

    companion object {
        fun empty(): Cart = Cart(emptyList())
    }
}
