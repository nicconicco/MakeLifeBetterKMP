# MakeLifeBetter - Roteiro de Apresentacao

## Aplicativo Multiplataforma de Gestao de Eventos e Comunidade

**Autor:** Carlos Nicolau Galves
**Tecnologia Principal:** Kotlin Multiplatform (KMP) + Compose Multiplatform
**Plataformas:** Android, iOS, Web (WASM/JS), Desktop

---

## PARTE 1 - INTRODUCAO (2 min)

### Slide 1: Abertura

**O que e o MakeLifeBetter?**

Um aplicativo completo de gestao de eventos e engajamento de comunidade, construido com uma unica base de codigo compartilhada em Kotlin que roda em Android, iOS, Web e Desktop.

O app permite que organizadores publiquem eventos, vendam produtos em uma loja integrada com pagamento real via Stripe, comuniquem-se com participantes via chat em tempo real, e muito mais.

**Por que esse projeto existe?**

Para demonstrar na pratica que e possivel construir um app de producao real — com autenticacao, pagamentos, mapas, notificacoes, chat em tempo real — usando Kotlin Multiplatform, compartilhando a maior parte da logica de negocios entre todas as plataformas.

---

## PARTE 2 - ARQUITETURA GERAL (5 min)

### Slide 2: Visao Geral da Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                     CLIENTES                        │
│  ┌──────────┐ ┌──────────┐ ┌──────┐ ┌──────────┐  │
│  │ Android  │ │   iOS    │ │ Web  │ │ Desktop  │   │
│  │ Compose  │ │ SwiftUI  │ │ WASM │ │ Compose  │   │
│  └────┬─────┘ └────┬─────┘ └──┬───┘ └────┬─────┘  │
│       │             │          │           │        │
│  ┌────▼─────────────▼──────────▼───────────▼────┐   │
│  │         KOTLIN MULTIPLATFORM (shared)        │   │
│  │  ViewModels | Repositories | Models | Utils  │   │
│  └────────────────────┬────────────────────────┘   │
│                       │                             │
│  ┌────────────────────▼────────────────────────┐   │
│  │         composeApp (Screens / Components)    │   │
│  └──────────────────────────────────────────────┘   │
└───────────────────────┬─────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────┐
│               GOOGLE CLOUD / FIREBASE               │
│  ┌──────────┐ ┌───────────┐ ┌──────────────────┐   │
│  │ Firebase │ │  Cloud    │ │ Cloud Functions  │   │
│  │   Auth   │ │ Firestore │ │  (Node.js 22)   │   │
│  └──────────┘ └───────────┘ └──────────────────┘   │
│  ┌──────────┐ ┌───────────┐ ┌──────────────────┐   │
│  │ Realtime │ │  Remote   │ │ Secret Manager   │   │
│  │ Database │ │  Config   │ │ (API Keys)       │   │
│  └──────────┘ └───────────┘ └──────────────────┘   │
└─────────────────────────────────────────────────────┘
```

> **Imagem de referencia:** `evidence/project_structure.png` e `evidence/project_structure_2.png`

### Slide 3: O Padrao MVVM + Repository

O projeto segue **MVVM (Model-View-ViewModel)** com **Repository Pattern**:

- **Model:** Data classes em `shared/commonMain/model/` — representam eventos, produtos, pedidos, mensagens, etc.
- **ViewModel:** Classes compartilhadas em `shared/commonMain/viewmodel/` — toda a logica de negocios vive aqui, em Kotlin puro.
- **View:** UI nativa por plataforma — Compose no Android/Desktop/Web, SwiftUI no iOS.
- **Repository:** Interfaces em `commonMain`, implementacoes especificas em `androidMain` e `iosMain` usando Firebase.

**Beneficio:** A logica e escrita uma unica vez. Se uma regra de negocios muda, muda em um lugar so.

### Slide 4: Estrutura de Modulos

| Modulo | Funcao |
|--------|--------|
| `shared` | ViewModels, Repositories (interfaces + implementacoes), Models, Utils — o coracao do app |
| `composeApp` | Telas Compose Multiplatform (Android, Desktop, Web), integracao nativa Android |
| `iosApp` | App iOS em SwiftUI que consome o modulo `shared` via wrappers Swift |
| `server` | Backend Ktor (JVM) — suporte futuro |
| `functions` | Cloud Functions Firebase (Node.js 22) — pagamentos, admin, configs |

---

## PARTE 3 - STACK TECNOLOGICA DETALHADA (5 min)

### Slide 5: Linguagens e Frameworks Core

| Tecnologia | Versao | Papel |
|------------|--------|-------|
| **Kotlin** | 2.3.0 | Linguagem principal — compartilhada entre todas as plataformas |
| **Compose Multiplatform** | 1.10.0 | Framework de UI declarativa — Android, Desktop, Web |
| **SwiftUI** | iOS 15+ | UI nativa no iOS, consumindo ViewModels Kotlin |
| **Kotlin Coroutines** | 1.10.2 | Programacao assincrona e concorrencia estruturada |
| **Kotlin Serialization** | 1.7.3 | Serializacao JSON multiplataforma |
| **Kotlin DateTime** | 0.7.1 | Manipulacao de datas multiplataforma |

### Slide 6: Backend e Servicos Firebase

| Servico Firebase | Uso no App |
|------------------|------------|
| **Firebase Auth** | Login, registro, recuperacao de senha, custom claims para admin |
| **Cloud Firestore** | Banco principal — eventos, produtos, categorias, pedidos, carrinho, perguntas |
| **Realtime Database** | Chat em tempo real — mensagens com atualizacao instantanea |
| **Cloud Functions** | 7 funcoes serverless — pagamentos Stripe, config segura, admin ops |
| **Remote Config** | Feature flags e configuracao remota do app |
| **Secret Manager** | Armazenamento seguro de API keys (Maps, Stripe) |

> **Imagem de referencia:** `images_app/firebase.jpeg` — Console Firebase com ambos os apps (Android + iOS) registrados no plano Blaze.

### Slide 7: Bibliotecas e SDKs

**Rede e Imagens:**
| Biblioteca | Versao | Funcao |
|------------|--------|--------|
| Ktor Client | 3.3.3 | HTTP client multiplataforma (OkHttp no Android, Darwin no iOS) |
| Coil 3 | 3.1.0 | Carregamento e cache de imagens multiplataforma |

**Pagamentos:**
| Biblioteca | Versao | Funcao |
|------------|--------|--------|
| Stripe Android SDK | 21.5.0 | Payment Sheet nativo no Android |
| StripePaymentSheet (SPM) | Latest | Payment Sheet nativo no iOS |

**Mapas:**
| Biblioteca | Versao | Funcao |
|------------|--------|--------|
| Google Maps Compose | 6.4.1 | Mapas nativos no Android |
| Play Services Maps | 19.0.0 | Servicos Google Maps |
| MapKit (nativo) | iOS 15+ | Mapas nativos no iOS |

**Android Especifico:**
| Biblioteca | Versao | Funcao |
|------------|--------|--------|
| AndroidX Core | 1.17.0 | Utilitarios Android |
| AndroidX Activity Compose | 1.12.2 | Integracao Activity + Compose |
| AndroidX Lifecycle | 2.9.6 | Gerenciamento de ciclo de vida |
| Apache POI | 5.2.5 | Importacao de planilhas Excel (.xlsx) |

**Firebase Multiplataforma (GitLive SDK):**
| Biblioteca | Versao |
|------------|--------|
| firebase-auth | 2.1.0 |
| firebase-firestore | 2.1.0 |
| firebase-database | 2.1.0 |
| firebase-config | 2.1.0 |

### Slide 8: Build e Tooling

| Ferramenta | Versao | Funcao |
|------------|--------|--------|
| **Gradle** | 8.14.3 | Build system com Kotlin DSL |
| **AGP** | 8.11.2 | Android Gradle Plugin |
| **JDK** | 17 | Target de compilacao |
| **Compile SDK** | 36 (Android 16) | Nivel de API mais recente |
| **Min SDK** | 26 (Android 8.0) | Suporte minimo |
| **Xcode** | Latest | Build iOS |
| **Swift Package Manager** | - | Gerenciamento de dependencias iOS (migrado do CocoaPods) |

---

## PARTE 4 - FUNCIONALIDADES DO APP (10 min)

### Slide 9: Autenticacao e Perfil

> **Imagem de referencia:** `images_app/ios.jpeg` — Telas iOS de Login, Boas-vindas, Registro e Recuperacao de Senha

**Funcionalidades:**
- Login com email/senha via Firebase Auth
- Registro com validacao e termos de compromisso
- Recuperacao de senha por email
- Edicao de perfil (nome, email, senha)
- Selecao de idioma (Portugues, Ingles, Espanhol) com bandeira do pais
- Modo convidado com acesso limitado

**Detalhes tecnicos:**
- `SharedLoginViewModel` gerencia todo o estado de autenticacao
- Sealed classes tipadas: `AuthResult.Idle | Loading | Success | Error`
- StateFlow reativo — a UI reage automaticamente a mudancas de estado
- Custom Claims no Firebase Auth para controle de acesso admin

### Slide 10: Eventos — Tela Principal

> **Imagem de referencia:** `images_app/allscreens.jpeg` — Tela de eventos mostrando "O que ta rolando agora" e "Ainda vai rolar"

**Funcionalidades:**
- Lista de eventos agrupados por secao:
  - "O que ta rolando agora" (eventos em andamento)
  - "Ainda vai rolar" (eventos futuros)
  - "Novidades" (lancamentos, SDK, etc.)
- Cada evento mostra: titulo, categoria (Live Coding, Networking...), horario, local (Sala 30, Rooftop, etc.)
- Filtragem por categoria em tempo real

**Detalhes tecnicos:**
- `SharedEventViewModel` com `EventSectionsResult` para secoes dinamicas
- Dados carregados do Cloud Firestore (colecao `eventos`)
- Categorias vindas da colecao `categorias`

### Slide 11: Mapa e Localizacao

> **Imagem de referencia:** `images_app/allscreens.jpeg` — Tela de mapa com pin em Curitiba, Parana, Brasil

**Funcionalidades:**
- Mapa interativo mostrando a localizacao do evento
- Endereco completo: "Rua Emirados Arabes 354 - Curitiba"
- Contatos do evento com telefone
- Animacao de camera para a localizacao

**Detalhes tecnicos:**
- Android: Google Maps Compose com marcador customizado
- iOS: MapKit nativo com anotacao
- `MapViewModel` com cache de sessao — busca localizacao uma unica vez para economizar chamadas de API
- API Key protegida via Firebase Secret Manager + Cloud Function `getSecureConfig`

### Slide 12: Chat em Tempo Real

> **Imagem de referencia:** `images_app/allscreens.jpeg` — Tela de chat com "Lista Geral" e "Duvidas"

**Funcionalidades:**
- Chat geral da comunidade com mensagens em tempo real
- Identificacao do autor e timestamp
- Sistema de Perguntas e Respostas (Q&A):
  - Criar perguntas com titulo e descricao
  - Responder perguntas existentes
  - Contador de respostas por pergunta
  - Deletar proprias perguntas e respostas

**Detalhes tecnicos:**
- Chat usa **Firebase Realtime Database** (nao Firestore) — otimizado para baixa latencia
- Q&A usa **Cloud Firestore** com subcollections (`duvidas/{questionId}/respostas`)
- `SharedChatViewModel` gerencia ambos os sistemas
- Listeners em tempo real com `Flow` do Kotlin

> **Imagem de referencia:** `evidence/realtime_database_chat.png` — Estrutura do Realtime Database no console Firebase

### Slide 13: Loja e Catalogo de Produtos

**Funcionalidades:**
- Catalogo de produtos com filtragem por categoria
- Detalhe do produto com:
  - Caracteristicas
  - Curiosidades
  - Sugestoes de harmonizacao (para vinhos)
  - Preco e disponibilidade
- Carrinho de compras com controle de quantidade
- Carrinho funciona ate como convidado (sincroniza ao fazer login)

**Detalhes tecnicos:**
- `SharedStoreViewModel` — gerencia produtos, categorias, carrinho e pedidos
- Produtos na colecao `produtos` do Firestore
- Carrinho em subcollection `carrinho/{userId}/items`
- Imagens carregadas via Coil 3 com cache

### Slide 14: Checkout e Pagamento com Stripe

**Funcionalidades:**
- Formulario de endereco completo (rua, numero, bairro, cidade, estado, CEP)
- Payment Sheet nativo do Stripe (cartao de credito)
- Confirmacao de pedido com status
- Historico de pedidos ("Meus Pedidos")

**Detalhes tecnicos — Fluxo de pagamento em 3 camadas:**

```
1. App (Android/iOS)
   └─> SharedStoreViewModel.createPaymentIntent()
       └─> Chama Cloud Function "createPaymentIntent"

2. Cloud Function (Node.js 22)
   └─> Cria/busca Stripe Customer (vinculado ao Firebase UID)
   └─> Cria EphemeralKey para o customer
   └─> Cria PaymentIntent com valor em centavos
   └─> Retorna: clientSecret, ephemeralKey, customerId

3. App recebe os dados
   └─> Abre Stripe Payment Sheet com clientSecret
   └─> Usuario paga
   └─> Apos sucesso: checkoutAfterPayment(endereco, paymentIntentId)
   └─> Pedido salvo no Firestore com metodo "stripe" e ID do intent
```

- Android: `com.stripe:stripe-android:21.5.0`
- iOS: `StripePaymentSheet` via Swift Package Manager
- Chave secreta Stripe armazenada no **Firebase Secret Manager**
- JWT Token configurado para autenticacao segura

### Slide 15: Notificacoes Locais

**Funcionalidades:**
- Lembretes automaticos 5 minutos antes de cada evento
- Gerenciamento de permissoes por plataforma
- Tela de notificacoes com lido/nao lido
- Acao de dispensar notificacoes

**Detalhes tecnicos:**
- `SharedNotificationViewModel` — logica compartilhada de agendamento
- Android: `AlarmManager` + `BroadcastReceiver` (`NotificationReceiver.kt`)
- iOS: `UNUserNotificationCenter`
- Permissao `POST_NOTIFICATIONS` (Android 13+) e `UNNotificationCenter.requestAuthorization` (iOS)
- `TimeUtils` calcula o momento exato de disparo a partir do horario do evento

### Slide 16: Perfil e Configuracoes

> **Imagem de referencia:** `images_app/allscreens.jpeg` — Tela "Meu Perfil" com edicao de nome, email e senha

**Funcionalidades:**
- Visualizar e editar username e email
- Alterar senha (senha atual + nova senha + confirmacao)
- Selecao de idioma do app (PT/EN/ES)
- Termos e condicoes
- Logout

### Slide 17: Painel Administrativo (Secret Screen)

**Funcionalidades:**
- Tela oculta acessivel apenas por admins
- Bootstrap do primeiro admin (operacao unica)
- Importacao em massa via planilha Excel (.xlsx)
- Deletar todos os dados do banco
- Popular dados de exemplo

**Detalhes tecnicos:**
- Custom Claims do Firebase Auth (`admin: true`)
- Cloud Functions: `bootstrapFirstAdmin`, `setAdminClaim`, `checkAdminStatus`, `adminDeleteAllData`
- Apache POI para leitura de arquivos .xlsx no Android
- Batch operations no Firestore para performance

---

## PARTE 5 - FASES DE DESENVOLVIMENTO (5 min)

### Slide 18: Timeline de Desenvolvimento

O projeto foi construido em fases iterativas, cada uma adicionando camadas de funcionalidade:

#### Fase 1 — Fundacao e Autenticacao
**Commits:** `Initial commit` ate `recovery password working`

- Setup inicial do projeto Kotlin Multiplatform
- Criacao do modulo `shared` com ViewModels compartilhados
- Firebase Auth integrado no Android (login, registro, recuperacao de senha)
- Replicacao das telas de autenticacao no iOS com SwiftUI
- Bottom navigation e perfil do usuario

#### Fase 2 — Funcionalidades Core do Evento
**Commits:** `adding static notification` ate `populating firebase`

- Sistema de notificacoes locais (Android + iOS)
- Integracao com Google Maps (Android) e MapKit (iOS)
- Chat geral em tempo real via Firebase Realtime Database
- Sistema de Perguntas e Respostas (Q&A)
- Tela secreta de administracao
- Importacao de dados via Excel
- Coleta de dados de localizacao do Firebase

#### Fase 3 — iOS Parity e Polish
**Commits:** `iOS WIP Event screen` ate `Upload notifications`

- Telas de eventos no iOS
- Icone do app customizado
- Correcao de horarios no iOS
- Mapas funcionando no iOS
- Upload de evidencias e documentacao

#### Fase 4 — Loja e E-Commerce
**Commits:** `WIp creating store` ate `store showing image`

- Catalogo de produtos com categorias
- Tela de detalhe de produto
- Carrinho de compras
- Visualizacao de produtos com imagens via Coil
- Chat em tempo real consolidado entre Android e iOS

#### Fase 5 — Configuracao Remota e Refatoracao
**Commits:** `WIP remote config` ate `cache map`

- Firebase Remote Config para feature flags
- Temas remotos configurados via Firebase
- Migracao de CocoaPods para Swift Package Manager no iOS
- Refatoracao de arquitetura (Clean Architecture)
- Melhoria de seguranca de rede
- Cache do mapa para economizar chamadas de API

#### Fase 6 — Pagamentos e Producao
**Commits:** `refactor arch` ate `Android version stripe working`

- Refatoracao completa da arquitetura Android e iOS
- Integracao Stripe com Cloud Functions
- JWT Token para autenticacao segura
- Payment Sheet funcionando no Android e iOS
- Tela de checkout com endereco completo
- Historico de pedidos

---

## PARTE 6 - COMO O CODIGO E COMPARTILHADO (3 min)

### Slide 19: Expect/Actual e Compartilhamento

```
shared/src/
├── commonMain/          # Codigo 100% compartilhado
│   ├── model/           # Data classes (Event, Product, Order...)
│   ├── viewmodel/       # Logica de negocios (7 ViewModels)
│   ├── repository/      # Interfaces dos repositorios
│   ├── auth/            # Sealed classes de autenticacao
│   └── util/            # Utilitarios (TimeUtils, Strings)
│
├── androidMain/         # Implementacoes Firebase para Android
│   ├── repository/      # FirebaseXxxRepository.kt (10 repos)
│   └── notification/    # NotificationScheduler (AlarmManager)
│
└── iosMain/             # Implementacoes Firebase para iOS
    ├── repository/      # FirebaseXxxRepository.kt (10 repos)
    └── notification/    # Scheduling via UNNotificationCenter
```

**O pattern de Bridge no iOS:**

```
Kotlin (shared)                    Swift (iosApp)
─────────────────                  ─────────────
SharedLoginViewModel               LoginViewModel.swift
  ├─ login()                        ├─ @Observable
  ├─ register()          ──bridge── ├─ wraps SharedLoginViewModel
  └─ StateFlow<AuthResult>          └─ DispatchQueue callbacks
```

Os ViewModels Swift sao wrappers finos que convertem `Flow` do Kotlin para callbacks compatíveis com SwiftUI.

### Slide 20: Numeros do Projeto

| Metrica | Valor |
|---------|-------|
| Arquivos de repositorio | ~108 |
| Telas Compose | ~71 arquivos |
| ViewModels compartilhados | 7 |
| ViewModels Swift (bridge) | 6 |
| Views SwiftUI | 18+ |
| Models/Data classes | 12 principais |
| Cloud Functions | 7 |
| Colecoes Firestore | 10+ |
| Plataformas suportadas | 5 (Android, iOS, Web WASM, Web JS, Desktop) |

---

## PARTE 7 - SEGURANCA (2 min)

### Slide 21: Camadas de Seguranca

1. **Firebase Security Rules** — Controle de acesso por colecao:
   - Leitura publica para eventos e produtos
   - Escrita restrita a admins (validacao de custom claims)
   - Dados do usuario acessiveis apenas ao proprio usuario

2. **Firebase Auth Custom Claims** — Sistema de permissoes:
   - `admin: true` para operacoes administrativas
   - Verificacao tanto no client quanto nas Security Rules

3. **Cloud Functions como Gateway** — Operacoes sensiveis nunca rodam no client:
   - Pagamentos Stripe processados server-side
   - API keys nunca expostas ao client
   - JWT Token para autenticacao segura

4. **Secret Manager** — Chaves sensiveis armazenadas com seguranca:
   - `STRIPE_SECRET_KEY`
   - `MAPS_API_KEY`
   - Acessiveis apenas pelas Cloud Functions

5. **ProGuard** — Ofuscacao de codigo no release Android:
   - Minificacao ativada (`isMinifyEnabled = true`)
   - Shrink de recursos (`isShrinkResources = true`)

---

## PARTE 8 - DEMONSTRACAO / EVIDENCIAS (3 min)

### Slide 22: Telas do App Android

> **Imagem:** `images_app/allscreens.jpeg`

Mostrar as 5 telas principais:
1. **Login** — Campo usuario/senha, opcao de criar conta
2. **Eventos** — Lista categorizada com "O que ta rolando" e "Ainda vai rolar"
3. **Mapa** — Localizacao do evento em Curitiba com contatos
4. **Perfil** — Edicao de dados pessoais e alteracao de senha
5. **Chat** — Lista geral de mensagens e aba de duvidas

### Slide 23: Telas do App iOS

> **Imagem:** `images_app/ios.jpeg`

Mostrar as 4 telas iOS:
1. **Login** — Design iOS nativo com bandeira do Brasil
2. **Boas-vindas** — "Bem-vindo, angela!" com email e botao sair
3. **Criar Conta** — Formulario com termos de compromisso
4. **Forgot Password** — Recuperacao por email

### Slide 24: Firebase Console

> **Imagem:** `images_app/firebase.jpeg`

- Projeto "MakeLifeBetter" no plano Blaze
- 2 apps registrados: Android + iOS
- Servicos ativos: Auth, Firestore, Realtime Database, Functions, Remote Config

### Slide 25: Videos de Evidencia

| Video | O que demonstra |
|-------|-----------------|
| `evidence/App_full_stage_2.mov` | Fluxo completo do app |
| `evidence/realtime_chat.mov` | Chat em tempo real Android + iOS |
| `evidence/evidence_store_working_3_platforms.mov` | Loja funcionando em 3 plataformas |
| `evidence/remote_config_evidence.mov` | Feature flags com Remote Config |
| `evidence/stripe_evidence_android.mov` | Pagamento Stripe no Android |
| `evidence/web_admin_panel.mov` | Painel admin na web |

---

## PARTE 9 - CONCLUSAO (2 min)

### Slide 26: O que foi alcancado

- App de producao real com **5 plataformas** a partir de uma base de codigo
- **Pagamentos reais** com Stripe integrado em Android e iOS
- **Chat em tempo real** com Firebase Realtime Database
- **Seguranca em multiplas camadas**: Auth, Rules, Cloud Functions, Secret Manager
- **Arquitetura escalavel**: MVVM + Repository com separacao clara de responsabilidades
- **Codigo compartilhado**: ViewModels, Models e logica de negocios escritos uma unica vez

### Slide 27: Proximos Passos

- CI/CD com GitHub Actions para gerar APK automaticamente
- Firebase App Distribution para distribuicao de testes
- Analytics e monitoramento de uso
- Testes automatizados (unitarios e de integracao)
- Push Notifications via Firebase Cloud Messaging

---

## ANEXO: Resumo Tecnico Rapido

```
Linguagem:        Kotlin 2.3.0 + Swift (iOS)
UI Framework:     Compose Multiplatform 1.10.0 + SwiftUI
Backend:          Firebase (Auth, Firestore, Realtime DB, Functions, Remote Config)
Pagamentos:       Stripe (Android SDK 21.5.0 + iOS SPM)
Mapas:            Google Maps Compose 6.4.1 + MapKit (iOS)
HTTP Client:      Ktor 3.3.3
Imagens:          Coil 3.1.0
DI:               Factory pattern (sem framework pesado)
Arquitetura:      MVVM + Repository Pattern
Estado:           StateFlow + Sealed Classes
Build:            Gradle 8.14.3 + Kotlin DSL
Min Android:      API 26 (Android 8.0)
Min iOS:          iOS 15
Cloud Functions:  Node.js 22 (7 funcoes)
```
