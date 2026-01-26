package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.AppNotification
import com.carlosnicolaugalves.makelifebetter.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SharedNotificationViewModelWrapper {
    private val viewModel = SharedNotificationViewModel()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var notificationsObserver: Job? = null
    private var permissionObserver: Job? = null
    private var scheduledCountObserver: Job? = null

    // MARK: - Observe Notifications

    fun observeNotifications(callback: (List<AppNotification>) -> Unit) {
        notificationsObserver?.cancel()
        notificationsObserver = viewModel.notifications
            .onEach { callback(it) }
            .launchIn(scope)
    }

    // MARK: - Observe Permission State

    fun observePermissionState(callback: (Boolean) -> Unit) {
        permissionObserver?.cancel()
        permissionObserver = viewModel.permissionState
            .onEach { callback(it) }
            .launchIn(scope)
    }

    // MARK: - Observe Scheduled Count

    fun observeScheduledCount(callback: (Int) -> Unit) {
        scheduledCountObserver?.cancel()
        scheduledCountObserver = viewModel.scheduledCount
            .onEach { callback(it) }
            .launchIn(scope)
    }

    // MARK: - Actions

    fun refreshPermissionState() {
        viewModel.refreshPermissionState()
    }

    fun onPermissionResult(granted: Boolean) {
        viewModel.onPermissionResult(granted)
    }

    fun dismissNotification(notificationId: String) {
        viewModel.dismissNotification(notificationId)
    }

    fun markAsRead(notificationId: String) {
        viewModel.markAsRead(notificationId)
    }

    fun scheduleNotificationsForEvents(events: List<Event>) {
        viewModel.scheduleNotificationsForEvents(events)
    }

    fun clearAllNotifications() {
        viewModel.clearAllNotifications()
    }

    // MARK: - Cleanup

    fun clear() {
        notificationsObserver?.cancel()
        permissionObserver?.cancel()
        scheduledCountObserver?.cancel()
        viewModel.clear()
    }
}
