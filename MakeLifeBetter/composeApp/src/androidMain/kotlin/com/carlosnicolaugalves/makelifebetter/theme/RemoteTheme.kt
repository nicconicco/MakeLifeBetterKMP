package com.carlosnicolaugalves.makelifebetter.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

/**
 * Remote theme loader (Android only).
 *
 * Firestore document schema:
 * app_config/theme
 * {
 *   light: {
 *     primary: "#f2c200",
 *     secondary: "#1f3b34",
 *     tertiary: "#5a4e6e",
 *     background: "#f7f7f7",
 *     surface: "#ffffff",
 *     surfaceVariant: "#e6e6e6",
 *     outline: "#d6d6d6",
 *     error: "#f44336",
 *     primaryContainer: "#f7d861",
 *     secondaryContainer: "#264841",
 *     tertiaryContainer: "#6b5a83"
 *   },
 *   dark: {
 *     primary: "#c8102e",
 *     secondary: "#121212",
 *     tertiary: "#2a2a2a",
 *     background: "#151515",
 *     surface: "#1e1e1e",
 *     surfaceVariant: "#2f2f2f",
 *     outline: "#4a4a4a",
 *     error: "#f44336",
 *     primaryContainer: "#8f0b20",
 *     secondaryContainer: "#1c1c1c",
 *     tertiaryContainer: "#3a3a3a"
 *   }
 * }
 */
@Composable
fun rememberRemoteThemePalettes(): State<ThemePalettes> {
    val state = remember { mutableStateOf(ThemeDefaults.palettes) }
    val repository = remember { ThemeConfigRepository() }

    LaunchedEffect(Unit) {
        val remote = repository.fetchThemePalettes()
        if (remote != null) {
            state.value = remote
        }
    }

    return state
}

data class ThemePalettes(
    val light: ThemePalette,
    val dark: ThemePalette
)

data class ThemePalette(
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val tertiary: Color,
    val tertiaryContainer: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val error: Color,
    val errorContainer: Color
)

private data class ThemePaletteOverrides(
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

object ThemeDefaults {
    private val light = ThemePalette(
        primary = color("#f2c200"),
        primaryContainer = color("#f7d861"),
        secondary = color("#1f3b34"),
        secondaryContainer = color("#264841"),
        tertiary = color("#5a4e6e"),
        tertiaryContainer = color("#6b5a83"),
        background = color("#f7f7f7"),
        surface = color("#ffffff"),
        surfaceVariant = color("#e6e6e6"),
        outline = color("#d6d6d6"),
        error = color("#f44336"),
        errorContainer = blend(color("#f44336"), color("#ffffff"), 0.15f)
    )

    private val dark = ThemePalette(
        primary = color("#c8102e"),
        primaryContainer = color("#8f0b20"),
        secondary = color("#121212"),
        secondaryContainer = color("#1c1c1c"),
        tertiary = color("#2a2a2a"),
        tertiaryContainer = color("#3a3a3a"),
        background = color("#151515"),
        surface = color("#1e1e1e"),
        surfaceVariant = color("#2f2f2f"),
        outline = color("#4a4a4a"),
        error = color("#f44336"),
        errorContainer = blend(color("#f44336"), color("#1e1e1e"), 0.35f)
    )

    val palettes = ThemePalettes(light = light, dark = dark)
}

class ThemeConfigRepository {
    private val firestore by lazy { Firebase.firestore }

    suspend fun fetchThemePalettes(): ThemePalettes? = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("app_config").document("theme").get()
            if (!doc.exists) return@withContext null

            val lightOverrides = parsePaletteOverrides(doc.get<Any?>("light"))
            val darkOverrides = parsePaletteOverrides(doc.get<Any?>("dark"))

            ThemePalettes(
                light = applyOverrides(ThemeDefaults.palettes.light, lightOverrides),
                dark = applyOverrides(ThemeDefaults.palettes.dark, darkOverrides)
            )
        } catch (_: Exception) {
            null
        }
    }
}

private fun parsePaletteOverrides(raw: Any?): ThemePaletteOverrides {
    val map = raw as? Map<*, *> ?: return ThemePaletteOverrides()
    fun colorFrom(vararg keys: String): Color? {
        for (key in keys) {
            val value = map[key]?.toString()
            if (value != null) {
                val color = parseHexColor(value)
                if (color != null) return color
            }
        }
        return null
    }

    return ThemePaletteOverrides(
        primary = colorFrom("primary"),
        primaryContainer = colorFrom("primaryContainer", "primaryLight"),
        secondary = colorFrom("secondary"),
        secondaryContainer = colorFrom("secondaryContainer", "secondaryLight"),
        tertiary = colorFrom("tertiary", "accent"),
        tertiaryContainer = colorFrom("tertiaryContainer", "accentLight"),
        background = colorFrom("background"),
        surface = colorFrom("surface"),
        surfaceVariant = colorFrom("surfaceVariant"),
        outline = colorFrom("outline"),
        error = colorFrom("error"),
        errorContainer = colorFrom("errorContainer")
    )
}

private fun applyOverrides(base: ThemePalette, overrides: ThemePaletteOverrides): ThemePalette {
    val errorContainer = overrides.errorContainer
        ?: base.errorContainer
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
        errorContainer = errorContainer
    )
}

fun ThemePalette.toColorScheme(isDark: Boolean): androidx.compose.material3.ColorScheme {
    val onPrimary = contentColorFor(primary)
    val onPrimaryContainer = contentColorFor(primaryContainer)
    val onSecondary = contentColorFor(secondary)
    val onSecondaryContainer = contentColorFor(secondaryContainer)
    val onTertiary = contentColorFor(tertiary)
    val onTertiaryContainer = contentColorFor(tertiaryContainer)
    val onBackground = contentColorFor(background)
    val onSurface = contentColorFor(surface)
    val onSurfaceVariant = contentColorFor(surfaceVariant)
    val onError = contentColorFor(error)
    val onErrorContainer = contentColorFor(errorContainer)

    val outlineVariant = blend(outline, surfaceVariant, 0.5f)
    val surfaceTint = primary
    val inverseSurface = if (isDark) color("#e0e0e0") else color("#121212")
    val inverseOnSurface = contentColorFor(inverseSurface)
    val inversePrimary = if (isDark) primaryContainer else primary
    val scrim = Color(0x66000000)

    val surfaceDim = if (isDark) blend(surface, background, 0.85f) else blend(surface, background, 0.92f)
    val surfaceBright = if (isDark) blend(surface, color("#ffffff"), 0.2f) else surface
    val surfaceContainerLowest = if (isDark) blend(surface, background, 0.7f) else surface
    val surfaceContainerLow = blend(surface, background, if (isDark) 0.75f else 0.98f)
    val surfaceContainer = blend(surface, background, if (isDark) 0.8f else 0.96f)
    val surfaceContainerHigh = blend(surface, background, if (isDark) 0.85f else 0.94f)
    val surfaceContainerHighest = blend(surface, background, if (isDark) 0.9f else 0.92f)

    return androidx.compose.material3.ColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = surfaceTint,
        outline = outline,
        outlineVariant = outlineVariant,
        scrim = scrim,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        inversePrimary = inversePrimary,
        surfaceDim = surfaceDim,
        surfaceBright = surfaceBright,
        surfaceContainerLowest = surfaceContainerLowest,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest
    )
}

private fun contentColorFor(background: Color): Color {
    return if (background.luminance() > 0.5f) Color(0xFF000000) else Color(0xFFFFFFFF)
}

private fun parseHexColor(hex: String): Color? {
    val clean = hex.trim().removePrefix("#")
    return try {
        when (clean.length) {
            6 -> Color(0xFF000000 or clean.toLong(16))
            8 -> Color(clean.toLong(16))
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

private fun color(hex: String): Color = parseHexColor(hex) ?: Color(0xFF000000)

private fun blend(foreground: Color, background: Color, ratio: Float): Color {
    val r = clamp(foreground.red * ratio + background.red * (1f - ratio))
    val g = clamp(foreground.green * ratio + background.green * (1f - ratio))
    val b = clamp(foreground.blue * ratio + background.blue * (1f - ratio))
    return Color(r, g, b, 1f)
}

private fun clamp(value: Float): Float = min(1f, max(0f, value))
