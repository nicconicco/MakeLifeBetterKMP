import Foundation
import Combine

// MARK: - Auth States (espelha o Kotlin)

enum AuthResultState: Equatable {
    case idle
    case loading
    case success(User)
    case error(String)
}

enum RegisterResultState: Equatable {
    case idle
    case loading
    case success
    case error(String)
}

enum PasswordRecoveryResultState: Equatable {
    case idle
    case loading
    case success(String) // senha temporaria
    case error(String)
}

// MARK: - User Model

struct User: Equatable {
    let id: String
    let username: String
    let email: String
}

// MARK: - LoginViewModel

class LoginViewModel: ObservableObject {

    @Published var loginState: AuthResultState = .idle
    @Published var registerState: RegisterResultState = .idle
    @Published var passwordRecoveryState: PasswordRecoveryResultState = .idle
    @Published var currentUser: User? = nil

    // Repositorio local (usuarios em memoria)
    private var users: [String: (user: User, passwordHash: String)] = [:]

    init() {
        // Usuario padrao para testes
        let defaultUser = User(id: "1", username: "admin", email: "admin@example.com")
        users["admin"] = (user: defaultUser, passwordHash: hashPassword("password"))
    }

    // MARK: - Login

    func login(username: String, password: String) {
        // Reset para Idle primeiro para garantir que a transicao seja detectada
        loginState = .idle

        guard !username.isEmpty, !password.isEmpty else {
            loginState = .error("Preencha todos os campos")
            return
        }

        loginState = .loading

        // Simula delay de rede
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            guard let self = self else { return }

            if let userData = self.users[username] {
                if userData.passwordHash == self.hashPassword(password) {
                    self.currentUser = userData.user
                    self.loginState = .success(userData.user)
                } else {
                    self.loginState = .error("Senha incorreta")
                }
            } else {
                self.loginState = .error("Usuario nao encontrado")
            }
        }
    }

    // MARK: - Register

    func register(username: String, email: String, password: String, confirmPassword: String) {
        // Reset para Idle primeiro
        registerState = .idle

        guard !username.isEmpty, !email.isEmpty, !password.isEmpty else {
            registerState = .error("Preencha todos os campos")
            return
        }

        guard password == confirmPassword else {
            registerState = .error("As senhas nao coincidem")
            return
        }

        guard username.count >= 3 else {
            registerState = .error("Nome de usuario deve ter pelo menos 3 caracteres")
            return
        }

        guard password.count >= 6 else {
            registerState = .error("Senha deve ter pelo menos 6 caracteres")
            return
        }

        guard email.contains("@") && email.contains(".") else {
            registerState = .error("Email invalido")
            return
        }

        registerState = .loading

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            guard let self = self else { return }

            if self.users[username] != nil {
                self.registerState = .error("Nome de usuario ja existe")
                return
            }

            if self.users.values.contains(where: { $0.user.email == email }) {
                self.registerState = .error("Email ja cadastrado")
                return
            }

            let newUser = User(
                id: String(Int.random(in: 1000000...9999999)),
                username: username,
                email: email
            )

            self.users[username] = (user: newUser, passwordHash: self.hashPassword(password))
            self.registerState = .success
        }
    }

    // MARK: - Password Recovery

    func recoverPassword(email: String) {
        // Reset para Idle primeiro
        passwordRecoveryState = .idle

        guard !email.isEmpty else {
            passwordRecoveryState = .error("Informe o email")
            return
        }

        passwordRecoveryState = .loading

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) { [weak self] in
            guard let self = self else { return }

            guard let userEntry = self.users.first(where: { $0.value.user.email == email }) else {
                self.passwordRecoveryState = .error("Email nao encontrado")
                return
            }

            let temporaryPassword = self.generateTemporaryPassword()

            // Atualiza a senha do usuario
            self.users[userEntry.key] = (
                user: userEntry.value.user,
                passwordHash: self.hashPassword(temporaryPassword)
            )

            self.passwordRecoveryState = .success(temporaryPassword)
        }
    }

    // MARK: - Logout

    func logout() {
        currentUser = nil
        loginState = .idle
    }

    // MARK: - Reset States

    func resetLoginState() {
        loginState = .idle
    }

    func resetRegisterState() {
        registerState = .idle
    }

    func resetPasswordRecoveryState() {
        passwordRecoveryState = .idle
    }

    // MARK: - Helpers

    func isLoggedIn() -> Bool {
        return currentUser != nil
    }

    private func hashPassword(_ password: String) -> String {
        return String(password.hashValue)
    }

    private func generateTemporaryPassword() -> String {
        let chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789"
        return String((0..<8).map { _ in chars.randomElement()! })
    }
}
