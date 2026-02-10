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
    @Environment(\.colorScheme) private var colorScheme
    @State private var currentScreen: AppScreen = .loading
    @State private var termosAceitos: Bool = false
    @State private var idiomaAtual: Idioma = .portugues
    @State private var loginViewModel = LoginViewModel()
    @State private var remoteConfig = RemoteConfigRepositoryWrapper()
    @State private var themeLoader = RemoteThemeLoader()
    @State private var didLoadRemoteConfig = false

    var strings: AppStrings {
        Traducoes.obterStrings(idioma: idiomaAtual)
    }

    var currentPalette: ThemePalette {
        colorScheme == .dark ? themeLoader.palettes.dark : themeLoader.palettes.light
    }

    var body: some View {
        Group {
            switch currentScreen {
            case .loading:
                LoadingScreen(strings: strings, theme: currentPalette)
            case .login:
                LoginView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, idioma: $idiomaAtual, strings: strings, viewModel: loginViewModel, theme: currentPalette)
            case .cadastro:
                RegisterView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, strings: strings, viewModel: loginViewModel, theme: currentPalette)
            case .termos:
                TermsView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, strings: strings, theme: currentPalette)
            case .esqueciSenha:
                ForgetPasswordView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel, theme: currentPalette)
            case .senhaTemp:
                TempPasswordView(currentScreen: $currentScreen, strings: strings, theme: currentPalette)
            case .idioma:
                LanguageView(currentScreen: $currentScreen, idioma: $idiomaAtual, strings: strings, theme: currentPalette)
            case .home:
                Group {
                    if loginViewModel.currentUser == nil {
                        GuestProfileView(
                            strings: strings,
                            onLoginClick: { currentScreen = .login },
                            onBackClick: { currentScreen = .main },
                            theme: currentPalette
                        )
                    } else {
                        MeView(currentScreen: $currentScreen, strings: strings, viewModel: loginViewModel, theme: currentPalette)
                    }
                }
            case .main:
                MainView(currentScreen: $currentScreen, strings: strings, loginViewModel: loginViewModel, theme: currentPalette)
            }
        }
        .background(currentPalette.background)
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
