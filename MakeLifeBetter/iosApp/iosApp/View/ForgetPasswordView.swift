import SwiftUI

struct ForgetPasswordView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    @ObservedObject var viewModel: LoginViewModel

    @State private var email: String = ""
    @State private var showError: Bool = false
    @State private var errorMessage: String = ""
    @State private var showSuccess: Bool = false
    @State private var successMessage: String = ""

    private var emailPreenchido: Bool {
        !email.trimmingCharacters(in: .whitespaces).isEmpty
    }

    private var isLoading: Bool {
        viewModel.passwordRecoveryState == .loading
    }

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            Text(strings.esqueciSenhaTitulo)
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.blue)

            Spacer()
                .frame(height: 24)

            Text(strings.instrucaoRecuperacao)
                .font(.subheadline)
                .foregroundColor(.gray)
                .multilineTextAlignment(.center)

            Spacer()
                .frame(height: 16)

            TextField("\(strings.email) *", text: $email)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .autocapitalization(.none)
                .keyboardType(.emailAddress)
                .disabled(isLoading)

            HStack {
                Text(strings.camposObrigatorios)
                    .font(.caption)
                    .foregroundColor(.gray)
                Spacer()
            }

            Spacer()
                .frame(height: 16)

            Button(action: {
                viewModel.recoverPassword(email: email)
            }) {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.gray)
                        .cornerRadius(8)
                } else {
                    Text(strings.confirmar)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(emailPreenchido ? Color.blue : Color.gray)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
            }
            .disabled(!emailPreenchido || isLoading)

            Button(strings.voltarLogin) {
                viewModel.resetPasswordRecoveryState()
                currentScreen = .login
            }
            .padding(.top, 8)
            .disabled(isLoading)

            Spacer()
        }
        .padding(.horizontal, 32)
        .onChange(of: viewModel.passwordRecoveryState) { oldState, newState in
            handleRecoveryState(newState)
        }
        .alert(strings.erro, isPresented: $showError) {
            Button("OK") {
                viewModel.resetPasswordRecoveryState()
            }
        } message: {
            Text(errorMessage)
        }
        .alert(strings.emailEnviado, isPresented: $showSuccess) {
            Button("OK") {
                viewModel.resetPasswordRecoveryState()
                currentScreen = .login
            }
        } message: {
            Text(successMessage)
        }
    }

    private func handleRecoveryState(_ state: PasswordRecoveryResultState) {
        switch state {
        case .success(let message):
            successMessage = message
            showSuccess = true
        case .error(let message):
            errorMessage = message
            showError = true
        default:
            break
        }
    }
}

#Preview {
    ForgetPasswordView(
        currentScreen: .constant(.esqueciSenha),
        strings: Traducoes.portugues,
        viewModel: LoginViewModel()
    )
}
