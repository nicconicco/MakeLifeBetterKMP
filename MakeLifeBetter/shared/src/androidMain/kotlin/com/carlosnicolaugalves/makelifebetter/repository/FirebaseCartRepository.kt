package com.carlosnicolaugalves.makelifebetter.repository

import android.util.Log
import com.carlosnicolaugalves.makelifebetter.model.Address
import com.carlosnicolaugalves.makelifebetter.model.Cart
import com.carlosnicolaugalves.makelifebetter.model.CartItem
import com.carlosnicolaugalves.makelifebetter.model.Order
import com.carlosnicolaugalves.makelifebetter.model.OrderStatus
import com.carlosnicolaugalves.makelifebetter.model.PaymentInfo
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
                    Log.e("FirebaseCartRepo", "Erro ao parsear item: ${e.message}")
                    null
                }
            }

            Result.success(Cart(items = items))
        } catch (e: Exception) {
            Log.e("FirebaseCartRepo", "Erro ao buscar carrinho: ${e.message}")
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
                    descricao = doc.get<String>("descricao"),
                    preco = doc.get<Double>("preco"),
                    imagem = doc.get<String>("imagem"),
                    categoria = doc.get<String>("categoria"),
                    ativo = doc.get<Boolean?>("ativo") ?: true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
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
            Log.e("FirebaseCartRepo", "Erro ao adicionar ao carrinho: ${e.message}")
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
            Log.e("FirebaseCartRepo", "Erro ao atualizar quantidade: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(userId: String, itemId: String): Result<Cart> {
        return try {
            cartCollection(userId).document(itemId).delete()
            getCart(userId)
        } catch (e: Exception) {
            Log.e("FirebaseCartRepo", "Erro ao remover do carrinho: ${e.message}")
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
            Log.e("FirebaseCartRepo", "Erro ao limpar carrinho: ${e.message}")
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
            Log.e("FirebaseCartRepo", "Erro ao finalizar pedido: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun checkoutWithInfo(userId: String, address: Address, payment: PaymentInfo): Result<Order> {
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
                    "createdAt" to order.createdAt,
                    "address" to hashMapOf(
                        "street" to address.street,
                        "number" to address.number,
                        "complement" to address.complement,
                        "neighborhood" to address.neighborhood,
                        "city" to address.city,
                        "state" to address.state,
                        "zipCode" to address.zipCode
                    ),
                    "payment" to hashMapOf(
                        "cardLastDigits" to payment.cardNumber.takeLast(4),
                        "cardHolder" to payment.cardHolder
                    )
                )
            )

            clearCart(userId)

            Result.success(order)
        } catch (e: Exception) {
            Log.e("FirebaseCartRepo", "Erro ao finalizar pedido: ${e.message}")
            Result.failure(e)
        }
    }
}
