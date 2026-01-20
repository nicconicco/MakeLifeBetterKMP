package com.carlosnicolaugalves.makelifebetter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.carlosnicolaugalves.makelifebetter.notification.AndroidNotificationScheduler

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-initialize the notification scheduler
            AndroidNotificationScheduler.init(context)

            // Note: To properly reschedule notifications after boot,
            // you would need to persist scheduled notifications and reload them here.
            // For now, notifications will be rescheduled when the app is opened.
        }
    }
}
