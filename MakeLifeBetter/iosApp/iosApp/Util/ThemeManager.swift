import SwiftUI
import ComposeApp

@Observable
class ThemeManager {
    // Light colors
    private var lightPrimary: Color
    private var lightSecondary: Color
    private var lightTertiary: Color
    private var lightBackground: Color
    private var lightSurface: Color
    private var lightSurfaceVariant: Color
    private var lightOutline: Color
    private var lightError: Color

    // Dark colors
    private var darkPrimary: Color
    private var darkSecondary: Color
    private var darkTertiary: Color
    private var darkBackground: Color
    private var darkSurface: Color
    private var darkSurfaceVariant: Color
    private var darkOutline: Color
    private var darkError: Color

    private var isLoaded = false
    private let wrapper = ThemeConfigRepositoryWrapper()

    init() {
        let defaults = ThemeColorDefaults.shared.colors
        lightPrimary = Color(hex: defaults.lightPrimary)
        lightSecondary = Color(hex: defaults.lightSecondary)
        lightTertiary = Color(hex: defaults.lightTertiary)
        lightBackground = Color(hex: defaults.lightBackground)
        lightSurface = Color(hex: defaults.lightSurface)
        lightSurfaceVariant = Color(hex: defaults.lightSurfaceVariant)
        lightOutline = Color(hex: defaults.lightOutline)
        lightError = Color(hex: defaults.lightError)

        darkPrimary = Color(hex: defaults.darkPrimary)
        darkSecondary = Color(hex: defaults.darkSecondary)
        darkTertiary = Color(hex: defaults.darkTertiary)
        darkBackground = Color(hex: defaults.darkBackground)
        darkSurface = Color(hex: defaults.darkSurface)
        darkSurfaceVariant = Color(hex: defaults.darkSurfaceVariant)
        darkOutline = Color(hex: defaults.darkOutline)
        darkError = Color(hex: defaults.darkError)
    }

    // MARK: - Public computed properties (adapt to current color scheme)

    func primary(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkPrimary : lightPrimary
    }

    func secondary(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkSecondary : lightSecondary
    }

    func tertiary(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkTertiary : lightTertiary
    }

    func background(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkBackground : lightBackground
    }

    func surface(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkSurface : lightSurface
    }

    func surfaceVariant(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkSurfaceVariant : lightSurfaceVariant
    }

    func outline(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkOutline : lightOutline
    }

    func error(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkError : lightError
    }

    // MARK: - Load from remote

    func loadFromRemote() {
        guard !isLoaded else { return }
        isLoaded = true

        wrapper.fetchThemeColors(
            onSuccess: { [weak self] colors in
                guard let self else { return }
                self.lightPrimary = Color(hex: colors.lightPrimary)
                self.lightSecondary = Color(hex: colors.lightSecondary)
                self.lightTertiary = Color(hex: colors.lightTertiary)
                self.lightBackground = Color(hex: colors.lightBackground)
                self.lightSurface = Color(hex: colors.lightSurface)
                self.lightSurfaceVariant = Color(hex: colors.lightSurfaceVariant)
                self.lightOutline = Color(hex: colors.lightOutline)
                self.lightError = Color(hex: colors.lightError)

                self.darkPrimary = Color(hex: colors.darkPrimary)
                self.darkSecondary = Color(hex: colors.darkSecondary)
                self.darkTertiary = Color(hex: colors.darkTertiary)
                self.darkBackground = Color(hex: colors.darkBackground)
                self.darkSurface = Color(hex: colors.darkSurface)
                self.darkSurfaceVariant = Color(hex: colors.darkSurfaceVariant)
                self.darkOutline = Color(hex: colors.darkOutline)
                self.darkError = Color(hex: colors.darkError)
            },
            onError: { error in
                print("ThemeManager: Failed to load remote theme: \(error)")
            }
        )
    }

    deinit {
        wrapper.clear()
    }
}

// MARK: - Color hex extension

extension Color {
    init(hex: Int64) {
        let r = Double((hex >> 16) & 0xFF) / 255.0
        let g = Double((hex >> 8) & 0xFF) / 255.0
        let b = Double(hex & 0xFF) / 255.0
        let a = Double((hex >> 24) & 0xFF) / 255.0
        self.init(red: r, green: g, blue: b, opacity: a)
    }
}
