import SwiftUI

struct LanguageView: View {
    @Binding var currentScreen: AppScreen
    @Binding var idioma: Idioma
    let strings: AppStrings

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(strings.selecioneIdioma)
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)

            Spacer()
                .frame(height: 24)

            Button(action: {
                idioma = .portugues
                currentScreen = .login
            }) {
                HStack {
                    Text(Idioma.portugues.bandeira)
                        .font(.system(size: 32))
                    Spacer()
                        .frame(width: 16)
                    Text(strings.portugues)
                        .font(.body)
                        .foregroundColor(.primary)
                    Spacer()
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(8)
            }

            Button(action: {
                idioma = .ingles
                currentScreen = .login
            }) {
                HStack {
                    Text(Idioma.ingles.bandeira)
                        .font(.system(size: 32))
                    Spacer()
                        .frame(width: 16)
                    Text(strings.ingles)
                        .font(.body)
                        .foregroundColor(.primary)
                    Spacer()
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(8)
            }

            Button(action: {
                idioma = .espanhol
                currentScreen = .login
            }) {
                HStack {
                    Text(Idioma.espanhol.bandeira)
                        .font(.system(size: 32))
                    Spacer()
                        .frame(width: 16)
                    Text(strings.espanhol)
                        .font(.body)
                        .foregroundColor(.primary)
                    Spacer()
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(8)
            }

            Spacer()

            Button(strings.voltar) {
                currentScreen = .login
            }
        }
        .padding(.horizontal, 32)
    }
}

#Preview {
    LanguageView(
        currentScreen: .constant(.idioma),
        idioma: .constant(.portugues),
        strings: Traducoes.portugues
    )
}
