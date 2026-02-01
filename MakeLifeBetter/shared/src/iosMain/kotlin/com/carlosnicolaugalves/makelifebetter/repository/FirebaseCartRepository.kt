package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Cart
import com.carlosnicolaugalves.makelifebetter.model.CartItem
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.OrderStatus
import com.carlosnicolaugalves.makelifebetter.model.Product
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlin.time.Clock
import kotlin.random.Random

class FirebaseCartRepository : CartRepository {

    private val firestore by lazy { Firebase.firestore }

    private fun cartCollection(userId: String) = firestore.collection("carrinho").document(userId).collection("items")
    private val ordersCollection by lazy { firestore.collection("pedidos") }

    override suspend fun getCart(userId: String): Result<Cart> {
        return try {
            val querySnapshot = cartCollection(userId).get()

            val items = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val productId = doc.get<String>("productId")
                    val productResult = getProductFromFirebase(productId)

                    productResult?.let { product ->
                        CartItem(
                            id = doc.id,
                            productId = productId,
                            product = product,
                            quantidade = doc.get<Long>("quantidade").toInt(),
                            addedAt = doc.get<Long>("addedAt")
                        )
                    }
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(Cart(items = items))
        } catch (e: Exception) {
            Result.success(Cart())
        }
    }

    private suspend fun getProductFromFirebase(productId: String): Product? {
        return try {
            val doc = firestore.collection("produtos").document(productId).get()
            if (doc.exists) {
                Product(
                    id = doc.id,
                    nome = doc.get<String>("nome"),
                    subtitulo = doc.get<String>("subtitulo"),
                    descricao = doc.get<String>("descricao"),
                    preco = doc.get<Double>("preco"),
                    imageUrl = doc.get<String>("imageUrl"),
                    categoriaId = doc.get<String>("categoriaId"),
                    ativo = doc.get<Boolean?>("ativo") ?: true
                )
            } else {
                getSampleProducts().find { it.id == productId }
            }
        } catch (e: Exception) {
            getSampleProducts().find { it.id == productId }
        }
    }

    override suspend fun addToCart(userId: String, product: Product, quantidade: Int): Result<Cart> {
        return try {
            val existingQuery = cartCollection(userId)
                .where { "productId" equalTo product.id }
                .get()

            if (existingQuery.documents.isNotEmpty()) {
                val existingDoc = existingQuery.documents.first()
                val currentQty = existingDoc.get<Long>("quantidade").toInt()
                cartCollection(userId).document(existingDoc.id).update(
                    "quantidade" to (currentQty + quantidade)
                )
            } else {
                cartCollection(userId).add(
                    hashMapOf(
                        "productId" to product.id,
                        "quantidade" to quantidade,
                        "addedAt" to Clock.System.now().toEpochMilliseconds()
                    )
                )
            }

            getCart(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuantity(userId: String, itemId: String, quantidade: Int): Result<Cart> {
        return try {
            if (quantidade <= 0) {
                cartCollection(userId).document(itemId).delete()
            } else {
                cartCollection(userId).document(itemId).update(
                    "quantidade" to quantidade
                )
            }
            getCart(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(userId: String, itemId: String): Result<Cart> {
        return try {
            cartCollection(userId).document(itemId).delete()
            getCart(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val items = cartCollection(userId).get()
            items.documents.forEach { doc ->
                cartCollection(userId).document(doc.id).delete()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkout(userId: String): Result<Order> {
        return try {
            val cartResult = getCart(userId)
            val cart = cartResult.getOrNull() ?: return Result.failure(Exception("Erro ao obter carrinho"))

            if (cart.items.isEmpty()) {
                return Result.failure(Exception("Carrinho vazio"))
            }

            val orderId = "ORD-${Random.nextLong(100000, 999999)}"
            val order = Order(
                id = orderId,
                userId = userId,
                items = cart.items,
                totalPrice = cart.totalPrice,
                status = OrderStatus.CONFIRMED,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )

            ordersCollection.document(orderId).set(
                hashMapOf(
                    "userId" to order.userId,
                    "items" to order.items.map { item ->
                        hashMapOf(
                            "productId" to item.productId,
                            "productName" to item.product.nome,
                            "quantidade" to item.quantidade,
                            "preco" to item.product.preco
                        )
                    },
                    "totalPrice" to order.totalPrice,
                    "status" to order.status.name,
                    "createdAt" to order.createdAt
                )
            )

            clearCart(userId)

            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
