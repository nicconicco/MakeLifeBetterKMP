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
    val confirmPassword: String,
    val requiredFields: String,
    val acceptTerms: String,
    val registerButton: String,
    val backToLogin: String,
    val passwordMinLength: String,
    val passwordsDoNotMatch: String,

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

    // Access Code
    val accessCode: String,
    val invalidAccessCode: String,

    // Language
    val selectLanguage: String,
    val portuguese: String,
    val english: String,
    val spanish: String,

    // Common
    val cancel: String,
    val delete: String,
    val error: String,
    val tryAgain: String,
    val save: String,
    val send: String,
    val loading: String,
    val success: String,
    val loginError: String,
    val registerError: String,
    val errorMessage: String,

    // Chat Screen
    val generalList: String,
    val questions: String,
    val typeYourMessage: String,
    val you: String,
    val askQuestion: String,
    val questionTitle: String,
    val description: String,
    val post: String,
    val deleteQuestion: String,
    val deleteQuestionConfirm: String,
    val by: String,
    val replies: String,
    val questionDetails: String,
    val noRepliesYet: String,
    val writeYourReply: String,
    val sendReply: String,
    val deleteReply: String,

    // Profile Screen
    val myProfile: String,
    val myOrders: String,
    val seeYourOrderStatus: String,
    val personalInfo: String,
    val usernameLabel: String,
    val saveProfile: String,
    val changePassword: String,
    val currentPassword: String,
    val newPassword: String,
    val confirmNewPassword: String,
    val profileUpdated: String,
    val passwordChanged: String,
    val accountId: String,
    val adminTools: String,
    val adminOnlyAccess: String,
    val openAdminPanel: String,
    val signOut: String,

    // Store Screen
    val store: String,
    val all: String,
    val noProductsFound: String,
    val emptyCart: String,
    val cart: String,
    val addToCart: String,
    val removeFromCart: String,
    val checkout: String,
    val total: String,
    val quantity: String,
    val category: String,
    val price: String,
    val available: String,
    val unavailable: String,
    val orderConfirmed: String,
    val backToStore: String,
    val continueShopping: String,
    val orderDetails: String,
    val orderStatus: String,
    val orderDate: String,
    val orderTotal: String,
    val items: String,
    val finishOrder: String,
    val clearCart: String,
    val emptyCartMessage: String,

    // Hire Me Screen
    val hireMe: String,
    val mobileDeveloper: String,
    val experience: String,
    val years: String,
    val developerDescription: String,
    val contactMe: String,
    val sendEmailNow: String,
    val contact: String,

    // Event Details
    val details: String,
    val information: String,
    val eventDescription: String,
    val location: String,
    val date: String,
    val time: String,

    // Map Screen
    val map: String,
    val eventLocation: String,
    val openInMaps: String,
    val contacts: String,
    val callNumber: String,
    val whatsapp: String,

    // More Screen
    val more: String,
    val settings: String,
    val about: String,
    val version: String,

    // Guest Profile
    val guest: String,
    val loginToAccess: String,
    val loginRequired: String,
    val loginRequiredMessage: String,
    val guestMessage: String
)

object Translations {
    val portuguese = AppStrings(
        appName = "MakeLifeBetter",
        username = "Email",
        password = "Senha",
        forgotPassword = "Esqueci a senha",
        signIn = "Entrar",
        createAccount = "Criar nova conta",

        register = "Criar Conta",
        name = "Nome",
        email = "Email",
        cpf = "CPF",
        phone = "Celular",
        confirmPassword = "Confirmar senha",
        requiredFields = "* Campos obrigatórios",
        acceptTerms = "Aceito os termos de compromisso",
        registerButton = "Cadastrar",
        backToLogin = "Voltar ao login",
        passwordMinLength = "A senha deve ter pelo menos 8 caracteres",
        passwordsDoNotMatch = "As senhas não coincidem",

        termsTitle = "Termos de Compromisso",
        termsText = "Ao utilizar este aplicativo, você concorda que o MakeLifeBetter poderá coletar suas informações pessoais para melhorar sua experiência e fornecer nossos serviços.\n\nSuas informações serão tratadas com segurança e não serão compartilhadas com terceiros sem seu consentimento.\n\nAo clicar em \"Concordar\", você declara estar de acordo com estes termos.",
        agree = "Concordar",

        forgotPasswordTitle = "Esqueci a Senha",
        confirm = "Confirmar",
        back = "Voltar",

        tempPasswordTitle = "Recuperação de senha",
        yourNewPassword = "Confira seu email",
        useTempPassword = "Enviamos um link de redefinição. Abra o email para continuar.",

        accessCode = "Código de acesso",
        invalidAccessCode = "Código de acesso inválido",

        selectLanguage = "Selecione o Idioma",
        portuguese = "Português (Brasil)",
        english = "English (US)",
        spanish = "Español",

        // Common
        cancel = "Cancelar",
        delete = "Excluir",
        error = "Erro",
        tryAgain = "Tentar novamente",
        save = "Salvar",
        send = "Enviar",
        loading = "Carregando...",
        success = "Sucesso",
        loginError = "Erro no Login",
        registerError = "Erro no Cadastro",
        errorMessage = "Por favor, verifique suas informações e tente novamente.",

        // Chat Screen
        generalList = "Lista Geral",
        questions = "Dúvidas",
        typeYourMessage = "Digite sua mensagem...",
        you = "Você",
        askQuestion = "Fazer uma pergunta",
        questionTitle = "Título da pergunta",
        description = "Descrição",
        post = "Publicar",
        deleteQuestion = "Excluir pergunta",
        deleteQuestionConfirm = "Tem certeza que deseja excluir esta pergunta?",
        by = "Por",
        replies = "respostas",
        questionDetails = "Detalhes da Pergunta",
        noRepliesYet = "Nenhuma resposta ainda. Seja o primeiro a responder!",
        writeYourReply = "Escreva sua resposta...",
        sendReply = "Enviar resposta",
        deleteReply = "Excluir resposta",

        // Profile Screen
        myProfile = "Meu Perfil",
        myOrders = "Meus Pedidos",
        seeYourOrderStatus = "Veja o status dos seus pedidos",
        personalInfo = "Informações pessoais",
        usernameLabel = "Nome de usuário",
        saveProfile = "Salvar perfil",
        changePassword = "Alterar senha",
        currentPassword = "Senha atual",
        newPassword = "Nova senha",
        confirmNewPassword = "Confirmar nova senha",
        profileUpdated = "Perfil atualizado com sucesso!",
        passwordChanged = "Senha alterada com sucesso!",
        accountId = "ID da conta",
        adminTools = "Ferramentas administrativas",
        adminOnlyAccess = "Acesso exclusivo para administradores.",
        openAdminPanel = "Abrir painel admin",
        signOut = "Sair da conta",

        // Store Screen
        store = "Loja",
        all = "Todos",
        noProductsFound = "Nenhum produto encontrado",
        emptyCart = "Carrinho vazio",
        cart = "Carrinho",
        addToCart = "Adicionar ao carrinho",
        removeFromCart = "Remover do carrinho",
        checkout = "Finalizar compra",
        total = "Total",
        quantity = "Quantidade",
        category = "Categoria",
        price = "Preço",
        available = "Disponível",
        unavailable = "Indisponível",
        orderConfirmed = "Pedido confirmado!",
        backToStore = "Voltar para a Loja",
        continueShopping = "Continuar comprando",
        orderDetails = "Detalhes do pedido",
        orderStatus = "Status do pedido",
        orderDate = "Data do pedido",
        orderTotal = "Total do pedido",
        items = "itens",
        finishOrder = "Finalizar pedido",
        clearCart = "Limpar carrinho",
        emptyCartMessage = "Seu carrinho está vazio",

        // Hire Me Screen
        hireMe = "Contrate-me!",
        mobileDeveloper = "Desenvolvedor Mobile iOS e Android Nativos (Swift e Kotlin)",
        experience = "Experiência",
        years = "anos",
        developerDescription = "Desenvolvedor de aplicativos nativos Android com 10 anos de experiência. Especializado em Kotlin, Jetpack Compose e arquiteturas modernas.",
        contactMe = "Entre em Contato",
        sendEmailNow = "Enviar Email Agora",
        contact = "Contato",

        // Event Details
        details = "Detalhes",
        information = "Informações",
        eventDescription = "Descrição do evento",
        location = "Localização",
        date = "Data",
        time = "Horário",

        // Map Screen
        map = "Mapa",
        eventLocation = "Local do Evento",
        openInMaps = "Abrir no Mapa",
        contacts = "Contatos",
        callNumber = "Ligar",
        whatsapp = "WhatsApp",

        // More Screen
        more = "Mais",
        settings = "Configurações",
        about = "Sobre",
        version = "Versão",

        // Guest Profile
        guest = "Visitante",
        loginToAccess = "Faça login para acessar",
        loginRequired = "Login necessário",
        loginRequiredMessage = "Para finalizar a compra, você precisa fazer login primeiro.",
        guestMessage = "Você está navegando como visitante. Faça login para acessar todas as funcionalidades."
    )

    val english = AppStrings(
        appName = "MakeLifeBetter",
        username = "Email",
        password = "Password",
        forgotPassword = "Forgot password",
        signIn = "Sign In",
        createAccount = "Create new account",

        register = "Create Account",
        name = "Name",
        email = "Email",
        cpf = "CPF",
        phone = "Phone",
        confirmPassword = "Confirm password",
        requiredFields = "* Required fields",
        acceptTerms = "I accept the terms and conditions",
        registerButton = "Register",
        backToLogin = "Back to login",
        passwordMinLength = "Password must be at least 8 characters",
        passwordsDoNotMatch = "Passwords do not match",

        termsTitle = "Terms and Conditions",
        termsText = "By using this application, you agree that MakeLifeBetter may collect your personal information to improve your experience and provide our services.\n\nYour information will be treated securely and will not be shared with third parties without your consent.\n\nBy clicking \"Agree\", you declare that you agree to these terms.",
        agree = "Agree",

        forgotPasswordTitle = "Forgot Password",
        confirm = "Confirm",
        back = "Back",

        tempPasswordTitle = "Password recovery",
        yourNewPassword = "Check your email",
        useTempPassword = "We sent a reset link. Open your email to continue.",

        accessCode = "Access code",
        invalidAccessCode = "Invalid access code",

        selectLanguage = "Select Language",
        portuguese = "Português (Brasil)",
        english = "English (US)",
        spanish = "Español",

        // Common
        cancel = "Cancel",
        delete = "Delete",
        error = "Error",
        tryAgain = "Try again",
        save = "Save",
        send = "Send",
        loading = "Loading...",
        success = "Success",
        loginError = "Login Error",
        registerError = "Registration Error",
        errorMessage = "Please check your information and try again.",

        // Chat Screen
        generalList = "General List",
        questions = "Questions",
        typeYourMessage = "Type your message...",
        you = "You",
        askQuestion = "Ask a question",
        questionTitle = "Question title",
        description = "Description",
        post = "Post",
        deleteQuestion = "Delete question",
        deleteQuestionConfirm = "Are you sure you want to delete this question?",
        by = "By",
        replies = "replies",
        questionDetails = "Question Details",
        noRepliesYet = "No replies yet. Be the first to respond!",
        writeYourReply = "Write your reply...",
        sendReply = "Send reply",
        deleteReply = "Delete reply",

        // Profile Screen
        myProfile = "My Profile",
        myOrders = "My Orders",
        seeYourOrderStatus = "See your order status",
        personalInfo = "Personal information",
        usernameLabel = "Username",
        saveProfile = "Save profile",
        changePassword = "Change password",
        currentPassword = "Current password",
        newPassword = "New password",
        confirmNewPassword = "Confirm new password",
        profileUpdated = "Profile updated successfully!",
        passwordChanged = "Password changed successfully!",
        accountId = "Account ID",
        adminTools = "Admin tools",
        adminOnlyAccess = "Admin-only access.",
        openAdminPanel = "Open admin panel",
        signOut = "Sign out",

        // Store Screen
        store = "Store",
        all = "All",
        noProductsFound = "No products found",
        emptyCart = "Empty cart",
        cart = "Cart",
        addToCart = "Add to cart",
        removeFromCart = "Remove from cart",
        checkout = "Checkout",
        total = "Total",
        quantity = "Quantity",
        category = "Category",
        price = "Price",
        available = "Available",
        unavailable = "Unavailable",
        orderConfirmed = "Order confirmed!",
        backToStore = "Back to Store",
        continueShopping = "Continue shopping",
        orderDetails = "Order details",
        orderStatus = "Order status",
        orderDate = "Order date",
        orderTotal = "Order total",
        items = "items",
        finishOrder = "Finish order",
        clearCart = "Clear cart",
        emptyCartMessage = "Your cart is empty",

        // Hire Me Screen
        hireMe = "Hire me!",
        mobileDeveloper = "Native iOS and Android Mobile Developer (Swift and Kotlin)",
        experience = "Experience",
        years = "years",
        developerDescription = "Native Android app developer with 10 years of experience. Specialized in Kotlin, Jetpack Compose and modern architectures.",
        contactMe = "Contact Me",
        sendEmailNow = "Send Email Now",
        contact = "Contact",

        // Event Details
        details = "Details",
        information = "Information",
        eventDescription = "Event description",
        location = "Location",
        date = "Date",
        time = "Time",

        // Map Screen
        map = "Map",
        eventLocation = "Event Location",
        openInMaps = "Open in Maps",
        contacts = "Contacts",
        callNumber = "Call",
        whatsapp = "WhatsApp",

        // More Screen
        more = "More",
        settings = "Settings",
        about = "About",
        version = "Version",

        // Guest Profile
        guest = "Guest",
        loginToAccess = "Log in to access",
        loginRequired = "Login required",
        loginRequiredMessage = "To complete your purchase, you need to log in first.",
        guestMessage = "You are browsing as a guest. Log in to access all features."
    )

    val spanish = AppStrings(
        appName = "MakeLifeBetter",
        username = "Correo electrónico",
        password = "Contraseña",
        forgotPassword = "Olvidé mi contraseña",
        signIn = "Iniciar sesión",
        createAccount = "Crear nueva cuenta",

        register = "Crear Cuenta",
        name = "Nombre",
        email = "Correo electrónico",
        cpf = "CPF",
        phone = "Teléfono",
        confirmPassword = "Confirmar contraseña",
        requiredFields = "* Campos obligatorios",
        acceptTerms = "Acepto los términos y condiciones",
        registerButton = "Registrarse",
        backToLogin = "Volver al inicio",
        passwordMinLength = "La contraseña debe tener al menos 8 caracteres",
        passwordsDoNotMatch = "Las contraseñas no coinciden",

        termsTitle = "Términos y Condiciones",
        termsText = "Al utilizar esta aplicación, usted acepta que MakeLifeBetter pueda recopilar su información personal para mejorar su experiencia y proporcionar nuestros servicios.\n\nSu información será tratada de forma segura y no será compartida con terceros sin su consentimiento.\n\nAl hacer clic en \"Aceptar\", declara estar de acuerdo con estos términos.",
        agree = "Aceptar",

        forgotPasswordTitle = "Olvidé mi Contraseña",
        confirm = "Confirmar",
        back = "Volver",

        tempPasswordTitle = "Recuperación de contraseña",
        yourNewPassword = "Revisa tu correo",
        useTempPassword = "Enviamos un enlace de restablecimiento. Abre tu correo para continuar.",

        accessCode = "Código de acceso",
        invalidAccessCode = "Código de acceso inválido",

        selectLanguage = "Seleccionar Idioma",
        portuguese = "Português (Brasil)",
        english = "English (US)",
        spanish = "Español",

        // Common
        cancel = "Cancelar",
        delete = "Eliminar",
        error = "Error",
        tryAgain = "Intentar de nuevo",
        save = "Guardar",
        send = "Enviar",
        loading = "Cargando...",
        success = "Éxito",
        loginError = "Error de Inicio de Sesión",
        registerError = "Error de Registro",
        errorMessage = "Por favor, verifica tu información e inténtalo de nuevo.",

        // Chat Screen
        generalList = "Lista General",
        questions = "Preguntas",
        typeYourMessage = "Escribe tu mensaje...",
        you = "Tú",
        askQuestion = "Hacer una pregunta",
        questionTitle = "Título de la pregunta",
        description = "Descripción",
        post = "Publicar",
        deleteQuestion = "Eliminar pregunta",
        deleteQuestionConfirm = "¿Estás seguro de que deseas eliminar esta pregunta?",
        by = "Por",
        replies = "respuestas",
        questionDetails = "Detalles de la Pregunta",
        noRepliesYet = "Aún no hay respuestas. ¡Sé el primero en responder!",
        writeYourReply = "Escribe tu respuesta...",
        sendReply = "Enviar respuesta",
        deleteReply = "Eliminar respuesta",

        // Profile Screen
        myProfile = "Mi Perfil",
        myOrders = "Mis Pedidos",
        seeYourOrderStatus = "Ver el estado de tus pedidos",
        personalInfo = "Información personal",
        usernameLabel = "Nombre de usuario",
        saveProfile = "Guardar perfil",
        changePassword = "Cambiar contraseña",
        currentPassword = "Contraseña actual",
        newPassword = "Nueva contraseña",
        confirmNewPassword = "Confirmar nueva contraseña",
        profileUpdated = "¡Perfil actualizado con éxito!",
        passwordChanged = "¡Contraseña cambiada con éxito!",
        accountId = "ID de la cuenta",
        adminTools = "Herramientas administrativas",
        adminOnlyAccess = "Acceso exclusivo para administradores.",
        openAdminPanel = "Abrir panel admin",
        signOut = "Cerrar sesión",

        // Store Screen
        store = "Tienda",
        all = "Todos",
        noProductsFound = "No se encontraron productos",
        emptyCart = "Carrito vacío",
        cart = "Carrito",
        addToCart = "Agregar al carrito",
        removeFromCart = "Quitar del carrito",
        checkout = "Finalizar compra",
        total = "Total",
        quantity = "Cantidad",
        category = "Categoría",
        price = "Precio",
        available = "Disponible",
        unavailable = "No disponible",
        orderConfirmed = "¡Pedido confirmado!",
        backToStore = "Volver a la Tienda",
        continueShopping = "Seguir comprando",
        orderDetails = "Detalles del pedido",
        orderStatus = "Estado del pedido",
        orderDate = "Fecha del pedido",
        orderTotal = "Total del pedido",
        items = "artículos",
        finishOrder = "Finalizar pedido",
        clearCart = "Vaciar carrito",
        emptyCartMessage = "Tu carrito está vacío",

        // Hire Me Screen
        hireMe = "¡Contrátame!",
        mobileDeveloper = "Desarrollador Mobile iOS y Android Nativos (Swift y Kotlin)",
        experience = "Experiencia",
        years = "años",
        developerDescription = "Desarrollador de aplicaciones nativas Android con 10 años de experiencia. Especializado en Kotlin, Jetpack Compose y arquitecturas modernas.",
        contactMe = "Contáctame",
        sendEmailNow = "Enviar Email Ahora",
        contact = "Contacto",

        // Event Details
        details = "Detalles",
        information = "Información",
        eventDescription = "Descripción del evento",
        location = "Ubicación",
        date = "Fecha",
        time = "Hora",

        // Map Screen
        map = "Mapa",
        eventLocation = "Ubicación del Evento",
        openInMaps = "Abrir en Mapas",
        contacts = "Contactos",
        callNumber = "Llamar",
        whatsapp = "WhatsApp",

        // More Screen
        more = "Más",
        settings = "Configuración",
        about = "Acerca de",
        version = "Versión",

        // Guest Profile
        guest = "Visitante",
        loginToAccess = "Inicia sesión para acceder",
        loginRequired = "Se requiere inicio de sesión",
        loginRequiredMessage = "Para completar tu compra, necesitas iniciar sesión primero.",
        guestMessage = "Estás navegando como visitante. Inicia sesión para acceder a todas las funcionalidades."
    )

    fun getStrings(language: Language): AppStrings {
        return when (language) {
            Language.PORTUGUESE -> portuguese
            Language.ENGLISH -> english
            Language.SPANISH -> spanish
        }
    }
}
