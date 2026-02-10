package com.carlosnicolaugalves.makelifebetter.theme

import android.util.Log
import androidx.compose.ui.graphics.Color
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseThemeRepository : RemoteThemeRepository {

    companion object {
        private const val TAG = "FirebaseThemeRepo"
        private const val COLLECTION = "app_config"
        private const val DOCUMENT = "theme"
    }

    private val firestore by lazy { Firebase.firestore }
    private var cachedPalettes: ThemePalettes = ThemeDefaults.palettes

    override suspend fun fetchTheme(): Result<ThemePalettes> {
        return try {
            val doc = firestore.collection(COLLECTION).document(DOCUMENT).get()

            if (doc.exists) {
                val lightOverrides = parsePaletteOverrides(doc.get<Map<String, Any>?>("light"))
                val darkOverrides = parsePaletteOverrides(doc.get<Map<String, Any>?>("dark"))

                cachedPalettes = ThemePalettes(
                    light = applyOverrides(ThemeDefaults.light, lightOverrides),
                    dark = applyOverrides(ThemeDefaults.dark, darkOverrides)
                )

                Log.d(TAG, "Theme loaded successfully from Firestore")
                Result.success(cachedPalettes)
            } else {
                Log.d(TAG, "Theme document not found, using defaults")
                Result.success(ThemeDefaults.palettes)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching theme: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getCachedTheme(): ThemePalettes {
        return cachedPalettes
    }

    private fun parsePaletteOverrides(map: Map<String, Any>?): PaletteOverrides {
        if (map == null) return PaletteOverrides()

        return PaletteOverrides(
            primary = colorFromMap(map, "primary"),
            primaryContainer = colorFromMap(map, "primaryContainer", "primaryLight"),
            secondary = colorFromMap(map, "secondary"),
            secondaryContainer = colorFromMap(map, "secondaryContainer", "secondaryLight"),
            tertiary = colorFromMap(map, "tertiary", "accent"),
            tertiaryContainer = colorFromMap(map, "tertiaryContainer", "accentLight"),
            background = colorFromMap(map, "background"),
            surface = colorFromMap(map, "surface"),
            surfaceVariant = colorFromMap(map, "surfaceVariant"),
            outline = colorFromMap(map, "outline"),
            error = colorFromMap(map, "error"),
            errorContainer = colorFromMap(map, "errorContainer")
        )
    }

    private fun colorFromMap(map: Map<String, Any>, vararg keys: String): Color? {
        for (key in keys) {
            val value = map[key] as? String
            if (value != null) {
                return parseHexColor(value)
            }
        }
        return null
    }

    private fun parseHexColor(hex: String): Color? {
        return try {
            val cleanHex = hex.removePrefix("#")
            when (cleanHex.length) {
                6 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
                8 -> Color(android.graphics.Color.parseColor("#$cleanHex"))
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing color: $hex")
            null
        }
    }

    private fun applyOverrides(base: ThemePalette, overrides: PaletteOverrides): ThemePalette {
        return ThemePalette(
            primary = overrides.primary ?: base.primary,
            primaryContainer = overrides.primaryContainer ?: base.primaryContainer,
            secondary = overrides.secondary ?: base.secondary,
            secondaryContainer = overrides.secondaryContainer ?: base.secondaryContainer,
            tertiary = overrides.tertiary ?: base.tertiary,
            tertiaryContainer = overrides.tertiaryContainer ?: base.tertiaryContainer,
            background = overrides.background ?: base.background,
            surface = overrides.surface ?: base.surface,
            surfaceVariant = overrides.surfaceVariant ?: base.surfaceVariant,
            outline = overrides.outline ?: base.outline,
            error = overrides.error ?: base.error,
            errorContainer = overrides.errorContainer ?: base.errorContainer
        )
    }

    private data class PaletteOverrides(
        val primary: Color? = null,
        val primaryContainer: Color? = null,
        val secondary: Color? = null,
        val secondaryContainer: Color? = null,
        val tertiary: Color? = null,
        val tertiaryContainer: Color? = null,
        val background: Color? = null,
        val surface: Color? = null,
        val surfaceVariant: Color? = null,
        val outline: Color? = null,
        val error: Color? = null,
        val errorContainer: Color? = null
    )
}

actual fun createRemoteThemeRepository(): RemoteThemeRepository {
    return FirebaseThemeRepository()
}
