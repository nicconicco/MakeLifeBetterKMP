package com.carlosnicolaugalves.makelifebetter.notification

import com.carlosnicolaugalves.makelifebetter.model.AppNotification
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
class IOSNotificationScheduler : NotificationScheduler {

    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun schedule(notification: AppNotification): Boolean {
        if (!hasPermission()) return false

        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.message)
            setSound(UNNotificationSound.defaultSound())
        }

        val triggerDate = NSDate.dateWithTimeIntervalSince1970(
            notification.scheduledTime / 1000.0
        )

        val calendar = NSCalendar.currentCalendar
        val dateComponents = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                    NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond,
            fromDate = triggerDate
        )

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            notification.id,
            content = content,
            trigger = trigger
        )

        return suspendCoroutine { cont ->
            center.addNotificationRequest(request) { error ->
                cont.resume(error == null)
            }
        }
    }

    override suspend fun cancel(notificationId: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId))
    }

    override suspend fun cancelAll() {
        center.removeAllPendingNotificationRequests()
    }

    override suspend fun hasPermission(): Boolean {
        return suspendCoroutine { cont ->
            center.getNotificationSettingsWithCompletionHandler { settings ->
                cont.resume(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
            }
        }
    }

    override suspend fun requestPermission(): Boolean {
        return suspendCoroutine { cont ->
            center.requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            ) { granted, _ ->
                cont.resume(granted)
            }
        }
    }
}
