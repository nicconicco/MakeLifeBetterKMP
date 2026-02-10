import Foundation
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
