package com.carlosnicolaugalves.makelifebetter.notification

import com.carlosnicolaugalves.makelifebetter.model.AppNotification

interface NotificationScheduler {
    /**
     * Schedules a local notification.
     * Returns true if scheduled successfully.
     */
    suspend fun schedule(notification: AppNotification): Boolean

    /**
     * Cancels a scheduled notification by ID.
     */
    suspend fun cancel(notificationId: String)

    /**
     * Cancels all scheduled notifications.
     */
    suspend fun cancelAll()

    /**
     * Checks if notification permissions are granted.
     */
    suspend fun hasPermission(): Boolean

    /**
     * Requests notification permission.
     * Returns true if granted.
     */
    suspend fun requestPermission(): Boolean
}
