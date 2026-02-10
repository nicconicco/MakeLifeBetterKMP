import SwiftUI

struct LoadingScreen: View {
    let strings: AppStrings
    var theme: ThemePalette = ThemeDefaults.light

    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Text(strings.appName)
                .font(.title2)
                .fontWeight(.semibold)
                .foregroundColor(theme.primary)
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle())
                .scaleEffect(1.2)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(theme.background)
    }
}

#Preview {
    LoadingScreen(strings: Traducoes.portugues)
}
