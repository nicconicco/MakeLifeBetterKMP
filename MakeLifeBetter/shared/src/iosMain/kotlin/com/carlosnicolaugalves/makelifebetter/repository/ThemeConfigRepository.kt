package com.carlosnicolaugalves.makelifebetter.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ThemeColors(
    // Light
    val lightPrimary: Long,
    val lightSecondary: Long,
    val lightTertiary: Long,
    val lightBackground: Long,
    val lightSurface: Long,
    val lightSurfaceVariant: Long,
    val lightOutline: Long,
    val lightError: Long,
    // Dark
    val darkPrimary: Long,
    val darkSecondary: Long,
    val darkTertiary: Long,
    val darkBackground: Long,
    val darkSurface: Long,
    val darkSurfaceVariant: Long,
    val darkOutline: Long,
    val darkError: Long
)

object ThemeColorDefaults {
    val colors = ThemeColors(
        // Light
        lightPrimary = 0xFFF2C200,
        lightSecondary = 0xFF1F3B34,
        lightTertiary = 0xFF5A4E6E,
        lightBackground = 0xFFF7F7F7,
        lightSurface = 0xFFFFFFFF,
        lightSurfaceVariant = 0xFFE6E6E6,
        lightOutline = 0xFFD6D6D6,
        lightError = 0xFFF44336,
        // Dark
        darkPrimary = 0xFFC8102E,
        darkSecondary = 0xFF121212,
        darkTertiary = 0xFF2A2A2A,
        darkBackground = 0xFF151515,
        darkSurface = 0xFF1E1E1E,
        darkSurfaceVariant = 0xFF2F2F2F,
        darkOutline = 0xFF4A4A4A,
        darkError = 0xFFF44336
    )
}

class ThemeConfigRepository {
    private val firestore by lazy { Firebase.firestore }

    suspend fun fetchThemeColors(): ThemeColors = withContext(Dispatchers.Default) {
        try {
            val doc = firestore.collection("app_config").document("theme").get()
            if (!doc.exists) return@withContext ThemeColorDefaults.colors

            val lightMap = doc.get<Any?>("light") as? Map<*, *>
            val darkMap = doc.get<Any?>("dark") as? Map<*, *>

            val defaults = ThemeColorDefaults.colors

            ThemeColors(
                lightPrimary = parseHex(lightMap, "primary", defaults.lightPrimary),
                lightSecondary = parseHex(lightMap, "secondary", defaults.lightSecondary),
                lightTertiary = parseHex(lightMap, "tertiary", defaults.lightTertiary),
                lightBackground = parseHex(lightMap, "background", defaults.lightBackground),
                lightSurface = parseHex(lightMap, "surface", defaults.lightSurface),
                lightSurfaceVariant = parseHex(lightMap, "surfaceVariant", defaults.lightSurfaceVariant),
                lightOutline = parseHex(lightMap, "outline", defaults.lightOutline),
                lightError = parseHex(lightMap, "error", defaults.lightError),
                darkPrimary = parseHex(darkMap, "primary", defaults.darkPrimary),
                darkSecondary = parseHex(darkMap, "secondary", defaults.darkSecondary),
                darkTertiary = parseHex(darkMap, "tertiary", defaults.darkTertiary),
                darkBackground = parseHex(darkMap, "background", defaults.darkBackground),
                darkSurface = parseHex(darkMap, "surface", defaults.darkSurface),
                darkSurfaceVariant = parseHex(darkMap, "surfaceVariant", defaults.darkSurfaceVariant),
                darkOutline = parseHex(darkMap, "outline", defaults.darkOutline),
                darkError = parseHex(darkMap, "error", defaults.darkError)
            )
        } catch (_: Exception) {
            ThemeColorDefaults.colors
        }
    }

    private fun parseHex(map: Map<*, *>?, key: String, default: Long): Long {
        val value = map?.get(key)?.toString() ?: return default
        val clean = value.trim().removePrefix("#")
        return try {
            when (clean.length) {
                6 -> 0xFF000000L or clean.toLong(16)
                8 -> clean.toLong(16)
                else -> default
            }
        } catch (_: Exception) {
            default
        }
    }
}
