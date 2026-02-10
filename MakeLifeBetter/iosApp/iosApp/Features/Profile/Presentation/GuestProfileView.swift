import SwiftUI

struct GuestProfileView: View {
    let strings: AppStrings
    let onLoginClick: () -> Void
    let onBackClick: () -> Void
    var theme: ThemePalette = ThemeDefaults.light

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                Spacer().frame(height: 16)

                VStack(spacing: 12) {
                    Image(systemName: "lock.fill")
                        .font(.system(size: 32))
                        .foregroundColor(theme.primary)

                    Text(strings.acessoRestritoTitulo)
                        .font(.title2)
                        .bold()

                    Text(strings.acessoRestritoMensagem)
                        .multilineTextAlignment(.center)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity)
                .padding(24)
                .background(theme.surface)
                .cornerRadius(16)
                .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                Button(action: onLoginClick) {
                    Text(strings.entrar)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.borderedProminent)

                Button(action: onBackClick) {
                    Text(strings.voltar)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                }
                .buttonStyle(.bordered)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 12)
        }
        .background(theme.background)
    }
}

#Preview {
    GuestProfileView(
        strings: Traducoes.portugues,
        onLoginClick: {},
        onBackClick: {}
    )
}
