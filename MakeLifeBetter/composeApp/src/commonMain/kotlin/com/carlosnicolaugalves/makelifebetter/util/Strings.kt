package com.carlosnicolaugalves.makelifebetter.util

enum class Language(val code: String, val flag: String) {
    PORTUGUESE("pt-BR", "\uD83C\uDDE7\uD83C\uDDF7"),
    ENGLISH("en-US", "\uD83C\uDDFA\uD83C\uDDF8"),
    SPANISH("es-ES", "\uD83C\uDDEA\uD83C\uDDF8")
}

data class AppStrings(
    // Login
    val appName: String,
    val username: String,
    val password: String,
    val forgotPassword: String,
    val signIn: String,
    val createAccount: String,

    // Register
    val register: String,
    val name: String,
    val email: String,
    val cpf: String,
    val phone: String,
    val requiredFields: String,
    val acceptTerms: String,
    val registerButton: String,
    val backToLogin: String,

    // Terms
    val termsTitle: String,
    val termsText: String,
    val agree: String,

    // Forgot Password
    val forgotPasswordTitle: String,
    val confirm: String,
    val back: String,

    // Temp Password
    val tempPasswordTitle: String,
    val yourNewPassword: String,
    val useTempPassword: String,

    // Language
    val selectLanguage: String,
    val portuguese: String,
    val english: String,
    val spanish: String
)

object Translations {
    val portuguese = AppStrings(
        appName = "MakeLifeBetter",
        username = "Usuário",
        password = "Senha",
        forgotPassword = "Esqueci a senha",
        signIn = "Entrar",
        createAccount = "Criar nova conta",

        register = "Criar Conta",
        name = "Nome",
        email = "Email",
        cpf = "CPF",
        phone = "Celular",
        requiredFields = "* Campos obrigatórios",
        acceptTerms = "Aceito os termos de compromisso",
        registerButton = "Cadastrar",
        backToLogin = "Voltar ao login",

        termsTitle = "Termos de Compromisso",
        termsText = "Ao utilizar este aplicativo, você concorda que o MakeLifeBetter poderá coletar suas informações pessoais para melhorar sua experiência e fornecer nossos serviços.\n\nSuas informações serão tratadas com segurança e não serão compartilhadas com terceiros sem seu consentimento.\n\nAo clicar em \"Concordar\", você declara estar de acordo com estes termos.",
        agree = "Concordar",

        forgotPasswordTitle = "Esqueci a Senha",
        confirm = "Confirmar",
        back = "Voltar",

        tempPasswordTitle = "Senha Temporária",
        yourNewPassword = "Sua nova senha é:",
        useTempPassword = "Use esta senha para fazer login e depois altere para uma senha de sua preferência.",

        selectLanguage = "Selecione o Idioma",
        portuguese = "Português (Brasil)",
        english = "English (US)",
        spanish = "Español"
    )

    val english = AppStrings(
        appName = "MakeLifeBetter",
        username = "Username",
        password = "Password",
        forgotPassword = "Forgot password",
        signIn = "Sign In",
        createAccount = "Create new account",

        register = "Create Account",
        name = "Name",
        email = "Email",
        cpf = "CPF",
        phone = "Phone",
        requiredFields = "* Required fields",
        acceptTerms = "I accept the terms and conditions",
        registerButton = "Register",
        backToLogin = "Back to login",

        termsTitle = "Terms and Conditions",
        termsText = "By using this application, you agree that MakeLifeBetter may collect your personal information to improve your experience and provide our services.\n\nYour information will be treated securely and will not be shared with third parties without your consent.\n\nBy clicking \"Agree\", you declare that you agree to these terms.",
        agree = "Agree",

        forgotPasswordTitle = "Forgot Password",
        confirm = "Confirm",
        back = "Back",

        tempPasswordTitle = "Temporary Password",
        yourNewPassword = "Your new password is:",
        useTempPassword = "Use this password to log in and then change it to a password of your choice.",

        selectLanguage = "Select Language",
        portuguese = "Português (Brasil)",
        english = "English (US)",
        spanish = "Español"
    )

    val spanish = AppStrings(
        appName = "MakeLifeBetter",
        username = "Usuario",
        password = "Contraseña",
        forgotPassword = "Olvidé mi contraseña",
        signIn = "Iniciar sesión",
        createAccount = "Crear nueva cuenta",

        register = "Crear Cuenta",
        name = "Nombre",
        email = "Correo electrónico",
        cpf = "CPF",
        phone = "Teléfono",
        requiredFields = "* Campos obligatorios",
        acceptTerms = "Acepto los términos y condiciones",
        registerButton = "Registrarse",
        backToLogin = "Volver al inicio",

        termsTitle = "Términos y Condiciones",
        termsText = "Al utilizar esta aplicación, usted acepta que MakeLifeBetter pueda recopilar su información personal para mejorar su experiencia y proporcionar nuestros servicios.\n\nSu información será tratada de forma segura y no será compartida con terceros sin su consentimiento.\n\nAl hacer clic en \"Aceptar\", declara estar de acuerdo con estos términos.",
        agree = "Aceptar",

        forgotPasswordTitle = "Olvidé mi Contraseña",
        confirm = "Confirmar",
        back = "Volver",

        tempPasswordTitle = "Contraseña Temporal",
        yourNewPassword = "Su nueva contraseña es:",
        useTempPassword = "Use esta contraseña para iniciar sesión y luego cámbiela por una contraseña de su preferencia.",

        selectLanguage = "Seleccionar Idioma",
        portuguese = "Português (Brasil)",
        english = "English (US)",
        spanish = "Español"
    )

    fun getStrings(language: Language): AppStrings {
        return when (language) {
            Language.PORTUGUESE -> portuguese
            Language.ENGLISH -> english
            Language.SPANISH -> spanish
        }
    }
}
