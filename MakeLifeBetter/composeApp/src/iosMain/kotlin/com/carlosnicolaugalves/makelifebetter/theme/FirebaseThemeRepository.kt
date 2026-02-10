package com.carlosnicolaugalves.makelifebetter.theme

import androidx.compose.ui.graphics.Color
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirebaseThemeRepository : RemoteThemeRepository {

    companion object {
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

                Result.success(cachedPalettes)
            } else {
                Result.success(ThemeDefaults.palettes)
            }
        } catch (e: Exception) {
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
                6 -> {
                    val r = cleanHex.substring(0, 2).toInt(16)
                    val g = cleanHex.substring(2, 4).toInt(16)
                    val b = cleanHex.substring(4, 6).toInt(16)
                    Color(r, g, b)
                }
                8 -> {
                    val a = cleanHex.substring(0, 2).toInt(16)
                    val r = cleanHex.substring(2, 4).toInt(16)
                    val g = cleanHex.substring(4, 6).toInt(16)
                    val b = cleanHex.substring(6, 8).toInt(16)
                    Color(r, g, b, a)
                }
                else -> null
            }
        } catch (e: Exception) {
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
