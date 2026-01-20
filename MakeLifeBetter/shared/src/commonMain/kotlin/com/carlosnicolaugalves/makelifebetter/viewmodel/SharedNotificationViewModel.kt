package com.carlosnicolaugalves.makelifebetter.viewmodel

import com.carlosnicolaugalves.makelifebetter.model.AppNotification
import com.carlosnicolaugalves.makelifebetter.model.Event
import com.carlosnicolaugalves.makelifebetter.notification.NotificationScheduler
import com.carlosnicolaugalves.makelifebetter.notification.createNotificationScheduler
import com.carlosnicolaugalves.makelifebetter.util.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SharedNotificationViewModel(
    private val scheduler: NotificationScheduler = createNotificationScheduler()
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _permissionState = MutableStateFlow(false)
    val permissionState: StateFlow<Boolean> = _permissionState.asStateFlow()

    private val _scheduledCount = MutableStateFlow(0)
    val scheduledCount: StateFlow<Int> = _scheduledCount.asStateFlow()

    init {
        checkPermission()
    }

    private fun checkPermission() {
        viewModelScope.launch {
            _permissionState.value = scheduler.hasPermission()
        }
    }

    fun requestPermission() {
        viewModelScope.launch {
            _permissionState.value = scheduler.requestPermission()
        }
    }

    /**
     * Schedules notifications for all schedulable events.
     * Called when events are loaded.
     */
    fun scheduleNotificationsForEvents(events: List<Event>) {
        viewModelScope.launch {
            if (!scheduler.hasPermission()) {
                _permissionState.value = false
                return@launch
            }

            // Cancel existing scheduled notifications
            scheduler.cancelAll()

            val scheduledNotifications = mutableListOf<AppNotification>()

            events.filter { TimeUtils.isSchedulable(it.hora) }
                .forEach { event ->
                    val notificationTime = TimeUtils.calculateNotificationTime(event.hora)
                    val eventTime = TimeUtils.getEventTimeMillis(event.hora)

                    if (notificationTime != null && eventTime != null) {
                        val notification = AppNotification(
                            id = "notif_${event.id}",
                            eventId = event.id,
                            title = "Em breve: ${event.titulo}",
                            message = "Comeca em 5 minutos - ${event.lugar}",
                            scheduledTime = notificationTime,
                            eventTime = eventTime,
                            createdAt = Clock.System.now().toEpochMilliseconds()
                        )

                        if (scheduler.schedule(notification)) {
                            scheduledNotifications.add(notification)
                        }
                    }
                }

            _notifications.value = scheduledNotifications.sortedBy { it.scheduledTime }
            _scheduledCount.value = scheduledNotifications.size
        }
    }

    fun addFiredNotification(notification: AppNotification) {
        viewModelScope.launch {
            val updated = _notifications.value.map {
                if (it.id == notification.id) it.copy(isFired = true) else it
            }
            _notifications.value = updated
        }
    }

    fun dismissNotification(notificationId: String) {
        viewModelScope.launch {
            scheduler.cancel(notificationId)
            _notifications.value = _notifications.value.filter { it.id != notificationId }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            _notifications.value = _notifications.value.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            scheduler.cancelAll()
            _notifications.value = emptyList()
            _scheduledCount.value = 0
        }
    }

    fun observeNotifications(callback: (List<AppNotification>) -> Unit): Job {
        return notifications.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun observePermissionState(callback: (Boolean) -> Unit): Job {
        return permissionState.onEach { callback(it) }.launchIn(viewModelScope)
    }

    fun clear() {
        viewModelScope.launch {
            // Cleanup if needed
        }
    }
}
