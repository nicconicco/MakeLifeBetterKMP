package com.carlosnicolaugalves.makelifebetter.model

data class Event(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val descricao: String,
    val hora: String,
    val lugar: String,
    val categoria: String = ""
)

data class EventSection(
    val titulo: String,
    val eventos: List<Event>
)
