package com.carlosnicolaugalves.makelifebetter.util

import kotlinx.datetime.*
import kotlin.time.Clock

object TimeUtils {
    private val TIME_REGEX = Regex("^([01]?[0-9]|2[0-3]):([0-5][0-9])$")

    /**
     * Parses "HH:mm" format. Returns null if not parseable.
     */
    fun parseTime(hora: String): Pair<Int, Int>? {
        val match = TIME_REGEX.matchEntire(hora.trim()) ?: return null
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        return hour to minute
    }

    /**
     * Returns true if the hora field is in schedulable HH:mm format.
     */
    fun isSchedulable(hora: String): Boolean = parseTime(hora) != null

    /**
     * Calculates notification trigger time (5 minutes before event).
     * Returns epoch millis or null if event time has passed.
     */
    fun calculateNotificationTime(
        hora: String,
        minutesBefore: Int = 5
    ): Long? {
        val (hour, minute) = parseTime(hora) ?: return null

        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val eventDateTime = LocalDateTime(today, LocalTime(hour, minute))
        val eventInstant = eventDateTime.toInstant(TimeZone.currentSystemDefault())

        val notificationInstant = eventInstant.minus(minutesBefore, DateTimeUnit.MINUTE)

        // Only schedule if notification time is in the future
        if (notificationInstant <= now) return null

        return notificationInstant.toEpochMilliseconds()
    }

    /**
     * Returns event time in epoch millis for today.
     */
    fun getEventTimeMillis(hora: String): Long? {
        val (hour, minute) = parseTime(hora) ?: return null

        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val eventDateTime = LocalDateTime(today, LocalTime(hour, minute))
        return eventDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    /**
     * Formats epoch millis to relative time string.
     */
    fun formatRelativeTime(epochMillis: Long): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val diffMinutes = (now - epochMillis) / 60000

        return when {
            diffMinutes < 0 -> "Em ${-diffMinutes} minutos"
            diffMinutes < 1 -> "Agora"
            diffMinutes < 60 -> "$diffMinutes minutos atras"
            diffMinutes < 1440 -> "${diffMinutes / 60} horas atras"
            else -> "${diffMinutes / 1440} dias atras"
        }
    }

    /**
     * Formats epoch millis to HH:mm string.
     */
    fun formatTime(epochMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }
}
