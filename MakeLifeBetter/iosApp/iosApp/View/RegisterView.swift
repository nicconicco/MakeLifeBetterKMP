import SwiftUI

struct RegisterView: View {
    @Binding var currentScreen: AppScreen
    @Binding var termosAceitos: Bool
    let strings: AppStrings
    @ObservedObject var viewModel: LoginViewModel

    @State private var nome: String = ""
    @State private var email: String = ""
    @State private var senha: String = ""
    @State private var confirmarSenha: String = ""
    @State private var showError: Bool = false
    @State private var errorMessage: String = ""
    @State private var showSuccess: Bool = false

    private var camposPreenchidos: Bool {
        !nome.trimmingCharacters(in: .whitespaces).isEmpty &&
        !email.trimmingCharacters(in: .whitespaces).isEmpty &&
        !senha.trimmingCharacters(in: .whitespaces).isEmpty &&
        !confirmarSenha.trimmingCharacters(in: .whitespaces).isEmpty
    }

    private var senhasConferem: Bool {
        senha == confirmarSenha
    }

    private var podeCadastrar: Bool {
        camposPreenchidos && termosAceitos && senhasConferem
    }

    private var isLoading: Bool {
        viewModel.registerState == .loading
    }

    var body: some View {
        ZStack {
            VStack(spacing: 16) {
                Spacer()

                Text(strings.criarConta)
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .foregroundColor(.blue)

                Spacer()
                    .frame(height: 24)

                TextField("\(strings.nome) *", text: $nome)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .disabled(isLoading)

                TextField("\(strings.email) *", text: $email)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .autocapitalization(.none)
                    .keyboardType(.emailAddress)
                    .disabled(isLoading)

                SecureField("\(strings.senha) *", text: $senha)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .disabled(isLoading)

                SecureField("\(strings.confirmarSenha) *", text: $confirmarSenha)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .disabled(isLoading)

                if !senha.isEmpty && !confirmarSenha.isEmpty && !senhasConferem {
                    Text(strings.senhasNaoCoincidem)
                        .font(.caption)
                        .foregroundColor(.red)
                }

                HStack {
                    Text(strings.camposObrigatorios)
                        .font(.caption)
                        .foregroundColor(.gray)
                    Spacer()
                }

                HStack {
                    Toggle("", isOn: .constant(termosAceitos))
                        .labelsHidden()
                        .fixedSize()
                        .disabled(true)

                    Text(strings.aceitoTermos)
                        .font(.subheadline)
                        .foregroundColor(.blue)
                        .underline()
                        .padding(.leading, 5)
                        .onTapGesture {
                            currentScreen = .termos
                        }
                }
                .padding(.vertical, 8)

                Spacer()
                    .frame(height: 16)

                Button(action: {
                    viewModel.register(
                        username: nome,
                        email: email,
                        password: senha,
                        confirmPassword: confirmarSenha
                    )
                }) {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.gray)
                            .cornerRadius(8)
                    } else {
                        Text(strings.cadastrar)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(podeCadastrar ? Color.blue : Color.gray)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                }
                .disabled(!podeCadastrar || isLoading)

                Button(strings.voltarLogin) {
                    viewModel.resetRegisterState()
                    currentScreen = .login
                }
                .padding(.top, 8)
                .disabled(isLoading)

                Spacer()
            }
            .padding(.horizontal, 32)
        }
        .onChange(of: viewModel.registerState) { newState in
            switch newState {
            case .success:
                showSuccess = true
            case .error(let message):
                errorMessage = message
                showError = true
            default:
                break
            }
        }
        .alert(strings.cadastroSucesso, isPresented: $showSuccess) {
            Button("OK") {
                viewModel.resetRegisterState()
                currentScreen = .login
            }
        }
        .alert(strings.erro, isPresented: $showError) {
            Button("OK") {
                viewModel.resetRegisterState()
            }
        } message: {
            Text(errorMessage)
        }
    }
}

#Preview {
    RegisterView(
        currentScreen: .constant(.cadastro),
        termosAceitos: .constant(false),
        strings: Traducoes.portugues,
        viewModel: LoginViewModel()
    )
}
