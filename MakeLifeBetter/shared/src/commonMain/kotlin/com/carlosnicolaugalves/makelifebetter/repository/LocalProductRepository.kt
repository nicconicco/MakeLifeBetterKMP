package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory

class LocalProductRepository : ProductRepository {

    override suspend fun getProducts(): Result<List<Product>> {
        return Result.success(getSampleProducts())
    }

    override suspend fun getProductById(id: String): Result<Product?> {
        val product = getSampleProducts().find { it.id == id }
        return Result.success(product)
    }

    override suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        val products = getSampleProducts().filter { it.categoriaId == categoryId }
        return Result.success(products)
    }

    override suspend fun getCategories(): Result<List<ProductCategory>> {
        return Result.success(getSampleCategories())
    }
}
