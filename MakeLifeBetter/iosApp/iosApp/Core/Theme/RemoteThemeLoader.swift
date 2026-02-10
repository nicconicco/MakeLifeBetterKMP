import SwiftUI
import FirebaseFirestore

/// Remote theme loader matching Android's RemoteTheme.kt
/// Fetches theme palettes from Firestore document: app_config/theme
@Observable
class RemoteThemeLoader {
    private(set) var palettes: ThemePalettes = ThemeDefaults.palettes
    private(set) var isLoading: Bool = true

    private let db = Firestore.firestore()

    init() {
        fetchTheme()
    }

    func fetchTheme() {
        isLoading = true

        db.collection("app_config").document("theme").getDocument { [weak self] snapshot, error in
            guard let self = self else { return }

            defer { self.isLoading = false }

            guard let data = snapshot?.data(), error == nil else {
                return
            }

            let lightOverrides = self.parsePaletteOverrides(data["light"])
            let darkOverrides = self.parsePaletteOverrides(data["dark"])

            self.palettes = ThemePalettes(
                light: self.applyOverrides(ThemeDefaults.light, overrides: lightOverrides),
                dark: self.applyOverrides(ThemeDefaults.dark, overrides: darkOverrides)
            )
        }
    }

    private func parsePaletteOverrides(_ raw: Any?) -> PaletteOverrides {
        guard let map = raw as? [String: Any] else {
            return PaletteOverrides()
        }

        func colorFrom(_ keys: String...) -> Color? {
            for key in keys {
                if let value = map[key] as? String {
                    return Color(hex: value)
                }
            }
            return nil
        }

        return PaletteOverrides(
            primary: colorFrom("primary"),
            primaryContainer: colorFrom("primaryContainer", "primaryLight"),
            secondary: colorFrom("secondary"),
            secondaryContainer: colorFrom("secondaryContainer", "secondaryLight"),
            tertiary: colorFrom("tertiary", "accent"),
            tertiaryContainer: colorFrom("tertiaryContainer", "accentLight"),
            background: colorFrom("background"),
            surface: colorFrom("surface"),
            surfaceVariant: colorFrom("surfaceVariant"),
            outline: colorFrom("outline"),
            error: colorFrom("error"),
            errorContainer: colorFrom("errorContainer")
        )
    }

    private func applyOverrides(_ base: ThemePalette, overrides: PaletteOverrides) -> ThemePalette {
        ThemePalette(
            primary: overrides.primary ?? base.primary,
            primaryContainer: overrides.primaryContainer ?? base.primaryContainer,
            secondary: overrides.secondary ?? base.secondary,
            secondaryContainer: overrides.secondaryContainer ?? base.secondaryContainer,
            tertiary: overrides.tertiary ?? base.tertiary,
            tertiaryContainer: overrides.tertiaryContainer ?? base.tertiaryContainer,
            background: overrides.background ?? base.background,
            surface: overrides.surface ?? base.surface,
            surfaceVariant: overrides.surfaceVariant ?? base.surfaceVariant,
            outline: overrides.outline ?? base.outline,
            error: overrides.error ?? base.error,
            errorContainer: overrides.errorContainer ?? base.errorContainer
        )
    }
}

private struct PaletteOverrides {
    var primary: Color? = nil
    var primaryContainer: Color? = nil
    var secondary: Color? = nil
    var secondaryContainer: Color? = nil
    var tertiary: Color? = nil
    var tertiaryContainer: Color? = nil
    var background: Color? = nil
    var surface: Color? = nil
    var surfaceVariant: Color? = nil
    var outline: Color? = nil
    var error: Color? = nil
    var errorContainer: Color? = nil
}
