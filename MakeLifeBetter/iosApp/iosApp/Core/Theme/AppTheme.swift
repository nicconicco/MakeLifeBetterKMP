import SwiftUI
import UIKit

/// Theme palette matching Android's RemoteTheme structure.
/// Firestore document schema: app_config/theme
struct ThemePalette {
    let primary: Color
    let primaryContainer: Color
    let secondary: Color
    let secondaryContainer: Color
    let tertiary: Color
    let tertiaryContainer: Color
    let background: Color
    let surface: Color
    let surfaceVariant: Color
    let outline: Color
    let error: Color
    let errorContainer: Color

    var onPrimary: Color { contentColor(for: primary) }
    var onPrimaryContainer: Color { contentColor(for: primaryContainer) }
    var onSecondary: Color { contentColor(for: secondary) }
    var onSecondaryContainer: Color { contentColor(for: secondaryContainer) }
    var onTertiary: Color { contentColor(for: tertiary) }
    var onTertiaryContainer: Color { contentColor(for: tertiaryContainer) }
    var onBackground: Color { contentColor(for: background) }
    var onSurface: Color { contentColor(for: surface) }
    var onSurfaceVariant: Color { contentColor(for: surfaceVariant) }
    var onError: Color { contentColor(for: error) }
    var onErrorContainer: Color { contentColor(for: errorContainer) }

    private func contentColor(for background: Color) -> Color {
        let luminance = background.luminance
        return luminance > 0.5 ? Color.black : Color.white
    }
}

struct ThemePalettes {
    let light: ThemePalette
    let dark: ThemePalette
}

/// Default theme matching Android's ThemeDefaults
enum ThemeDefaults {
    static let light = ThemePalette(
        primary: Color(hex: "#f2c200"),
        primaryContainer: Color(hex: "#f7d861"),
        secondary: Color(hex: "#1f3b34"),
        secondaryContainer: Color(hex: "#264841"),
        tertiary: Color(hex: "#5a4e6e"),
        tertiaryContainer: Color(hex: "#6b5a83"),
        background: Color(hex: "#f7f7f7"),
        surface: Color(hex: "#ffffff"),
        surfaceVariant: Color(hex: "#e6e6e6"),
        outline: Color(hex: "#d6d6d6"),
        error: Color(hex: "#f44336"),
        errorContainer: Color(hex: "#f44336").blend(with: Color(hex: "#ffffff"), ratio: 0.15)
    )

    static let dark = ThemePalette(
        primary: Color(hex: "#c8102e"),
        primaryContainer: Color(hex: "#8f0b20"),
        secondary: Color(hex: "#121212"),
        secondaryContainer: Color(hex: "#1c1c1c"),
        tertiary: Color(hex: "#2a2a2a"),
        tertiaryContainer: Color(hex: "#3a3a3a"),
        background: Color(hex: "#151515"),
        surface: Color(hex: "#1e1e1e"),
        surfaceVariant: Color(hex: "#2f2f2f"),
        outline: Color(hex: "#4a4a4a"),
        error: Color(hex: "#f44336"),
        errorContainer: Color(hex: "#f44336").blend(with: Color(hex: "#1e1e1e"), ratio: 0.35)
    )

    static let palettes = ThemePalettes(light: light, dark: dark)
}

// MARK: - Color Extensions

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3:
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }

    var luminance: Double {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0

        UIColor(self).getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        return 0.299 * Double(red) + 0.587 * Double(green) + 0.114 * Double(blue)
    }

    func blend(with other: Color, ratio: Double) -> Color {
        var r1: CGFloat = 0, g1: CGFloat = 0, b1: CGFloat = 0, a1: CGFloat = 0
        var r2: CGFloat = 0, g2: CGFloat = 0, b2: CGFloat = 0, a2: CGFloat = 0

        UIColor(self).getRed(&r1, green: &g1, blue: &b1, alpha: &a1)
        UIColor(other).getRed(&r2, green: &g2, blue: &b2, alpha: &a2)

        let r = r1 * ratio + r2 * (1 - ratio)
        let g = g1 * ratio + g2 * (1 - ratio)
        let b = b1 * ratio + b2 * (1 - ratio)

        return Color(
            .sRGB,
            red: min(1, max(0, r)),
            green: min(1, max(0, g)),
            blue: min(1, max(0, b))
        )
    }
}
