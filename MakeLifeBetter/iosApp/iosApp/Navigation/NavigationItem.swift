//
//  NavigationItem.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import SwiftUI

enum NavigationItem: String, CaseIterable {
    case evento = "Event"
    case mapa = "Map"
    case perfil = "Me"
    case chat = "Chat"
    case notificacoes = "Alarm"
    case contrate = "Contact"

    var title: String {
        return self.rawValue
    }

    var icon: Image {
        switch self {
        case .evento:
            return Image(systemName: "calendar")
        case .mapa:
            return Image(systemName: "map")
        case .perfil:
            return Image(systemName: "person")
        case .chat:
            return Image(systemName: "message")
        case .notificacoes:
            return Image(systemName: "bell")
        case .contrate:
            return Image(systemName: "briefcase")
        }
    }
}
