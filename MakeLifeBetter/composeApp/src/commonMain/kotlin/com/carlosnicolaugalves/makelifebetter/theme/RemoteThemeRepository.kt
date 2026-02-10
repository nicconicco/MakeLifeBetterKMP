package com.carlosnicolaugalves.makelifebetter.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Theme palette for Material3 ColorScheme
 * Matches the Firestore document schema: app_config/theme
 */
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

data class ThemePalettes(
    val light: ThemePalette,
    val dark: ThemePalette
)

/**
 * Default theme colors
 */
object ThemeDefaults {
    val light = ThemePalette(
        primary = Color(0xFFf2c200),
        primaryContainer = Color(0xFFf7d861),
        secondary = Color(0xFF1f3b34),
        secondaryContainer = Color(0xFF264841),
        tertiary = Color(0xFF5a4e6e),
        tertiaryContainer = Color(0xFF6b5a83),
        background = Color(0xFFf7f7f7),
        surface = Color(0xFFffffff),
        surfaceVariant = Color(0xFFe6e6e6),
        outline = Color(0xFFd6d6d6),
        error = Color(0xFFf44336),
        errorContainer = Color(0xFFffdad6)
    )

    val dark = ThemePalette(
        primary = Color(0xFFc8102e),
        primaryContainer = Color(0xFF8f0b20),
        secondary = Color(0xFF121212),
        secondaryContainer = Color(0xFF1c1c1c),
        tertiary = Color(0xFF2a2a2a),
        tertiaryContainer = Color(0xFF3a3a3a),
        background = Color(0xFF151515),
        surface = Color(0xFF1e1e1e),
        surfaceVariant = Color(0xFF2f2f2f),
        outline = Color(0xFF4a4a4a),
        error = Color(0xFFf44336),
        errorContainer = Color(0xFF93000a)
    )

    val palettes = ThemePalettes(light = light, dark = dark)
}

/**
 * Repository interface for fetching remote theme configuration
 */
interface RemoteThemeRepository {
    suspend fun fetchTheme(): Result<ThemePalettes>
    fun getCachedTheme(): ThemePalettes
}

/**
 * Factory function to create platform-specific RemoteThemeRepository
 */
expect fun createRemoteThemeRepository(): RemoteThemeRepository

/**
 * Helper to get content color (on* color) based on background luminance
 */
private fun contentColorFor(background: Color): Color {
    return if (background.luminance() > 0.5f) Color.Black else Color.White
}

/**
 * Convert ThemePalette to Material3 light ColorScheme
 */
fun ThemePalette.toLightColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = primary,
        onPrimary = contentColorFor(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = contentColorFor(primaryContainer),
        secondary = secondary,
        onSecondary = contentColorFor(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = contentColorFor(secondaryContainer),
        tertiary = tertiary,
        onTertiary = contentColorFor(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = contentColorFor(tertiaryContainer),
        background = background,
        onBackground = contentColorFor(background),
        surface = surface,
        onSurface = contentColorFor(surface),
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = contentColorFor(surfaceVariant),
        outline = outline,
        error = error,
        onError = contentColorFor(error),
        errorContainer = errorContainer,
        onErrorContainer = contentColorFor(errorContainer)
    )
}

/**
 * Convert ThemePalette to Material3 dark ColorScheme
 */
fun ThemePalette.toDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = primary,
        onPrimary = contentColorFor(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = contentColorFor(primaryContainer),
        secondary = secondary,
        onSecondary = contentColorFor(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = contentColorFor(secondaryContainer),
        tertiary = tertiary,
        onTertiary = contentColorFor(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = contentColorFor(tertiaryContainer),
        background = background,
        onBackground = contentColorFor(background),
        surface = surface,
        onSurface = contentColorFor(surface),
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = contentColorFor(surfaceVariant),
        outline = outline,
        error = error,
        onError = contentColorFor(error),
        errorContainer = errorContainer,
        onErrorContainer = contentColorFor(errorContainer)
    )
}
