//
//  NavigationItem.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


import SwiftUI

enum NavigationItem: String, CaseIterable {
    case evento = "Event"
    case loja = "Store"
    case chat = "Chat"
    case mapa = "Map"
    case more = "More"

    var title: String {
        return self.rawValue
    }

    var icon: Image {
        switch self {
        case .evento:
            return Image(systemName: "calendar")
        case .loja:
            return Image(systemName: "cart")
        case .chat:
            return Image(systemName: "message")
        case .mapa:
            return Image(systemName: "map")
        case .more:
            return Image(systemName: "ellipsis")
        }
    }
}
