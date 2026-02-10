import SwiftUI

struct TempPasswordView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    var theme: ThemePalette = ThemeDefaults.light

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(strings.senhaTempTitulo)
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(theme.primary)

            Spacer()
                .frame(height: 24)

            Text(strings.suaNovaSenha)
                .font(.body)

            Spacer()
                .frame(height: 24)

            Text(strings.useSenhaTemp)
                .font(.subheadline)
                .foregroundColor(theme.onSurfaceVariant)
                .multilineTextAlignment(.center)

            Spacer()

            Button(action: {
                currentScreen = .login
            }) {
                Text(strings.confirmar)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(theme.primary)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
        }
        .padding(.horizontal, 32)
    }
}

#Preview {
    TempPasswordView(
        currentScreen: .constant(.senhaTemp),
        strings: Traducoes.portugues
    )
}
