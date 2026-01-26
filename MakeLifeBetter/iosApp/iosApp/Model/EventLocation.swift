//
//  EventLocation.swift
//  iosApp
//

import Foundation
import ComposeApp

struct EventLocation {
    var id: String
    var name: String
    var address: String
    var city: String
    var latitude: Double
    var longitude: Double
    var contacts: [EventContact]

    init(id: String, name: String, address: String, city: String, latitude: Double, longitude: Double, contacts: [EventContact] = []) {
        self.id = id
        self.name = name
        self.address = address
        self.city = city
        self.latitude = latitude
        self.longitude = longitude
        self.contacts = contacts
    }

    init(from kotlinLocation: ComposeApp.EventLocation) {
        self.id = kotlinLocation.id
        self.name = kotlinLocation.name
        self.address = kotlinLocation.address
        self.city = kotlinLocation.city
        self.latitude = kotlinLocation.latitude
        self.longitude = kotlinLocation.longitude
        self.contacts = kotlinLocation.contacts.map { EventContact(from: $0 as! ComposeApp.EventContact) }
    }
}

struct EventContact {
    var id: String
    var name: String
    var phone: String

    init(id: String, name: String, phone: String) {
        self.id = id
        self.name = name
        self.phone = phone
    }

    init(from kotlinContact: ComposeApp.EventContact) {
        self.id = kotlinContact.id
        self.name = kotlinContact.name
        self.phone = kotlinContact.phone
    }
}
