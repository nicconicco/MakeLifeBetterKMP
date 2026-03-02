package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.CartItem
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.OrderItemData
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
                    // Tentar obter items como lista de OrderItemData
                    val itemsData = try {
                        doc.get<List<OrderItemData>>("items")
                    } catch (e: Exception) {
                        emptyList()
                    }

                    val items = itemsData.map { itemData ->
                        CartItem(
                            id = itemData.productId,
                            productId = itemData.productId,
                            product = Product(
                                id = itemData.productId,
                                nome = itemData.productName,
                                descricao = "",
                                preco = itemData.preco,
                                imagem = "",
                                categoria = "",
                                ativo = true
                            ),
                            quantidade = itemData.quantidade,
                            addedAt = 0L
                        )
                    }

                    val totalPrice = doc.get<Double>("totalPrice")
                    val createdAt = doc.get<Long>("createdAt")
                    val statusStr = doc.get<String>("status")
                    val orderUserId = doc.get<String>("userId")

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
                    val paymentMethod = paymentMap?.get("method") as? String ?: ""
                    val paymentIntentId = paymentMap?.get("paymentIntentId") as? String ?: ""

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
                    null
                }
            }.sortedByDescending { it.createdAt }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val doc = ordersCollection.document(orderId).get()

            if (!doc.exists) {
                return Result.failure(Exception("Pedido nao encontrado"))
            }

            val itemsData = doc.get<List<Map<String, Any>>>("items")
            val items = itemsData.map { itemMap ->
                val productId = itemMap["productId"] as? String ?: ""
                val productName = itemMap["productName"] as? String ?: ""
                val quantidade = (itemMap["quantidade"] as? Long)?.toInt() ?: 1
                val preco = (itemMap["preco"] as? Double) ?: 0.0

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

            val paymentMap = try {
                doc.get<Map<String, Any>>("payment")
            } catch (e: Exception) {
                null
            }

            val order = Order(
                id = doc.id,
                userId = doc.get<String>("userId"),
                items = items,
                totalPrice = doc.get<Double>("totalPrice"),
                status = OrderStatus.valueOf(doc.get<String>("status")),
                createdAt = doc.get<Long>("createdAt"),
                paymentMethod = paymentMap?.get("method") as? String ?: "",
                paymentIntentId = paymentMap?.get("paymentIntentId") as? String ?: ""
            )

            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
