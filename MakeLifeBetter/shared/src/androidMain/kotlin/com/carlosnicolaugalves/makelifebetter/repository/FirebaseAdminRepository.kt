package com.carlosnicolaugalves.makelifebetter.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions

class FirebaseAdminRepository : AdminRepository {

    private val firestore by lazy { Firebase.firestore }
    private val functions by lazy { Firebase.functions }

    override suspend fun bootstrapFirstAdmin(): Result<String> {
        return try {
            val result = functions.httpsCallable("bootstrapFirstAdmin").invoke()
            val message = result.data<Map<String, String>>()["message"] ?: "Success"
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllEvents(): Result<Unit> {
        return try {
            val eventsCollection = firestore.collection("eventos")
            val snapshot = eventsCollection.get()

            snapshot.documents.forEach { doc ->
                eventsCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEventLocation(): Result<Unit> {
        return try {
            val locationCollection = firestore.collection("event_location")
            val snapshot = locationCollection.get()

            snapshot.documents.forEach { doc ->
                // Deletar subcoleção de contatos primeiro
                val contactsSnapshot = locationCollection
                    .document(doc.id)
                    .collection("contacts")
                    .get()

                contactsSnapshot.documents.forEach { contactDoc ->
                    locationCollection
                        .document(doc.id)
                        .collection("contacts")
                        .document(contactDoc.id)
                        .delete()
                }

                // Deletar documento principal
                locationCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllChatMessages(): Result<Unit> {
        return try {
            val chatCollection = firestore.collection("lista_geral")
            val snapshot = chatCollection.get()

            snapshot.documents.forEach { doc ->
                chatCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllQuestions(): Result<Unit> {
        return try {
            val questionsCollection = firestore.collection("duvidas")
            val snapshot = questionsCollection.get()

            snapshot.documents.forEach { doc ->
                // Deletar subcoleção de respostas primeiro
                val repliesSnapshot = questionsCollection
                    .document(doc.id)
                    .collection("respostas")
                    .get()

                repliesSnapshot.documents.forEach { replyDoc ->
                    questionsCollection
                        .document(doc.id)
                        .collection("respostas")
                        .document(replyDoc.id)
                        .delete()
                }

                // Deletar documento principal
                questionsCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllProducts(): Result<Unit> {
        return try {
            val productsCollection = firestore.collection("produtos")
            val snapshot = productsCollection.get()

            snapshot.documents.forEach { doc ->
                productsCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllCategories(): Result<Unit> {
        return try {
            val categoriesCollection = firestore.collection("categorias")
            val snapshot = categoriesCollection.get()

            snapshot.documents.forEach { doc ->
                categoriesCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllCarts(): Result<Unit> {
        return try {
            val cartsCollection = firestore.collection("carrinho")
            val snapshot = cartsCollection.get()

            snapshot.documents.forEach { doc ->
                // Deletar subcoleção de items primeiro
                val itemsSnapshot = cartsCollection
                    .document(doc.id)
                    .collection("items")
                    .get()

                itemsSnapshot.documents.forEach { itemDoc ->
                    cartsCollection
                        .document(doc.id)
                        .collection("items")
                        .document(itemDoc.id)
                        .delete()
                }

                cartsCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllOrders(): Result<Unit> {
        return try {
            val ordersCollection = firestore.collection("pedidos")
            val snapshot = ordersCollection.get()

            snapshot.documents.forEach { doc ->
                ordersCollection.document(doc.id).delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllData(): Result<Unit> {
        return try {
            val result = functions.httpsCallable("adminDeleteAllData").invoke()
            val message = result.data<Map<String, String>>()["message"] ?: "Success"
            println("deleteAllData via Cloud Function: $message")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun populateSampleEvents(): Result<Unit> {
        return try {
            val eventsCollection = firestore.collection("eventos")

            // Eventos de exemplo
            val sampleEvents = listOf(
                mapOf(
                    "titulo" to "Cerimonia de Abertura",
                    "subtitulo" to "Bem-vindos ao evento",
                    "descricao" to "Cerimonia oficial de abertura com discursos e apresentacoes especiais.",
                    "hora" to "09:00",
                    "lugar" to "Salao Principal",
                    "categoria" to "cerimonia"
                ),
                mapOf(
                    "titulo" to "Coffee Break",
                    "subtitulo" to "Pausa para cafe",
                    "descricao" to "Momento de networking e degustacao de cafe e lanches.",
                    "hora" to "10:30",
                    "lugar" to "Area de Convivencia",
                    "categoria" to "intervalo"
                ),
                mapOf(
                    "titulo" to "Palestra Principal",
                    "subtitulo" to "Tema especial do dia",
                    "descricao" to "Palestra inspiradora sobre inovacao e tecnologia.",
                    "hora" to "11:00",
                    "lugar" to "Auditorio",
                    "categoria" to "palestra"
                ),
                mapOf(
                    "titulo" to "Almoco",
                    "subtitulo" to "Refeicao",
                    "descricao" to "Almoco servido no restaurante do local.",
                    "hora" to "12:30",
                    "lugar" to "Restaurante",
                    "categoria" to "refeicao"
                ),
                mapOf(
                    "titulo" to "Workshop",
                    "subtitulo" to "Atividade pratica",
                    "descricao" to "Workshop interativo com atividades em grupo.",
                    "hora" to "14:00",
                    "lugar" to "Sala de Treinamento",
                    "categoria" to "workshop"
                ),
                mapOf(
                    "titulo" to "Encerramento",
                    "subtitulo" to "Despedida",
                    "descricao" to "Cerimonia de encerramento e agradecimentos.",
                    "hora" to "17:00",
                    "lugar" to "Salao Principal",
                    "categoria" to "cerimonia"
                )
            )

            sampleEvents.forEach { event ->
                eventsCollection.add(event)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun populateSampleEventLocation(): Result<Unit> {
        return try {
            val locationCollection = firestore.collection("event_location")

            // Criar documento de localizacao
            val locationDoc = locationCollection.document("main_location")
            locationDoc.set(
                mapOf(
                    "name" to "Centro de Convencoes",
                    "address" to "Av. Principal, 1000",
                    "city" to "Sao Paulo - SP",
                    "latitude" to -23.550520,
                    "longitude" to -46.633308
                )
            )

            // Adicionar contatos na subcoleção
            val contactsCollection = locationDoc.collection("contacts")

            val contacts = listOf(
                mapOf(
                    "name" to "Recepcao",
                    "phone" to "(11) 1234-5678"
                ),
                mapOf(
                    "name" to "Organizacao",
                    "phone" to "(11) 9876-5432"
                ),
                mapOf(
                    "name" to "Emergencia",
                    "phone" to "(11) 9999-9999"
                )
            )

            contacts.forEach { contact ->
                contactsCollection.add(contact)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun populateSampleCategories(): Result<Unit> {
        return try {
            val categoriesCollection = firestore.collection("categorias")

            val sampleCategories = listOf(
                mapOf(
                    "nome" to "Camisetas",
                    "ordem" to 1
                ),
                mapOf(
                    "nome" to "Canecas",
                    "ordem" to 2
                ),
                mapOf(
                    "nome" to "Acessorios",
                    "ordem" to 3
                ),
                mapOf(
                    "nome" to "Adesivos",
                    "ordem" to 4
                )
            )

            sampleCategories.forEach { category ->
                categoriesCollection.add(category)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun populateSampleProducts(): Result<Unit> {
        return try {
            // Primeiro, obter os IDs das categorias
            val categoriesSnapshot = firestore.collection("categorias").get()
            val categoryMap = mutableMapOf<String, String>()

            categoriesSnapshot.documents.forEach { doc ->
                val nome = doc.get<String?>("nome") ?: ""
                categoryMap[nome] = doc.id
            }

            // Se nao houver categorias, criar primeiro
            if (categoryMap.isEmpty()) {
                populateSampleCategories()
                val newSnapshot = firestore.collection("categorias").get()
                newSnapshot.documents.forEach { doc ->
                    val nome = doc.get<String?>("nome") ?: ""
                    categoryMap[nome] = doc.id
                }
            }

            val productsCollection = firestore.collection("produtos")

            val sampleProducts = listOf(
                mapOf(
                    "nome" to "Camiseta Dev Life",
                    "subtitulo" to "100% Algodao",
                    "descricao" to "Camiseta confortavel para desenvolvedores que amam codigo. Estampa exclusiva com design moderno.",
                    "preco" to 79.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=Camiseta+Dev",
                    "categoria" to (categoryMap["Camisetas"] ?: "1"),
                    "ativo" to true
                ),
                mapOf(
                    "nome" to "Camiseta Kotlin Lover",
                    "subtitulo" to "Edicao Limitada",
                    "descricao" to "Para os amantes de Kotlin. Design exclusivo com o logo da linguagem.",
                    "preco" to 89.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=Kotlin+Shirt",
                    "categoria" to (categoryMap["Camisetas"] ?: "1"),
                    "ativo" to true
                ),
                mapOf(
                    "nome" to "Caneca Coffee & Code",
                    "subtitulo" to "350ml",
                    "descricao" to "Caneca perfeita para suas sessoes de coding. Mantem o cafe quente por mais tempo.",
                    "preco" to 49.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=Caneca+Code",
                    "categoria" to (categoryMap["Canecas"] ?: "2"),
                    "ativo" to true
                ),
                mapOf(
                    "nome" to "Caneca Debug Mode",
                    "subtitulo" to "Ceramica Premium",
                    "descricao" to "Para aqueles momentos de debugging intenso. Capacidade de 400ml.",
                    "preco" to 54.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=Debug+Mug",
                    "categoria" to (categoryMap["Canecas"] ?: "2"),
                    "ativo" to true
                ),
                mapOf(
                    "nome" to "Mousepad Extended",
                    "subtitulo" to "80x30cm",
                    "descricao" to "Mousepad grande para sua estacao de trabalho. Superficie suave e base antiderrapante.",
                    "preco" to 69.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=Mousepad",
                    "categoria" to (categoryMap["Acessorios"] ?: "3"),
                    "ativo" to true
                ),
                mapOf(
                    "nome" to "Chaveiro USB-C",
                    "subtitulo" to "16GB",
                    "descricao" to "Pendrive compacto em formato de chaveiro. Perfeito para carregar seus projetos.",
                    "preco" to 39.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=USB+Key",
                    "categoria" to (categoryMap["Acessorios"] ?: "3"),
                    "ativo" to true
                ),
                mapOf(
                    "nome" to "Pack Adesivos Dev",
                    "subtitulo" to "10 unidades",
                    "descricao" to "Pack com 10 adesivos variados sobre programacao. Perfeitos para seu notebook.",
                    "preco" to 24.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=Stickers",
                    "categoria" to (categoryMap["Adesivos"] ?: "4"),
                    "ativo" to true
                ),
                mapOf(
                    "nome" to "Adesivo Holografico KMP",
                    "subtitulo" to "Edicao Especial",
                    "descricao" to "Adesivo holografico exclusivo de Kotlin Multiplatform. Brilha em diferentes angulos.",
                    "preco" to 14.90,
                    "imagem" to "https://via.placeholder.com/300x300.png?text=KMP+Sticker",
                    "categoria" to (categoryMap["Adesivos"] ?: "4"),
                    "ativo" to true
                )
            )

            sampleProducts.forEach { product ->
                productsCollection.add(product)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun populateAllSampleData(): Result<Unit> {
        return try {
            populateSampleEvents()
            populateSampleEventLocation()
            populateSampleCategories()
            populateSampleProducts()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadEvents(events: List<Map<String, String>>): Result<Int> {
        return try {
            var count = 0
            val eventsCollection = firestore.collection("eventos")

            events.forEach { event ->
                try {
                    eventsCollection.add(event)
                    count++
                } catch (e: Exception) {
                    // Ignora erros individuais
                }
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadLocation(location: Map<String, Any>): Result<Boolean> {
        return try {
            val locationDoc = firestore.collection("event_location").document("main_location")
            locationDoc.set(location)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadContacts(contacts: List<Map<String, String>>): Result<Int> {
        return try {
            var count = 0
            val contactsCollection = firestore
                .collection("event_location")
                .document("main_location")
                .collection("contacts")

            contacts.forEach { contact ->
                try {
                    contactsCollection.add(contact)
                    count++
                } catch (e: Exception) {
                    // Ignora erros individuais
                }
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadCategories(categories: List<Map<String, String>>): Result<Int> {
        return try {
            var count = 0
            val categoriesCollection = firestore.collection("categorias")

            categories.forEachIndexed { index, category ->
                try {
                    val categoryData = mapOf(
                        "nome" to (category["nome"] ?: ""),
                        "ordem" to (category["ordem"]?.toIntOrNull() ?: (index + 1))
                    )
                    categoriesCollection.add(categoryData)
                    count++
                } catch (e: Exception) {
                    // Ignora erros individuais
                }
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProducts(products: List<Map<String, String>>): Result<Int> {
        return try {
            var count = 0
            val productsCollection = firestore.collection("produtos")

            products.forEach { product ->
                try {
                    val productData = mapOf(
                        "nome" to (product["nome"] ?: ""),
                        "subtitulo" to (product["subtitulo"] ?: ""),
                        "descricao" to (product["descricao"] ?: ""),
                        "preco" to (product["preco"]?.replace(",", ".")?.toDoubleOrNull() ?: 0.0),
                        "imagem" to (product["imagem"] ?: ""),
                        "categoria" to (product["categoria"] ?: ""),
                        "ativo" to ((product["ativo"]?.lowercase() ?: "true") == "true")
                    )
                    productsCollection.add(productData)
                    count++
                } catch (e: Exception) {
                    // Ignora erros individuais
                }
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
