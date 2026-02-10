package com.carlosnicolaugalves.makelifebetter.theme

/**
 * Default theme repository for JVM/Desktop - returns default theme only
 * Firebase is not available on desktop, so we just use default colors
 */
class DefaultThemeRepository : RemoteThemeRepository {
    override suspend fun fetchTheme(): Result<ThemePalettes> {
        return Result.success(ThemeDefaults.palettes)
    }

    override fun getCachedTheme(): ThemePalettes {
        return ThemeDefaults.palettes
    }
}

actual fun createRemoteThemeRepository(): RemoteThemeRepository {
    return DefaultThemeRepository()
}
