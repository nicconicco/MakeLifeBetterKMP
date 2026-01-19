import SwiftUI

enum AppScreen {
    case login
    case cadastro
    case termos
    case esqueciSenha
    case senhaTemp
    case idioma
}

struct ContentView: View {
    @State private var currentScreen: AppScreen = .login
    @State private var termosAceitos: Bool = false
    @State private var idiomaAtual: Idioma = .portugues

    var strings: AppStrings {
        Traducoes.obterStrings(idioma: idiomaAtual)
    }

    var body: some View {
        switch currentScreen {
        case .login:
            LoginView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, idioma: $idiomaAtual, strings: strings)
        case .cadastro:
            RegisterView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, strings: strings)
        case .termos:
            TermsView(currentScreen: $currentScreen, termosAceitos: $termosAceitos, strings: strings)
        case .esqueciSenha:
            ForgetPasswordView(currentScreen: $currentScreen, strings: strings)
        case .senhaTemp:
            TempPasswordView(currentScreen: $currentScreen, strings: strings)
        case .idioma:
            LanguageView(currentScreen: $currentScreen, idioma: $idiomaAtual, strings: strings)
        }
    }
}

#Preview {
    ContentView()
}
