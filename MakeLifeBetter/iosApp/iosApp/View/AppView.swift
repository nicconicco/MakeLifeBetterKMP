import SwiftUI

enum AppScreen {
    case login
    case cadastro
    case termos
    case esqueciSenha
    case senhaTemp
    case idioma
    case home
    case main
}

struct AppView: View {
    @State private var currentScreen: AppScreen = .login
    @State private var termosAceitos: Bool = false
    @State private var idiomaAtual: Idioma = .portugues
    @StateObject private var loginViewModel = LoginViewModel()

    var strings: AppStrings {
        Traducoes.obterStrings(idioma: idiomaAtual)
    }

    var body: some View {
        switch currentScreen {
        case .login:
            LoginView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, idioma: $idiomaAtual, strings: strings, viewModel: loginViewModel)
        case .cadastro:
            RegisterView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, strings: strings, viewModel: loginViewModel)
        case .termos:
            TermsView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, strings: strings)
        case .esqueciSenha:
            ForgetPasswordView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel)
        case .senhaTemp:
            TempPasswordView(currentScreen: $currentScreen, strings: strings)
        case .idioma:
            LanguageView(currentScreen: $currentScreen, idioma: $idiomaAtual, strings: strings)
        case .home:
            HomeView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel)
        case .main:
            MainView(currentScreen: $currentScreen, strings: strings, loginViewModel: loginViewModel)
        }
    
    }
}

#Preview {
    AppView()
}
