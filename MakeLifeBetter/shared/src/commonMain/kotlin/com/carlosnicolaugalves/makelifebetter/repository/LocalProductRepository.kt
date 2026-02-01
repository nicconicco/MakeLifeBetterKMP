package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory

class LocalProductRepository : ProductRepository {

    override suspend fun getProducts(): Result<List<Product>> {
        return Result.success(emptyList())
    }

    override suspend fun getProductById(id: String): Result<Product?> {
        return Result.success(null)
    }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return Result.success(emptyList())
    }

    override suspend fun getCategories(): Result<List<ProductCategory>> {
        return Result.success(emptyList())
    }
}
