package com.carlosnicolaugalves.makelifebetter.theme

/**
 * Default theme repository for JS - returns default theme only
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
