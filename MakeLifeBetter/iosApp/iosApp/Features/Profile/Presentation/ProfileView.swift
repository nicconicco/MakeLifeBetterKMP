import SwiftUI
import ComposeApp

struct ProfileView: View {
    var currentUser: User?
    var profileUpdateState: ProfileUpdateResult
    var passwordChangeState: PasswordChangeResult
    var onSaveClick: (String, String) -> Void
    var onChangePasswordClick: (String, String, String) -> Void
    var onLogoutClick: () -> Void

    @State private var username: String
    @State private var email: String
    @State private var showProfileSuccess: Bool = false

    @State private var currentPassword: String = ""
    @State private var newPassword: String = ""
    @State private var confirmNewPassword: String = ""
    @State private var showPasswordSuccess: Bool = false

    init(currentUser: User?, profileUpdateState: ProfileUpdateResult, passwordChangeState: PasswordChangeResult, onSaveClick: @escaping (String, String) -> Void, onChangePasswordClick: @escaping (String, String, String) -> Void, onLogoutClick: @escaping () -> Void) {
        self.currentUser = currentUser
        self.profileUpdateState = profileUpdateState
        self.passwordChangeState = passwordChangeState
        self.onSaveClick = onSaveClick
        self.onChangePasswordClick = onChangePasswordClick
        self.onLogoutClick = onLogoutClick
        
        _username = State(initialValue: currentUser?.username ?? "")
        _email = State(initialValue: currentUser?.email ?? "")
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                Spacer(minLength: 16)

                // Avatar
                AvatarView(username: currentUser?.username ?? "?")

                Text("Meu Perfil")
                    .font(.headline)
                    .fontWeight(.bold)

                // Profile Form
                ProfileFormView(
                    username: $username,
                    email: $email,
                    profileUpdateState: profileUpdateState,
                    showProfileSuccess: $showProfileSuccess,
                    onSaveClick: {
                        showProfileSuccess = false
                        onSaveClick(username, email)
                    }
                )

                // Password Change
                PasswordChangeView(
                    currentPassword: $currentPassword,
                    newPassword: $newPassword,
                    confirmNewPassword: $confirmNewPassword,
                    passwordChangeState: passwordChangeState,
                    showPasswordSuccess: $showPasswordSuccess,
                    onChangePasswordClick: {
                        showPasswordSuccess = false
                        onChangePasswordClick(currentPassword, newPassword, confirmNewPassword)
                    }
                )

                // User ID Info
                if let user = currentUser {
                    UserIdInfoView(userId: user.id)
                }

                Spacer(minLength: 8)

                // Logout button
                LogoutButton(onLogoutClick: onLogoutClick)
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 80)
        }
        .onChange(of: profileUpdateState) { newState in
        //todo: resolver
//            if newState is ComposeApp.ProfileUpdateResult.Success {
//                showProfileSuccess = true
//            }
        }
        .onChange(of: passwordChangeState) { newState in
            //todo: resolver
//            if newState is ComposeAppPasswordChangeResultSuccess {
//                            showPasswordSuccess = true
//                            currentPassword = ""
//                            newPassword = ""
//                            confirmNewPassword = ""
//            }
        }
    }
}

// AvatarView, ProfileFormView, PasswordChangeView, UserIdInfoView, LogoutButton remain unchanged

struct AvatarView: View {
    var username: String

    var body: some View {
        Circle()
            .fill(Color.blue.opacity(0.2))
            .frame(width: 100, height: 100)
            .overlay(
                Text(username.first?.uppercased() ?? "?")
                    .font(.system(size: 40, weight: .bold))
                    .foregroundColor(.blue)
            )
    }
}

struct ProfileFormView: View {
    @Binding var username: String
    @Binding var email: String
    var profileUpdateState: ProfileUpdateResult
    @Binding var showProfileSuccess: Bool
    var onSaveClick: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text("Informações pessoais")
                .font(.title2)
                .fontWeight(.semibold)

            TextField("Nome de usuário", text: $username)
                .textFieldStyle(RoundedBorderTextFieldStyle())
            //todo: resolver
//                .disabled(profileUpdateState is ProfileUpdateResult.Loading)

            TextField("Email", text: $email)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .disabled(profileUpdateState is ProfileUpdateResult.Loading)

            // Handle error message
//            if case let ProfileUpdateResult.Error(message) = profileUpdateState {
//                Text(message)
//                    .foregroundColor(.red)
//                    .font(.footnote)
//            }

            // Display success message
            if showProfileSuccess {
                Text("Perfil atualizado com sucesso!")
                    .foregroundColor(.green)
                    .font(.footnote)
            }

            Button(action: onSaveClick) {
                if profileUpdateState is ProfileUpdateResult.Loading {
                    ProgressView()
                        .frame(width: 20, height: 20)
                } else {
                    Text("Salvar perfil")
                }
            }
            .disabled(profileUpdateState is ProfileUpdateResult.Loading)
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

struct PasswordChangeView: View {
    @Binding var currentPassword: String
    @Binding var newPassword: String
    @Binding var confirmNewPassword: String
    var passwordChangeState: PasswordChangeResult
    @Binding var showPasswordSuccess: Bool
    var onChangePasswordClick: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text("Alterar senha")
                .font(.title2)
                .fontWeight(.semibold)

            SecureField("Senha atual", text: $currentPassword)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .disabled(passwordChangeState is PasswordChangeResult.Loading)

            SecureField("Nova senha", text: $newPassword)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .disabled(passwordChangeState is PasswordChangeResult.Loading)

            SecureField("Confirmar nova senha", text: $confirmNewPassword)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .disabled(passwordChangeState is PasswordChangeResult.Loading)

            // Handle error message
            if let errorState = passwordChangeState as? PasswordChangeResult.Error {
                Text(errorState.message)
                .foregroundColor(.red)
                .font(.footnote)
            }

            // Display success message
            if showPasswordSuccess {
                Text("Senha alterada com sucesso!")
                    .foregroundColor(.green)
                    .font(.footnote)
            }

            Button(action: onChangePasswordClick) {
                if passwordChangeState is PasswordChangeResult.Loading {
                    ProgressView()
                        .frame(width: 20, height: 20)
                } else {
                    Text("Alterar senha")
                }
            }
            .disabled(passwordChangeState is PasswordChangeResult.Loading)
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

struct UserIdInfoView: View {
    var userId: String

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("ID da conta")
                .font(.subheadline)
                .foregroundColor(.gray)
            Text(userId)
                .font(.body)
        }
        .padding()
        .background(Color.gray.opacity(0.1))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

struct LogoutButton: View {
    var onLogoutClick: () -> Void

    var body: some View {
        Button(action: onLogoutClick) {
            Text("Sair da conta")
                .foregroundColor(.white)
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.red)
                .cornerRadius(8)
        }
    }
}

//struct ProfileViewPreview: PreviewProvider {
    //todo: resolver
//    static var previews: some View {
//        let user = User(id: "1", username: "Usuario Test", email: "email@gmail.com")
//        ProfileScreen(
//            currentUser: user,
//            profileUpdateState: ProfileUpdateResult.Success(user: user),
//            passwordChangeState: PasswordChangeResult.Success(message: "ok"),
//            onSaveClick: { _, _ in },
//            onChangePasswordClick: { _, _, _ in },
//            onLogoutClick: {}
//        )
//    }
//}
