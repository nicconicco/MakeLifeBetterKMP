import SwiftUI
import ComposeApp

enum AppScreen {
    case loading
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
    @State private var currentScreen: AppScreen = .loading
    @State private var termosAceitos: Bool = false
    @State private var idiomaAtual: Idioma = .portugues
    @State private var loginViewModel = LoginViewModel()
    @State private var remoteConfig = RemoteConfigRepositoryWrapper()
    @State private var didLoadRemoteConfig = false

    var strings: AppStrings {
        Traducoes.obterStrings(idioma: idiomaAtual)
    }

    var body: some View {
        Group {
            switch currentScreen {
            case .loading:
                LoadingScreen(strings: strings)
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
                MeView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel)
            case .main:
                MainView(currentScreen: $currentScreen, strings: strings, loginViewModel: loginViewModel)
            }
        }
        .onAppear {
            if !didLoadRemoteConfig {
                didLoadRemoteConfig = true
                currentScreen = .loading
                remoteConfig.fetchAndActivate(
                    onSuccess: { _ in
                        DispatchQueue.main.async {
                            let loginRequired = remoteConfig.isLoginRequired()
                            currentScreen = loginRequired ? .login : .main
                        }
                    },
                    onError: { _ in
                        DispatchQueue.main.async {
                            let loginRequired = remoteConfig.isLoginRequired()
                            currentScreen = loginRequired ? .login : .main
                        }
                    }
                )
            }
        }
    }
}

#Preview {
    AppView()
}
