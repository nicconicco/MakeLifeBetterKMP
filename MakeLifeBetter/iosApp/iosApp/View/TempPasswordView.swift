import SwiftUI

struct TempPasswordView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(strings.senhaTempTitulo)
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(.blue)

            Spacer()
                .frame(height: 24)

            Text(strings.suaNovaSenha)
                .font(.body)

            Text("123456")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)

            Spacer()
                .frame(height: 24)

            Text(strings.useSenhaTemp)
                .font(.subheadline)
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)

            Spacer()

            Button(action: {
                currentScreen = .login
            }) {
                Text(strings.confirmar)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
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
