import SwiftUI

struct TermsView: View {
    @Binding var currentScreen: AppScreen
    @Binding var termosAceitos: Bool
    let strings: AppStrings
    var theme: ThemePalette = ThemeDefaults.light

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(strings.termosCompromisso)
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(theme.primary)

            Spacer()
                .frame(height: 24)

            Text(strings.termosTexto)
                .font(.body)
                .multilineTextAlignment(.center)

            Spacer()

            Button(action: {
                termosAceitos = true
                currentScreen = .cadastro
            }) {
                Text(strings.concordar)
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
    TermsView(
        currentScreen: .constant(.termos),
        termosAceitos: .constant(false),
        strings: Traducoes.portugues
    )
}
