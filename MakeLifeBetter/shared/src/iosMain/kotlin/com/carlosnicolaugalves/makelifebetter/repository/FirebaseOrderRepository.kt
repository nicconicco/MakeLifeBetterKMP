package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.CartItem
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.OrderStatus
import com.carlosnicolaugalves.makelifebetter.model.Product
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseOrderRepository : OrderRepository {

    private val firestore by lazy { Firebase.firestore }
    private val ordersCollection by lazy { firestore.collection("pedidos") }

    override suspend fun getOrders(userId: String): Result<List<Order>> {
        return try {
            val querySnapshot = ordersCollection
                .where { "userId" equalTo userId }
                .get()

            val orders = querySnapshot.documents.mapNotNull { doc ->
                try {
                    // Parse items using Map approach (more resilient on iOS)
                    val items = try {
                        val itemsList = doc.get<List<Map<String, Any>>>("items")
                        itemsList.map { itemMap ->
                            val productId = itemMap["productId"]?.toString() ?: ""
                            val productName = itemMap["productName"]?.toString() ?: ""
                            val quantidade = (itemMap["quantidade"] as? Number)?.toInt() ?: 1
                            val preco = (itemMap["preco"] as? Number)?.toDouble() ?: 0.0

                            CartItem(
                                id = productId,
                                productId = productId,
                                product = Product(
                                    id = productId,
                                    nome = productName,
                                    descricao = "",
                                    preco = preco,
                                    imagem = "",
                                    categoria = "",
                                    ativo = true
                                ),
                                quantidade = quantidade,
                                addedAt = 0L
                            )
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }

                    val totalPrice = try {
                        doc.get<Double>("totalPrice")
                    } catch (e: Exception) {
                        try { doc.get<Long>("totalPrice").toDouble() } catch (e2: Exception) { 0.0 }
                    }

                    val createdAt = try {
                        doc.get<Long>("createdAt")
                    } catch (e: Exception) {
                        try { doc.get<Double>("createdAt").toLong() } catch (e2: Exception) { 0L }
                    }

                    val statusStr = try {
                        doc.get<String>("status")
                    } catch (e: Exception) {
                        "PENDING"
                    }

                    val orderUserId = try {
                        doc.get<String>("userId")
                    } catch (e: Exception) {
                        userId
                    }

                    val status = try {
                        OrderStatus.valueOf(statusStr)
                    } catch (e: Exception) {
                        OrderStatus.PENDING
                    }

                    val paymentMap = try {
                        doc.get<Map<String, Any>>("payment")
                    } catch (e: Exception) {
                        null
                    }
                    val paymentMethod = paymentMap?.get("method")?.toString() ?: ""
                    val paymentIntentId = paymentMap?.get("paymentIntentId")?.toString() ?: ""

                    Order(
                        id = doc.id,
                        userId = orderUserId,
                        items = items,
                        totalPrice = totalPrice,
                        status = status,
                        createdAt = createdAt,
                        paymentMethod = paymentMethod,
                        paymentIntentId = paymentIntentId
                    )
                } catch (e: Exception) {
                    println("FirebaseOrderRepo: Erro ao parsear pedido ${doc.id}: ${e.message}")
                    null
                }
            }.sortedByDescending { it.createdAt }

            Result.success(orders)
        } catch (e: Exception) {
            println("FirebaseOrderRepo: Erro ao buscar pedidos: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val doc = ordersCollection.document(orderId).get()

            if (!doc.exists) {
                return Result.failure(Exception("Pedido nao encontrado"))
            }

            val items = try {
                val itemsData = doc.get<List<Map<String, Any>>>("items")
                itemsData.map { itemMap ->
                    val productId = itemMap["productId"]?.toString() ?: ""
                    val productName = itemMap["productName"]?.toString() ?: ""
                    val quantidade = (itemMap["quantidade"] as? Number)?.toInt() ?: 1
                    val preco = (itemMap["preco"] as? Number)?.toDouble() ?: 0.0

                    CartItem(
                        id = productId,
                        productId = productId,
                        product = Product(
                            id = productId,
                            nome = productName,
                            descricao = "",
                            preco = preco,
                            imagem = "",
                            categoria = "",
                            ativo = true
                        ),
                        quantidade = quantidade,
                        addedAt = 0L
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }

            val totalPrice = try {
                doc.get<Double>("totalPrice")
            } catch (e: Exception) {
                try { doc.get<Long>("totalPrice").toDouble() } catch (e2: Exception) { 0.0 }
            }

            val createdAt = try {
                doc.get<Long>("createdAt")
            } catch (e: Exception) {
                try { doc.get<Double>("createdAt").toLong() } catch (e2: Exception) { 0L }
            }

            val paymentMap = try {
                doc.get<Map<String, Any>>("payment")
            } catch (e: Exception) {
                null
            }

            val order = Order(
                id = doc.id,
                userId = try { doc.get<String>("userId") } catch (e: Exception) { "" },
                items = items,
                totalPrice = totalPrice,
                status = try { OrderStatus.valueOf(doc.get<String>("status")) } catch (e: Exception) { OrderStatus.PENDING },
                createdAt = createdAt,
                paymentMethod = paymentMap?.get("method")?.toString() ?: "",
                paymentIntentId = paymentMap?.get("paymentIntentId")?.toString() ?: ""
            )

            Result.success(order)
        } catch (e: Exception) {
            println("FirebaseOrderRepo: Erro ao buscar pedido: ${e.message}")
            Result.failure(e)
        }
    }
}
