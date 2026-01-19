import SwiftUI

struct LoginView: View {
    @Binding var currentScreen: AppScreen
    @Binding var termosAceitos: Bool
    @Binding var idioma: Idioma
    let strings: AppStrings

    @State private var username: String = ""
    @State private var password: String = ""

    var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(spacing: 16) {
                Spacer()

                Text(strings.appName)
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .foregroundColor(.blue)

                Spacer()
                    .frame(height: 32)

                TextField(strings.usuario, text: $username)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                    .keyboardType(.emailAddress)

                SecureField(strings.senha, text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())

                HStack {
                    Spacer()
                    Button(strings.esqueciSenha) {
                        currentScreen = .esqueciSenha
                    }
                    .font(.subheadline)
                }

                Spacer()
                    .frame(height: 16)

                Button(action: {
                    // TODO: Implementar logica de login
                }) {
                    Text(strings.entrar)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }

                Button(strings.criarNovaConta) {
                    termosAceitos = false
                    currentScreen = .cadastro
                }
                .padding(.top, 8)

                Spacer()
            }

            Button(action: {
                currentScreen = .idioma
            }) {
                Text(idioma.bandeira)
                    .font(.system(size: 32))
            }
            .padding(.top, 16)
        }
        .padding(.horizontal, 32)
    }
}

#Preview {
    LoginView(
        currentScreen: .constant(.login),
        termosAceitos: .constant(false),
        idioma: .constant(.portugues),
        strings: Traducoes.portugues
    )
}
