package com.carlosnicolaugalves.makelifebetter.notification

actual fun createNotificationScheduler(): NotificationScheduler =
    AndroidNotificationScheduler.getInstance()
