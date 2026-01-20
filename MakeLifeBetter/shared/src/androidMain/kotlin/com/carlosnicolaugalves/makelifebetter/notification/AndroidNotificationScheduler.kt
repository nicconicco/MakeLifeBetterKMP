package com.carlosnicolaugalves.makelifebetter.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.carlosnicolaugalves.makelifebetter.model.AppNotification

class AndroidNotificationScheduler private constructor() : NotificationScheduler {
    private var context: Context? = null

    companion object {
        const val CHANNEL_ID = "event_notifications"
        const val CHANNEL_NAME = "Notificacoes de Eventos"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_EVENT_ID = "event_id"

        private var instance: AndroidNotificationScheduler? = null

        fun getInstance(): AndroidNotificationScheduler {
            return instance ?: AndroidNotificationScheduler().also { instance = it }
        }

        fun init(context: Context) {
            getInstance().context = context.applicationContext
            getInstance().createNotificationChannel()
        }
    }

    private fun createNotificationChannel() {
        val ctx = context ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Lembretes de eventos que estao prestes a comecar"
                enableVibration(true)
            }
            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override suspend fun schedule(notification: AppNotification): Boolean {
        val ctx = context ?: return false

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(ctx, Class.forName("com.carlosnicolaugalves.makelifebetter.NotificationReceiver")).apply {
            putExtra(EXTRA_NOTIFICATION_ID, notification.id)
            putExtra(EXTRA_TITLE, notification.title)
            putExtra(EXTRA_MESSAGE, notification.message)
            putExtra(EXTRA_EVENT_ID, notification.eventId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notification.scheduledTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notification.scheduledTime,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notification.scheduledTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notification.scheduledTime,
                    pendingIntent
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun cancel(notificationId: String) {
        val ctx = context ?: return
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(ctx, Class.forName("com.carlosnicolaugalves.makelifebetter.NotificationReceiver"))
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    override suspend fun cancelAll() {
        // Android doesn't have a direct way to cancel all alarms
        // This would need to iterate through saved notification IDs
    }

    override suspend fun hasPermission(): Boolean {
        val ctx = context ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override suspend fun requestPermission(): Boolean {
        // Permission request needs to be handled by the Activity
        // This returns the current state; actual request is done in UI layer
        return hasPermission()
    }
}
