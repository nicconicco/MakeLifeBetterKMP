package com.carlosnicolaugalves.makelifebetter.model

data class Product(
    val id: String,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val imagem: String,
    val categoria: String,
    val ativo: Boolean = true
)

data class ProductCategory(
    val id: String,
    val nome: String,
    val ordem: Int = 0
)
