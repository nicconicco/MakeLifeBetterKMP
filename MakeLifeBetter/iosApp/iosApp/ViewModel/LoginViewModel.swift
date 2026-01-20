import Foundation
import Combine
import ComposeApp

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
    case success(String)
    case error(String)
}

enum ProfileUpdateResultState: Equatable {
    case idle
    case loading
    case success(User)
    case error(String)
}

enum PasswordChangeResultState: Equatable {
    case idle
    case loading
    case success(String)
    case error(String)
}

// MARK: - User Model

struct User: Equatable {
    let id: String
    let username: String
    let email: String

    init(id: String, username: String, email: String) {
        self.id = id
        self.username = username
        self.email = email
    }

    init(from kotlinUser: ComposeApp.User) {
        self.id = kotlinUser.id
        self.username = kotlinUser.username
        self.email = kotlinUser.email
    }
}

// MARK: - LoginViewModel

class LoginViewModel: ObservableObject {

    @Published var loginState: AuthResultState = .idle
    @Published var registerState: RegisterResultState = .idle
    @Published var passwordRecoveryState: PasswordRecoveryResultState = .idle
    @Published var profileUpdateState: ProfileUpdateResultState = .idle
    @Published var passwordChangeState: PasswordChangeResultState = .idle
    @Published var currentUser: User? = nil

    private let sharedViewModel: SharedLoginViewModelWrapper

    init() {
        sharedViewModel = SharedLoginViewModelWrapper()
        setupObservers()
    }

    private func setupObservers() {
        // Observa Login State
        sharedViewModel.observeLoginState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.loginState = .idle
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.loginState = .loading
                }
            },
            onSuccess: { [weak self] kotlinUser in
                DispatchQueue.main.async {
                    let user = User(from: kotlinUser)
                    self?.currentUser = user
                    self?.loginState = .success(user)
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.loginState = .error(message)
                }
            }
        )

        // Observa Register State
        sharedViewModel.observeRegisterState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.registerState = .idle
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.registerState = .loading
                }
            },
            onSuccess: { [weak self] in
                DispatchQueue.main.async {
                    self?.registerState = .success
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.registerState = .error(message)
                }
            }
        )

        // Observa Password Recovery State
        sharedViewModel.observePasswordRecoveryState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.passwordRecoveryState = .idle
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.passwordRecoveryState = .loading
                }
            },
            onSuccess: { [weak self] message in
                DispatchQueue.main.async {
                    self?.passwordRecoveryState = .success(message)
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.passwordRecoveryState = .error(message)
                }
            }
        )

        // Observa Profile Update State
        sharedViewModel.observeProfileUpdateState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.profileUpdateState = .idle
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.profileUpdateState = .loading
                }
            },
            onSuccess: { [weak self] kotlinUser in
                DispatchQueue.main.async {
                    let user = User(from: kotlinUser)
                    self?.currentUser = user
                    self?.profileUpdateState = .success(user)
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.profileUpdateState = .error(message)
                }
            }
        )

        // Observa Password Change State
        sharedViewModel.observePasswordChangeState(
            onIdle: { [weak self] in
                DispatchQueue.main.async {
                    self?.passwordChangeState = .idle
                }
            },
            onLoading: { [weak self] in
                DispatchQueue.main.async {
                    self?.passwordChangeState = .loading
                }
            },
            onSuccess: { [weak self] message in
                DispatchQueue.main.async {
                    self?.passwordChangeState = .success(message)
                }
            },
            onError: { [weak self] message in
                DispatchQueue.main.async {
                    self?.passwordChangeState = .error(message)
                }
            }
        )

        // Observa Current User
        sharedViewModel.observeCurrentUser { [weak self] kotlinUser in
            DispatchQueue.main.async {
                if let kotlinUser = kotlinUser {
                    self?.currentUser = User(from: kotlinUser)
                } else {
                    self?.currentUser = nil
                }
            }
        }
    }

    // MARK: - Login

    func login(username: String, password: String) {
        sharedViewModel.login(username: username, password: password)
    }

    // MARK: - Register

    func register(username: String, email: String, password: String, confirmPassword: String) {
        sharedViewModel.register(username: username, email: email, password: password, confirmPassword: confirmPassword)
    }

    // MARK: - Password Recovery

    func recoverPassword(email: String) {
        sharedViewModel.recoverPassword(email: email)
    }

    // MARK: - Profile Update

    func updateProfile(username: String, email: String) {
        sharedViewModel.updateProfile(username: username, email: email)
    }

    // MARK: - Password Change

    func changePassword(currentPassword: String, newPassword: String, confirmNewPassword: String) {
        sharedViewModel.changePassword(currentPassword: currentPassword, newPassword: newPassword, confirmNewPassword: confirmNewPassword)
    }

    // MARK: - Logout

    func logout() {
        sharedViewModel.logout()
    }

    // MARK: - Reset States

    func resetLoginState() {
        sharedViewModel.resetLoginState()
    }

    func resetRegisterState() {
        sharedViewModel.resetRegisterState()
    }

    func resetPasswordRecoveryState() {
        sharedViewModel.resetPasswordRecoveryState()
    }

    func resetProfileUpdateState() {
        sharedViewModel.resetProfileUpdateState()
    }

    func resetPasswordChangeState() {
        sharedViewModel.resetPasswordChangeState()
    }

    // MARK: - Helpers

    func isLoggedIn() -> Bool {
        return sharedViewModel.isLoggedIn()
    }

    deinit {
        sharedViewModel.clear()
    }
}
