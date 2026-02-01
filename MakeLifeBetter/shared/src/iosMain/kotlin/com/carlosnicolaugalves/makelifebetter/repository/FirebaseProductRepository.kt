package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseProductRepository : ProductRepository {

    private val firestore by lazy { Firebase.firestore }
    private val productsCollection by lazy { firestore.collection("produtos") }
    private val categoriesCollection by lazy { firestore.collection("categorias") }

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection.get()

            val products = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val ativo = doc.get<Boolean?>("ativo") ?: true
                    if (!ativo) return@mapNotNull null

                    Product(
                        id = doc.id,
                        nome = doc.get<String>("nome"),
                        subtitulo = doc.get<String>("subtitulo"),
                        descricao = doc.get<String>("descricao"),
                        preco = doc.get<Double>("preco"),
                        imageUrl = doc.get<String>("imageUrl"),
                        categoriaId = doc.get<String>("categoriaId"),
                        ativo = ativo
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (products.isNotEmpty()) {
                Result.success(products)
            } else {
                Result.success(getSampleProducts())
            }
        } catch (e: Exception) {
            Result.success(getSampleProducts())
        }
    }

    override suspend fun getProductById(id: String): Result<Product?> {
        return try {
            val doc = productsCollection.document(id).get()

            if (doc.exists) {
                val product = Product(
                    id = doc.id,
                    nome = doc.get<String>("nome"),
                    subtitulo = doc.get<String>("subtitulo"),
                    descricao = doc.get<String>("descricao"),
                    preco = doc.get<Double>("preco"),
                    imageUrl = doc.get<String>("imageUrl"),
                    categoriaId = doc.get<String>("categoriaId"),
                    ativo = doc.get<Boolean?>("ativo") ?: true
                )
                Result.success(product)
            } else {
                Result.success(getSampleProducts().find { it.id == id })
            }
        } catch (e: Exception) {
            Result.success(getSampleProducts().find { it.id == id })
        }
    }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection
                .where { "categoriaId" equalTo categoryId }
                .get()

            val products = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val ativo = doc.get<Boolean?>("ativo") ?: true
                    if (!ativo) return@mapNotNull null

                    Product(
                        id = doc.id,
                        nome = doc.get<String>("nome"),
                        subtitulo = doc.get<String>("subtitulo"),
                        descricao = doc.get<String>("descricao"),
                        preco = doc.get<Double>("preco"),
                        imageUrl = doc.get<String>("imageUrl"),
                        categoriaId = doc.get<String>("categoriaId"),
                        ativo = ativo
                    )
                } catch (e: Exception) {
                    null
                }
            }

            if (products.isNotEmpty()) {
                Result.success(products)
            } else {
                Result.success(getSampleProducts().filter { it.categoriaId == categoryId })
            }
        } catch (e: Exception) {
            Result.success(getSampleProducts().filter { it.categoriaId == categoryId })
        }
    }

    override suspend fun getCategories(): Result<List<ProductCategory>> {
        return try {
            val querySnapshot = categoriesCollection.get()

            val categories = querySnapshot.documents.mapNotNull { doc ->
                try {
                    ProductCategory(
                        id = doc.id,
                        nome = doc.get<String>("nome"),
                        ordem = doc.get<Long?>("ordem")?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedBy { it.ordem }

            if (categories.isNotEmpty()) {
                Result.success(categories)
            } else {
                Result.success(getSampleCategories())
            }
        } catch (e: Exception) {
            Result.success(getSampleCategories())
        }
    }
}
