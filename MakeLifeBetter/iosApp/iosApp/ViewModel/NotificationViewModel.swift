//
//  NotificationViewModel.swift
//  iosApp
//

import Foundation
import Observation
import ComposeApp

@Observable
class NotificationViewModel {

    var notifications: [AppNotification] = []
    var hasPermission: Bool = false
    var scheduledCount: Int = 0

    private let sharedViewModel: SharedNotificationViewModelWrapper

    init() {
        sharedViewModel = SharedNotificationViewModelWrapper()
        setupObservers()
    }

    private func setupObservers() {
        sharedViewModel.observeNotifications { [weak self] kotlinNotifications in
            DispatchQueue.main.async {
                self?.notifications = kotlinNotifications.map { AppNotification(from: $0 as! ComposeApp.AppNotification) }
            }
        }

        sharedViewModel.observePermissionState { [weak self] granted in
            DispatchQueue.main.async {
                self?.hasPermission = granted.boolValue
            }
        }

        sharedViewModel.observeScheduledCount { [weak self] count in
            DispatchQueue.main.async {
                self?.scheduledCount = Int(count)
            }
        }
    }

    // MARK: - Actions

    func refreshPermissionState() {
        sharedViewModel.refreshPermissionState()
    }

    func onPermissionResult(granted: Bool) {
        sharedViewModel.onPermissionResult(granted: granted)
    }

    func dismissNotification(id: String) {
        sharedViewModel.dismissNotification(notificationId: id)
    }

    func markAsRead(id: String) {
        sharedViewModel.markAsRead(notificationId: id)
    }

    deinit {
        sharedViewModel.clear()
    }
}
