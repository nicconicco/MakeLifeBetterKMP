//
//  EventResult.swift
//  iosApp
//
//  Created by Carlos Nicolau Galves on 22/01/26.
//


enum EventResult {
    case idle
    case loading
    case success(events: [Event])
    case error(message: String)
}

enum EventSectionsResult {
    case idle
    case loading
    case success(sections: [EventSection])
    case error(message: String)
}