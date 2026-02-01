package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Product
import com.carlosnicolaugalves.makelifebetter.model.ProductCategory

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product?>
    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>>
    suspend fun getCategories(): Result<List<ProductCategory>>
}

fun getSampleCategories(): List<ProductCategory> {
    return listOf(
        ProductCategory(id = "1", nome = "Camisetas", ordem = 1),
        ProductCategory(id = "2", nome = "Canecas", ordem = 2),
        ProductCategory(id = "3", nome = "Acessórios", ordem = 3),
        ProductCategory(id = "4", nome = "Adesivos", ordem = 4)
    )
}

fun getSampleProducts(): List<Product> {
    return listOf(
        Product(
            id = "1",
            nome = "Camiseta Dev Life",
            subtitulo = "100% Algodão",
            descricao = "Camiseta confortável para desenvolvedores que amam código. Estampa exclusiva com design moderno.",
            preco = 79.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=Camiseta+Dev",
            categoriaId = "1"
        ),
        Product(
            id = "2",
            nome = "Camiseta Kotlin Lover",
            subtitulo = "Edição Limitada",
            descricao = "Para os amantes de Kotlin. Design exclusivo com o logo da linguagem.",
            preco = 89.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=Kotlin+Shirt",
            categoriaId = "1"
        ),
        Product(
            id = "3",
            nome = "Caneca Coffee & Code",
            subtitulo = "350ml",
            descricao = "Caneca perfeita para suas sessões de coding. Mantém o café quente por mais tempo.",
            preco = 49.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=Caneca+Code",
            categoriaId = "2"
        ),
        Product(
            id = "4",
            nome = "Caneca Debug Mode",
            subtitulo = "Cerâmica Premium",
            descricao = "Para aqueles momentos de debugging intenso. Capacidade de 400ml.",
            preco = 54.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=Debug+Mug",
            categoriaId = "2"
        ),
        Product(
            id = "5",
            nome = "Mousepad Extended",
            subtitulo = "80x30cm",
            descricao = "Mousepad grande para sua estação de trabalho. Superfície suave e base antiderrapante.",
            preco = 69.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=Mousepad",
            categoriaId = "3"
        ),
        Product(
            id = "6",
            nome = "Chaveiro USB-C",
            subtitulo = "16GB",
            descricao = "Pendrive compacto em formato de chaveiro. Perfeito para carregar seus projetos.",
            preco = 39.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=USB+Key",
            categoriaId = "3"
        ),
        Product(
            id = "7",
            nome = "Pack Adesivos Dev",
            subtitulo = "10 unidades",
            descricao = "Pack com 10 adesivos variados sobre programação. Perfeitos para seu notebook.",
            preco = 24.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=Stickers",
            categoriaId = "4"
        ),
        Product(
            id = "8",
            nome = "Adesivo Holográfico KMP",
            subtitulo = "Edição Especial",
            descricao = "Adesivo holográfico exclusivo de Kotlin Multiplatform. Brilha em diferentes ângulos.",
            preco = 14.90,
            imageUrl = "https://via.placeholder.com/300x300.png?text=KMP+Sticker",
            categoriaId = "4"
        )
    )
}
