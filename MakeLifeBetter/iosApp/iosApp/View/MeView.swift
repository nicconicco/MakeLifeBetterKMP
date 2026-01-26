import SwiftUI

struct MeView: View {
    @Binding var currentScreen: AppScreen
    let strings: AppStrings
    @ObservedObject var viewModel: LoginViewModel

    @State private var username: String = ""
    @State private var email: String = ""
    @State private var showProfileSuccess: Bool = false

    @State private var currentPassword: String = ""
    @State private var newPassword: String = ""
    @State private var confirmNewPassword: String = ""
    @State private var showPasswordSuccess: Bool = false

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                Spacer().frame(height: 16)

                // Avatar
                ZStack {
                    Circle()
                        .fill(Color.blue.opacity(0.15))
                        .frame(width: 100, height: 100)
                    Text(viewModel.currentUser?.username.prefix(1).uppercased() ?? "?")
                        .font(.system(size: 40, weight: .bold))
                        .foregroundColor(.blue)
                }

                Text("Meu Perfil")
                    .font(.title)
                    .fontWeight(.bold)

                // MARK: - Profile Form Card
                VStack(alignment: .leading, spacing: 16) {
                    Text("Informacoes pessoais")
                        .font(.headline)
                        .fontWeight(.semibold)

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Nome de usuario").font(.caption).foregroundColor(.secondary)
                        TextField("Nome de usuario", text: $username)
                            .textFieldStyle(.roundedBorder)
                            .disabled(viewModel.profileUpdateState == .loading)
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Email").font(.caption).foregroundColor(.secondary)
                        TextField("Email", text: $email)
                            .textFieldStyle(.roundedBorder)
                            .keyboardType(.emailAddress)
                            .autocapitalization(.none)
                            .disabled(viewModel.profileUpdateState == .loading)
                    }

                    if case .error(let message) = viewModel.profileUpdateState {
                        Text(message)
                            .font(.caption)
                            .foregroundColor(.red)
                    }

                    if showProfileSuccess {
                        Text("Perfil atualizado com sucesso!")
                            .font(.caption)
                            .foregroundColor(.blue)
                    }

                    Button(action: {
                        showProfileSuccess = false
                        viewModel.updateProfile(username: username, email: email)
                    }) {
                        if viewModel.profileUpdateState == .loading {
                            ProgressView()
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 8)
                        } else {
                            Text("Salvar perfil")
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 8)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    .disabled(viewModel.profileUpdateState == .loading)
                }
                .padding(16)
                .background(Color(.systemBackground))
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                // MARK: - Password Change Card
                VStack(alignment: .leading, spacing: 16) {
                    Text("Alterar senha")
                        .font(.headline)
                        .fontWeight(.semibold)

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Senha atual").font(.caption).foregroundColor(.secondary)
                        SecureField("Senha atual", text: $currentPassword)
                            .textFieldStyle(.roundedBorder)
                            .disabled(viewModel.passwordChangeState == .loading)
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Nova senha").font(.caption).foregroundColor(.secondary)
                        SecureField("Nova senha", text: $newPassword)
                            .textFieldStyle(.roundedBorder)
                            .disabled(viewModel.passwordChangeState == .loading)
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Confirmar nova senha").font(.caption).foregroundColor(.secondary)
                        SecureField("Confirmar nova senha", text: $confirmNewPassword)
                            .textFieldStyle(.roundedBorder)
                            .disabled(viewModel.passwordChangeState == .loading)
                    }

                    if case .error(let message) = viewModel.passwordChangeState {
                        Text(message)
                            .font(.caption)
                            .foregroundColor(.red)
                    }

                    if showPasswordSuccess {
                        Text("Senha alterada com sucesso!")
                            .font(.caption)
                            .foregroundColor(.blue)
                    }

                    Button(action: {
                        showPasswordSuccess = false
                        viewModel.changePassword(
                            currentPassword: currentPassword,
                            newPassword: newPassword,
                            confirmNewPassword: confirmNewPassword
                        )
                    }) {
                        if viewModel.passwordChangeState == .loading {
                            ProgressView()
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 8)
                        } else {
                            Text("Alterar senha")
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 8)
                        }
                    }
                    .buttonStyle(.bordered)
                    .disabled(viewModel.passwordChangeState == .loading)
                }
                .padding(16)
                .background(Color(.systemBackground))
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                // MARK: - Account ID Card
                if let user = viewModel.currentUser {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("ID da conta")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Text(user.id)
                            .font(.body)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(16)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                }

                Spacer().frame(height: 8)

                // MARK: - Logout Button
                Button(action: {
                    viewModel.logout()
                    currentScreen = .login
                }) {
                    Text("Sair da conta")
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.borderedProminent)
                .tint(.red)

                Spacer().frame(height: 16)
            }
            .padding(.horizontal, 16)
        }
        .onAppear {
            if let user = viewModel.currentUser {
                username = user.username
                email = user.email
            }
        }
        .onChange(of: viewModel.profileUpdateState) { _, newState in
            if case .success = newState {
                showProfileSuccess = true
            }
        }
        .onChange(of: viewModel.passwordChangeState) { _, newState in
            if case .success = newState {
                showPasswordSuccess = true
                currentPassword = ""
                newPassword = ""
                confirmNewPassword = ""
            }
        }
    }
}

#Preview {
    MeView(
        currentScreen: .constant(.home),
        strings: Traducoes.portugues,
        viewModel: LoginViewModel()
    )
}
