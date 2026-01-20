package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.model.EventSection

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getEventsByCategory(categoria: String): Result<List<Event>>
    suspend fun getEventSections(): Result<List<EventSection>>
}

fun getSampleEvents(): List<Event> {
    return listOf(
        // O que tá rolando agora
        Event(
            id = "1",
            titulo = "Keynote: O Futuro da IA Generativa",
            subtitulo = "Palco Principal",
            descricao = "Descubra as últimas tendências em IA generativa e como elas estão transformando o desenvolvimento de software.",
            hora = "Agora",
            lugar = "Palco Principal",
            categoria = "agora"
        ),
        Event(
            id = "2",
            titulo = "Workshop: Compose Multiplatform",
            subtitulo = "Live Coding",
            descricao = "Aprenda a criar aplicativos multiplataforma com Compose Multiplatform em uma sessão prática.",
            hora = "Agora",
            lugar = "Sala 3B",
            categoria = "agora"
        ),
        Event(
            id = "3",
            titulo = "Espaço Networking",
            subtitulo = "Snacks liberados",
            descricao = "Conecte-se com outros desenvolvedores enquanto aproveita nossos snacks e bebidas.",
            hora = "Agora",
            lugar = "Área de Café",
            categoria = "agora"
        ),
        // Ainda vai rolar
        Event(
            id = "4",
            titulo = "Arquitetura Limpa em Escala",
            subtitulo = "Auditório Azul",
            descricao = "Como aplicar princípios de Clean Architecture em projetos de grande escala.",
            hora = "14:00",
            lugar = "Auditório Azul",
            categoria = "programado"
        ),
        Event(
            id = "5",
            titulo = "Painel: Carreira Internacional",
            subtitulo = "Palco Principal",
            descricao = "Desenvolvedores brasileiros compartilham suas experiências trabalhando no exterior.",
            hora = "15:30",
            lugar = "Palco Principal",
            categoria = "programado"
        ),
        Event(
            id = "6",
            titulo = "Happy Hour de Encerramento",
            subtitulo = "Rooftop",
            descricao = "Celebre o fim do evento com música, drinks e uma vista incrível da cidade.",
            hora = "18:00",
            lugar = "Rooftop",
            categoria = "programado"
        ),
        // Novidades
        Event(
            id = "7",
            titulo = "Lançamento da SDK v2.0",
            subtitulo = "Stand Principal",
            descricao = "Visite o stand e ganhe stickers exclusivos do lançamento.",
            hora = "Durante o evento",
            lugar = "Stand Principal",
            categoria = "novidade"
        ),
        Event(
            id = "8",
            titulo = "Hackathon Surpresa",
            subtitulo = "Recepção",
            descricao = "Inscrições abertas na recepção. Prêmios incríveis para os vencedores!",
            hora = "Inscrições abertas",
            lugar = "Recepção",
            categoria = "novidade"
        ),
        // Contatos
        Event(
            id = "9",
            titulo = "Suporte Técnico",
            subtitulo = "Ajuda disponível",
            descricao = "Precisa de ajuda com o App ou Wi-Fi? Nossa equipe está pronta para ajudar.",
            hora = "Disponível",
            lugar = "Balcão de Informações",
            categoria = "contato"
        ),
        Event(
            id = "10",
            titulo = "Discord da Comunidade",
            subtitulo = "Online",
            descricao = "Converse com outros devs agora mesmo no nosso servidor Discord.",
            hora = "24/7",
            lugar = "Online",
            categoria = "contato"
        ),
        // Cupons
        Event(
            id = "11",
            titulo = "Almoço Food Truck",
            subtitulo = "15% OFF",
            descricao = "Use o código BURGERTECH15 para ganhar 15% de desconto.",
            hora = "Válido hoje",
            lugar = "Food Trucks",
            categoria = "cupom"
        ),
        Event(
            id = "12",
            titulo = "Cursos Alura",
            subtitulo = "30% OFF",
            descricao = "30% de desconto na renovação anual. Código exclusivo para participantes.",
            hora = "Válido por 7 dias",
            lugar = "Online",
            categoria = "cupom"
        )
    )
}

fun getSampleEventSections(): List<EventSection> {
    val events = getSampleEvents()
    return listOf(
        EventSection(
            titulo = "O que tá rolando agora",
            eventos = events.filter { it.categoria == "agora" }
        ),
        EventSection(
            titulo = "Ainda vai rolar",
            eventos = events.filter { it.categoria == "programado" }
        ),
        EventSection(
            titulo = "Novidades",
            eventos = events.filter { it.categoria == "novidade" }
        ),
        EventSection(
            titulo = "Canais de Contato",
            eventos = events.filter { it.categoria == "contato" }
        ),
        EventSection(
            titulo = "Cupons",
            eventos = events.filter { it.categoria == "cupom" }
        )
    )
}
