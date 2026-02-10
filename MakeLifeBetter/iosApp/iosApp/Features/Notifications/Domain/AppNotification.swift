//
//  AppNotification.swift
//  iosApp
//

import Foundation
import ComposeApp

struct AppNotification: Identifiable {
    var id: String
    var eventId: String
    var title: String
    var message: String
    var scheduledTime: Int64
    var eventTime: Int64
    var createdAt: Int64
    var isRead: Bool
    var isFired: Bool

    init(id: String, eventId: String, title: String, message: String, scheduledTime: Int64, eventTime: Int64, createdAt: Int64, isRead: Bool = false, isFired: Bool = false) {
        self.id = id
        self.eventId = eventId
        self.title = title
        self.message = message
        self.scheduledTime = scheduledTime
        self.eventTime = eventTime
        self.createdAt = createdAt
        self.isRead = isRead
        self.isFired = isFired
    }

    init(from kotlinNotification: ComposeApp.AppNotification) {
        self.id = kotlinNotification.id
        self.eventId = kotlinNotification.eventId
        self.title = kotlinNotification.title
        self.message = kotlinNotification.message
        self.scheduledTime = kotlinNotification.scheduledTime
        self.eventTime = kotlinNotification.eventTime
        self.createdAt = kotlinNotification.createdAt
        self.isRead = kotlinNotification.isRead
        self.isFired = kotlinNotification.isFired
    }
}
