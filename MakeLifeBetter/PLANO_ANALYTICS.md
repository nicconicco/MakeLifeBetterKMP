# Plano de Analytics - MakeLifeBetter KMP

## Objetivo

Implementar rastreamento de analytics no app Android e iOS usando **Firebase Analytics**, cobrindo:

1. **Screen Views** - Quando cada tela aparece
2. **Button Actions** - Quando botoes de acao sao clicados
3. **Screen Time** - Tempo que o usuario ficou em cada tela
4. **Checkout Cart Summary** - Ao finalizar pedido, registrar quantidade de itens no carrinho

---

## Arquitetura

### Camada Shared (Common)

Criar uma interface `AnalyticsRepository` no modulo shared com metodos para logar eventos. Cada plataforma (Android/iOS) tera sua implementacao usando o SDK nativo do Firebase Analytics.

```
shared/src/commonMain/kotlin/.../repository/AnalyticsRepository.kt   (interface)
shared/src/androidMain/kotlin/.../repository/FirebaseAnalyticsRepository.kt (impl Android)
shared/src/iosMain/kotlin/.../repository/FirebaseAnalyticsRepository.kt     (impl iOS)
```

### Abordagem

- **Android**: Usa `com.google.firebase:firebase-analytics` diretamente
- **iOS**: Usa `FirebaseAnalytics` via Swift Package Manager (SPM) + GitLive KMP wrapper no shared

O rastreamento de screen time sera feito na camada de UI (Compose/SwiftUI), pois e la que sabemos quando a tela aparece e desaparece.

---

## Dependencias a Adicionar

### Android (`composeApp/build.gradle.kts`)
```kotlin
// androidMain.dependencies
implementation("com.google.firebase:firebase-analytics")
```

### iOS (Swift Package Manager)
No Xcode, adicionar o package `firebase-ios-sdk`:
1. File > Add Package Dependencies
2. URL: `https://github.com/firebase/firebase-ios-sdk`
3. Selecionar o produto **FirebaseAnalytics**

### Shared (`shared/build.gradle.kts`)
```kotlin
// Se usar GitLive KMP wrapper:
// commonMain: implementation("dev.gitlive:firebase-analytics:2.1.0")
// Ou implementar expect/actual direto com SDK nativo
```

---

## Interface AnalyticsRepository

```kotlin
interface AnalyticsRepository {
    // Screen views
    fun logScreenView(screenName: String)

    // Tempo na tela (em segundos)
    fun logScreenTime(screenName: String, durationSeconds: Long)

    // Acoes de botao
    fun logButtonClick(screenName: String, buttonName: String)

    // Evento de checkout (finalizar pedido)
    fun logCheckout(totalItems: Int, totalPrice: Double, paymentMethod: String)

    // Evento generico
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
}
```

---

## Eventos por Tela

### 1. LoginScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "login"` |
| Screen Time | `screen_time` | `screen_name = "login"`, `duration_seconds` |
| Button | `button_click` | `screen = "login"`, `button = "sign_in"` |
| Button | `button_click` | `screen = "login"`, `button = "create_account"` |
| Button | `button_click` | `screen = "login"`, `button = "forgot_password"` |
| Button | `button_click` | `screen = "login"`, `button = "language"` |

### 2. RegisterScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "register"` |
| Screen Time | `screen_time` | `screen_name = "register"`, `duration_seconds` |
| Button | `button_click` | `screen = "register"`, `button = "register"` |
| Button | `button_click` | `screen = "register"`, `button = "back"` |

### 3. ForgotPasswordScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "forgot_password"` |
| Screen Time | `screen_time` | `screen_name = "forgot_password"`, `duration_seconds` |
| Button | `button_click` | `screen = "forgot_password"`, `button = "send_reset"` |

### 4. HomeScreen (Container Principal)
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "home"` |
| Button | `button_click` | `screen = "home"`, `button = "tab_evento"` |
| Button | `button_click` | `screen = "home"`, `button = "tab_loja"` |
| Button | `button_click` | `screen = "home"`, `button = "tab_chat"` |
| Button | `button_click` | `screen = "home"`, `button = "tab_mapa"` |
| Button | `button_click` | `screen = "home"`, `button = "tab_more"` |

### 5. EventListScreen (SectionedList)
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "event_list"` |
| Screen Time | `screen_time` | `screen_name = "event_list"`, `duration_seconds` |
| Button | `button_click` | `screen = "event_list"`, `button = "event_card"`, `event_id` |

### 6. EventDetailScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "event_detail"`, `event_id` |
| Screen Time | `screen_time` | `screen_name = "event_detail"`, `duration_seconds` |
| Button | `button_click` | `screen = "event_detail"`, `button = "back"` |

### 7. StoreScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "store"` |
| Screen Time | `screen_time` | `screen_name = "store"`, `duration_seconds` |
| Button | `button_click` | `screen = "store"`, `button = "cart_icon"` |
| Button | `button_click` | `screen = "store"`, `button = "category_filter"`, `category` |
| Button | `button_click` | `screen = "store"`, `button = "product_card"`, `product_id` |
| Button | `button_click` | `screen = "store"`, `button = "quick_add_cart"`, `product_id` |

### 8. ProductDetailScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "product_detail"`, `product_id` |
| Screen Time | `screen_time` | `screen_name = "product_detail"`, `duration_seconds` |
| Button | `button_click` | `screen = "product_detail"`, `button = "add_to_cart"`, `product_id`, `quantity` |
| Button | `button_click` | `screen = "product_detail"`, `button = "quantity_increase"` |
| Button | `button_click` | `screen = "product_detail"`, `button = "quantity_decrease"` |
| Button | `button_click` | `screen = "product_detail"`, `button = "suggestion_click"`, `product_id` |
| Button | `button_click` | `screen = "product_detail"`, `button = "back"` |

### 9. CartScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "cart"` |
| Screen Time | `screen_time` | `screen_name = "cart"`, `duration_seconds` |
| Button | `button_click` | `screen = "cart"`, `button = "continue_to_payment"` |
| Button | `button_click` | `screen = "cart"`, `button = "back"` |

> **Nota**: Nao rastreamos add/remove/update no carrinho individualmente. O evento principal do carrinho e registrado no checkout (ver abaixo).

### 10. CheckoutScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "checkout"` |
| Screen Time | `screen_time` | `screen_name = "checkout"`, `duration_seconds` |
| Button | `button_click` | `screen = "checkout"`, `button = "confirm_payment"` |
| **Checkout** | **`checkout_complete`** | **`total_items`, `total_price`, `payment_method`** |
| Button | `button_click` | `screen = "checkout"`, `button = "back"` |

### 11. OrderConfirmationScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "order_confirmation"` |
| Screen Time | `screen_time` | `screen_name = "order_confirmation"`, `duration_seconds` |
| Button | `button_click` | `screen = "order_confirmation"`, `button = "back_to_store"` |

### 12. MyOrdersScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "my_orders"` |
| Screen Time | `screen_time` | `screen_name = "my_orders"`, `duration_seconds` |
| Button | `button_click` | `screen = "my_orders"`, `button = "back"` |

### 13. ProfileScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "profile"` |
| Screen Time | `screen_time` | `screen_name = "profile"`, `duration_seconds` |
| Button | `button_click` | `screen = "profile"`, `button = "save_profile"` |
| Button | `button_click` | `screen = "profile"`, `button = "change_password"` |
| Button | `button_click` | `screen = "profile"`, `button = "logout"` |
| Button | `button_click` | `screen = "profile"`, `button = "my_orders"` |

### 14. MoreScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "more"` |
| Screen Time | `screen_time` | `screen_name = "more"`, `duration_seconds` |
| Button | `button_click` | `screen = "more"`, `button = "profile"` |
| Button | `button_click` | `screen = "more"`, `button = "alarms"` |
| Button | `button_click` | `screen = "more"`, `button = "contact"` |

### 15. ChatScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "chat"` |
| Screen Time | `screen_time` | `screen_name = "chat"`, `duration_seconds` |
| Button | `button_click` | `screen = "chat"`, `button = "send_message"` |

### 16. MapScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "map"` |
| Screen Time | `screen_time` | `screen_name = "map"`, `duration_seconds` |

### 17. NotificationScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "notifications"` |
| Screen Time | `screen_time` | `screen_name = "notifications"`, `duration_seconds` |
| Button | `button_click` | `screen = "notifications"`, `button = "schedule_alarm"` |
| Button | `button_click` | `screen = "notifications"`, `button = "delete_alarm"` |

### 18. HireMeScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "hire_me"` |
| Screen Time | `screen_time` | `screen_name = "hire_me"`, `duration_seconds` |
| Button | `button_click` | `screen = "hire_me"`, `button = "send_contact"` |

### 19. LanguageScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "language"` |
| Screen Time | `screen_time` | `screen_name = "language"`, `duration_seconds` |
| Button | `button_click` | `screen = "language"`, `button = "select_language"`, `language` |

### 20. TermsScreen
| Tipo | Evento | Parametros |
|------|--------|------------|
| Screen View | `screen_view` | `screen_name = "terms"` |
| Screen Time | `screen_time` | `screen_name = "terms"`, `duration_seconds` |
| Button | `button_click` | `screen = "terms"`, `button = "accept_terms"` |

---

## Evento Especial: Checkout (Carrinho)

Quando o usuario finaliza o pedido (clica em "Confirmar Pagamento" e o pagamento e bem-sucedido), disparamos o evento `checkout_complete`:

```kotlin
analyticsRepository.logCheckout(
    totalItems = cart.totalItems,    // Ex: 5
    totalPrice = cart.totalPrice,    // Ex: 149.90
    paymentMethod = "stripe"         // ou "manual"
)
```

**Parametros do evento:**
| Parametro | Tipo | Descricao |
|-----------|------|-----------|
| `total_items` | Int | Quantidade total de itens no carrinho |
| `total_price` | Double | Valor total do pedido |
| `payment_method` | String | "stripe" (Android) ou "manual" (iOS) |

Este e o unico evento relacionado ao carrinho. Nao rastreamos adicionar/remover itens individualmente.

---

## Implementacao do Screen Time

### Como funciona

1. Quando a tela **aparece**, registramos o timestamp atual (`System.currentTimeMillis()`)
2. Quando a tela **desaparece** (navega para outra), calculamos a diferenca
3. Logamos o evento `screen_time` com a duracao em segundos

### Android (Compose)

```kotlin
@Composable
fun TrackScreenTime(screenName: String, analyticsRepository: AnalyticsRepository) {
    val startTime = remember { System.currentTimeMillis() }

    // Log screen view quando aparece
    LaunchedEffect(screenName) {
        analyticsRepository.logScreenView(screenName)
    }

    // Log screen time quando desaparece
    DisposableEffect(screenName) {
        onDispose {
            val duration = (System.currentTimeMillis() - startTime) / 1000
            analyticsRepository.logScreenTime(screenName, duration)
        }
    }
}
```

### iOS (SwiftUI)

```swift
struct ScreenTimeTracker: ViewModifier {
    let screenName: String
    let analytics: AnalyticsRepository
    @State private var startTime: Date?

    func body(content: Content) -> some View {
        content
            .onAppear {
                startTime = Date()
                analytics.logScreenView(screenName: screenName)
            }
            .onDisappear {
                if let start = startTime {
                    let duration = Int(Date().timeIntervalSince(start))
                    analytics.logScreenTime(screenName: screenName, durationSeconds: duration)
                }
            }
    }
}
```

---

## Arquivos a Criar/Modificar

### Novos Arquivos

| Arquivo | Descricao |
|---------|-----------|
| `shared/src/commonMain/.../repository/AnalyticsRepository.kt` | Interface do repositorio |
| `shared/src/androidMain/.../repository/FirebaseAnalyticsRepository.kt` | Implementacao Android |
| `shared/src/iosMain/.../repository/FirebaseAnalyticsRepository.kt` | Implementacao iOS |
| `shared/src/jvmMain/.../repository/NoOpAnalyticsRepository.kt` | Stub para Desktop |
| `shared/src/jsMain/.../repository/NoOpAnalyticsRepository.kt` | Stub para Web |
| `shared/src/wasmJsMain/.../repository/NoOpAnalyticsRepository.kt` | Stub para WASM |
| `composeApp/src/commonMain/.../components/ScreenTracker.kt` | Composable helper para screen view + time |

### Arquivos a Modificar

| Arquivo | Modificacao |
|---------|-------------|
| `gradle/libs.versions.toml` | Adicionar dependencia firebase-analytics |
| `shared/build.gradle.kts` | Adicionar dependencia firebase-analytics |
| `composeApp/build.gradle.kts` | Adicionar dependencia firebase-analytics |
| Todas as telas Compose (`*Screen.kt`) | Adicionar `ScreenTracker` e `logButtonClick` nos botoes |
| Todas as views SwiftUI (`*View.swift`) | Adicionar `.onAppear`/`.onDisappear` tracking |
| `SharedStoreViewModel.kt` | Adicionar `logCheckout` no fluxo de pagamento |
| `iosApp/iosApp.xcodeproj` | Adicionar `FirebaseAnalytics` via Swift Package Manager |

---

## Resumo dos Eventos Firebase

| Evento | Quando | Parametros Chave |
|--------|--------|------------------|
| `screen_view` | Tela aparece | `screen_name` |
| `screen_time` | Tela desaparece | `screen_name`, `duration_seconds` |
| `button_click` | Botao clicado | `screen_name`, `button_name` |
| `checkout_complete` | Pedido finalizado | `total_items`, `total_price`, `payment_method` |

---

## Ordem de Implementacao

### Fase 1 - Shared (Base comum)
1. Criar `AnalyticsRepository` interface no commonMain
2. Criar stubs `NoOpAnalyticsRepository` para Desktop/Web/WASM
3. Adicionar dependencia firebase-analytics no `gradle/libs.versions.toml` e `shared/build.gradle.kts`

### Fase 2 - Android (Primeiro)
4. Adicionar dependencia firebase-analytics no `composeApp/build.gradle.kts`
5. Criar `FirebaseAnalyticsRepository` no androidMain
6. Criar composable `ScreenTracker` para Compose
7. Integrar `ScreenTracker` em todas as telas Compose (screen view + screen time)
8. Adicionar `logButtonClick` em todos os botoes de acao das telas Compose
9. Adicionar `logCheckout` no fluxo de pagamento do `SharedStoreViewModel`
10. Testar no Android (DebugView do Firebase)

### Fase 3 - iOS (Depois)
11. Adicionar `FirebaseAnalytics` via Swift Package Manager no Xcode
12. Criar `FirebaseAnalyticsRepository` no iosMain
13. Criar `ViewModifier` `ScreenTimeTracker` para SwiftUI
14. Integrar tracking em todas as views SwiftUI (screen view + screen time)
15. Adicionar `logButtonClick` em todos os botoes de acao das views SwiftUI
16. Testar no iOS (DebugView do Firebase)
