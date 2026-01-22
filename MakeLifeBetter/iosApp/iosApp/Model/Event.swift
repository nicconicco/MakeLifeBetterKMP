//
//  Event.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import Foundation

struct Event: Identifiable {
    var id: String
    var titulo: String
    var subtitulo: String
    var descricao: String
    var hora: String
    var lugar: String
    var categoria: String

    // Default value for the `categoria` property
    init(id: String, titulo: String, subtitulo: String, descricao: String, hora: String, lugar: String, categoria: String = "") {
        self.id = id
        self.titulo = titulo
        self.subtitulo = subtitulo
        self.descricao = descricao
        self.hora = hora
        self.lugar = lugar
        self.categoria = categoria
    }
}

struct EventSection {
    var titulo: String
    var eventos: [Event]

    init(titulo: String, eventos: [Event]) {
        self.titulo = titulo
        self.eventos = eventos
    }
}