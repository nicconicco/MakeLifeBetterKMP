import SwiftUI

struct LoginView: View {
    @Environment(ThemeManager.self) private var themeManager
    @Environment(\.colorScheme) private var colorScheme
    @Binding var currentScreen: AppScreen
    @Binding var termosAceitos: Bool
    @Binding var idioma: Idioma
    let strings: AppStrings
    var viewModel: LoginViewModel

    @State private var username: String = ""
    @State private var password: String = ""
    @State private var showError: Bool = false
    @State private var errorMessage: String = ""

    private var isLoading: Bool {
        viewModel.loginState == .loading
    }

    private var canLogin: Bool {
        !username.trimmingCharacters(in: .whitespaces).isEmpty &&
        !password.isEmpty
    }

    var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(spacing: 16) {
                Spacer()

                Text(strings.appName)
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .foregroundColor(themeManager.primary(for: colorScheme))

                Spacer()
                    .frame(height: 32)

                TextField(strings.usuario, text: $username)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                    .keyboardType(.emailAddress)
                    .disabled(isLoading)

                SecureField(strings.senha, text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .disabled(isLoading)

                HStack {
                    Spacer()
                    Button(strings.esqueciSenha) {
                        currentScreen = .esqueciSenha
                    }
                    .font(.subheadline)
                    .disabled(isLoading)
                }

                Spacer()
                    .frame(height: 16)

                Button(action: {
                    viewModel.login(username: username, password: password)
                }) {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(themeManager.outline(for: colorScheme))
                            .cornerRadius(8)
                    } else {
                        Text(strings.entrar)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(canLogin ? themeManager.primary(for: colorScheme) : themeManager.outline(for: colorScheme))
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                }
                .disabled(!canLogin || isLoading)

                Button(strings.criarNovaConta) {
                    termosAceitos = false
                    currentScreen = .cadastro
                }
                .padding(.top, 8)
                .disabled(isLoading)

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
            currentScreen = .main
        case .error(let message):
            errorMessage = message
            showError = true
            viewModel.resetLoginState()
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
        strings: Traducoes.portugues,
        viewModel: LoginViewModel()
    )
    .environment(ThemeManager())
}
