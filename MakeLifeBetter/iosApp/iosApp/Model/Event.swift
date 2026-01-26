//
//  Event.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import Foundation
import ComposeApp

struct Event: Identifiable, Hashable {
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

    init(from kotlinEvent: ComposeApp.Event) {
        self.id = kotlinEvent.id
        self.titulo = kotlinEvent.titulo
        self.subtitulo = kotlinEvent.subtitulo
        self.descricao = kotlinEvent.descricao
        self.hora = kotlinEvent.hora
        self.lugar = kotlinEvent.lugar
        self.categoria = kotlinEvent.categoria
    }
}

struct EventSection {
    var titulo: String
    var eventos: [Event]

    init(titulo: String, eventos: [Event]) {
        self.titulo = titulo
        self.eventos = eventos
    }

    init(from kotlinSection: ComposeApp.EventSection) {
        self.titulo = kotlinSection.titulo
        self.eventos = kotlinSection.eventos.map { Event(from: $0 as! ComposeApp.Event) }
    }
}