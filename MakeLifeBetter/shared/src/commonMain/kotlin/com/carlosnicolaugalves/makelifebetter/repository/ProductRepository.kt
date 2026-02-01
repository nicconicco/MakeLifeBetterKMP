package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product?>
    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>>
    suspend fun getCategories(): Result<List<ProductCategory>>
}
