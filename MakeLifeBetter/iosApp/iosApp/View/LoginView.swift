import SwiftUI

struct LoginView: View {
    @Binding var currentScreen: AppScreen
    @Binding var termosAceitos: Bool
    @Binding var idioma: Idioma
    let strings: AppStrings

    @State private var username: String = ""
    @State private var password: String = ""
    @State private var showError: Bool = false
    @State private var errorMessage: String = ""

    @StateObject private var viewModel = LoginViewModel()

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
                    viewModel.login(username: username, password: password)
                }) {
                    if case .loading = viewModel.loginState {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(8)
                    } else {
                        Text(strings.entrar)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                }
                .disabled(viewModel.loginState == .loading)

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
        .alert(strings.erro, isPresented: $showError) {
            Button("OK", role: .cancel) { }
        } message: {
            Text(errorMessage)
        }
        .onChange(of: viewModel.loginState) { oldState, newState in
            handleLoginState(newState)
        }
    }

    private func handleLoginState(_ state: AuthResultState) {
        switch state {
        case .success(let user):
            print("Login bem sucedido: \(user.username)")
            // TODO: Navegar para tela principal
        case .error(let message):
            errorMessage = message
            showError = true
        default:
            break
        }
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
