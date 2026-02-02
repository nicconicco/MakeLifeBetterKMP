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
                        descricao = doc.get<String>("descricao"),
                        preco = doc.get<Double>("preco"),
                        imagem = doc.get<String>("imagem"),
                        categoria = doc.get<String>("categoria"),
                        ativo = ativo
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(products)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao carregar produtos: ${e.message}"))
        }
    }

    override suspend fun getProductById(id: String): Result<Product?> {
        return try {
            val doc = productsCollection.document(id).get()

            if (doc.exists) {
                val product = Product(
                    id = doc.id,
                    nome = doc.get<String>("nome"),
                    descricao = doc.get<String>("descricao"),
                    preco = doc.get<Double>("preco"),
                    imagem = doc.get<String>("imagem"),
                    categoria = doc.get<String>("categoria"),
                    ativo = doc.get<Boolean?>("ativo") ?: true
                )
                Result.success(product)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar produto: ${e.message}"))
        }
    }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection
                .where { "categoria" equalTo categoryId }
                .get()

            val products = querySnapshot.documents.mapNotNull { doc ->
                try {
                    val ativo = doc.get<Boolean?>("ativo") ?: true
                    if (!ativo) return@mapNotNull null

                    Product(
                        id = doc.id,
                        nome = doc.get<String>("nome"),
                        descricao = doc.get<String>("descricao"),
                        preco = doc.get<Double>("preco"),
                        imagem = doc.get<String>("imagem"),
                        categoria = doc.get<String>("categoria"),
                        ativo = ativo
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(products)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar produtos: ${e.message}"))
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

            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao carregar categorias: ${e.message}"))
        }
    }
}
