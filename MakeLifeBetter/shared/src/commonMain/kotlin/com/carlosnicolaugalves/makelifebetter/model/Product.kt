package com.carlosnicolaugalves.makelifebetter.model

data class Product(
    val id: String,
    val nome: String,
    val subtitulo: String,
    val descricao: String,
    val preco: Double,
    val imageUrl: String,
    val categoriaId: String,
    val ativo: Boolean = true
)

data class ProductCategory(
    val id: String,
    val nome: String,
    val ordem: Int = 0
)
