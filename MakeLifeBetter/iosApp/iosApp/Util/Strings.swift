import Foundation

enum Idioma: String {
    case portugues = "pt-BR"
    case ingles = "en-US"
    case espanhol = "es-ES"

    var bandeira: String {
        switch self {
        case .portugues: return "\u{1F1E7}\u{1F1F7}"
        case .ingles: return "\u{1F1FA}\u{1F1F8}"
        case .espanhol: return "\u{1F1EA}\u{1F1F8}"
        }
    }
}

struct AppStrings {
    // Login
    let appName: String
    let usuario: String
    let senha: String
    let esqueciSenha: String
    let entrar: String
    let criarNovaConta: String

    // Cadastro
    let criarConta: String
    let nome: String
    let email: String
    let cpf: String
    let celular: String
    let camposObrigatorios: String
    let aceitoTermos: String
    let cadastrar: String
    let voltarLogin: String

    // Termos
    let termosCompromisso: String
    let termosTexto: String
    let concordar: String

    // Esqueci Senha
    let esqueciSenhaTitulo: String
    let confirmar: String
    let voltar: String

    // Senha Temporária
    let senhaTempTitulo: String
    let suaNovaSenha: String
    let useSenhaTemp: String

    // Idioma
    let selecioneIdioma: String
    let portugues: String
    let ingles: String
    let espanhol: String
}

struct Traducoes {
    static let portugues = AppStrings(
        appName: "MakeLifeBetter",
        usuario: "Usuário",
        senha: "Senha",
        esqueciSenha: "Esqueci a senha",
        entrar: "Entrar",
        criarNovaConta: "Criar nova conta",

        criarConta: "Criar Conta",
        nome: "Nome",
        email: "Email",
        cpf: "CPF",
        celular: "Celular",
        camposObrigatorios: "* Campos obrigatórios",
        aceitoTermos: "Aceito os termos de compromisso",
        cadastrar: "Cadastrar",
        voltarLogin: "Voltar ao login",

        termosCompromisso: "Termos de Compromisso",
        termosTexto: "Ao utilizar este aplicativo, você concorda que o MakeLifeBetter poderá coletar suas informações pessoais para melhorar sua experiência e fornecer nossos serviços.\n\nSuas informações serão tratadas com segurança e não serão compartilhadas com terceiros sem seu consentimento.\n\nAo clicar em \"Concordar\", você declara estar de acordo com estes termos.",
        concordar: "Concordar",

        esqueciSenhaTitulo: "Esqueci a Senha",
        confirmar: "Confirmar",
        voltar: "Voltar",

        senhaTempTitulo: "Senha Temporária",
        suaNovaSenha: "Sua nova senha é:",
        useSenhaTemp: "Use esta senha para fazer login e depois altere para uma senha de sua preferência.",

        selecioneIdioma: "Selecione o Idioma",
        portugues: "Português (Brasil)",
        ingles: "English (US)",
        espanhol: "Español"
    )

    static let ingles = AppStrings(
        appName: "MakeLifeBetter",
        usuario: "Username",
        senha: "Password",
        esqueciSenha: "Forgot password",
        entrar: "Sign In",
        criarNovaConta: "Create new account",

        criarConta: "Create Account",
        nome: "Name",
        email: "Email",
        cpf: "CPF",
        celular: "Phone",
        camposObrigatorios: "* Required fields",
        aceitoTermos: "I accept the terms and conditions",
        cadastrar: "Register",
        voltarLogin: "Back to login",

        termosCompromisso: "Terms and Conditions",
        termosTexto: "By using this application, you agree that MakeLifeBetter may collect your personal information to improve your experience and provide our services.\n\nYour information will be treated securely and will not be shared with third parties without your consent.\n\nBy clicking \"Agree\", you declare that you agree to these terms.",
        concordar: "Agree",

        esqueciSenhaTitulo: "Forgot Password",
        confirmar: "Confirm",
        voltar: "Back",

        senhaTempTitulo: "Temporary Password",
        suaNovaSenha: "Your new password is:",
        useSenhaTemp: "Use this password to log in and then change it to a password of your choice.",

        selecioneIdioma: "Select Language",
        portugues: "Português (Brasil)",
        ingles: "English (US)",
        espanhol: "Español"
    )

    static let espanhol = AppStrings(
        appName: "MakeLifeBetter",
        usuario: "Usuario",
        senha: "Contraseña",
        esqueciSenha: "Olvidé mi contraseña",
        entrar: "Iniciar sesión",
        criarNovaConta: "Crear nueva cuenta",

        criarConta: "Crear Cuenta",
        nome: "Nombre",
        email: "Correo electrónico",
        cpf: "CPF",
        celular: "Teléfono",
        camposObrigatorios: "* Campos obligatorios",
        aceitoTermos: "Acepto los términos y condiciones",
        cadastrar: "Registrarse",
        voltarLogin: "Volver al inicio",

        termosCompromisso: "Términos y Condiciones",
        termosTexto: "Al utilizar esta aplicación, usted acepta que MakeLifeBetter pueda recopilar su información personal para mejorar su experiencia y proporcionar nuestros servicios.\n\nSu información será tratada de forma segura y no será compartida con terceros sin su consentimiento.\n\nAl hacer clic en \"Aceptar\", declara estar de acuerdo con estos términos.",
        concordar: "Aceptar",

        esqueciSenhaTitulo: "Olvidé mi Contraseña",
        confirmar: "Confirmar",
        voltar: "Volver",

        senhaTempTitulo: "Contraseña Temporal",
        suaNovaSenha: "Su nueva contraseña es:",
        useSenhaTemp: "Use esta contraseña para iniciar sesión y luego cámbiela por una contraseña de su preferencia.",

        selecioneIdioma: "Seleccionar Idioma",
        portugues: "Português (Brasil)",
        ingles: "English (US)",
        espanhol: "Español"
    )

    static func obterStrings(idioma: Idioma) -> AppStrings {
        switch idioma {
        case .portugues: return portugues
        case .ingles: return ingles
        case .espanhol: return espanhol
        }
    }
}
