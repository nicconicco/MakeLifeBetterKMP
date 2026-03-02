# Plano: Integrar Stripe Payment Sheet no Android (Modo Teste/Sandbox)

## Contexto
O usuario criou uma conta na Stripe e quer integrar pagamento real no app Android, em modo sandbox (teste). Atualmente o checkout coleta dados de cartao manualmente e salva o pedido no Firestore sem processar pagamento. A integracao usara o **Stripe Payment Sheet** (UI pronta do Stripe) para processar pagamentos de forma segura, com uma **Cloud Function** no backend para criar o PaymentIntent.

O formulario de endereco sera mantido na tela atual de checkout. Apenas a secao de cartao sera substituida pelo Stripe.

## Pre-requisitos (usuario)
1. No Stripe Dashboard (https://dashboard.stripe.com), ativar "Test mode"
2. Em **Developers > API keys**, copiar:
   - **Publishable key** (`pk_test_...`) → vai no app Android
   - **Secret key** (`sk_test_...`) → vai na Cloud Function
3. Configurar a secret key:
   ```bash
   cd functions/
   firebase functions:secrets:set STRIPE_SECRET_KEY
   # Colar: sk_test_...
   ```
4. Adicionar no `local.properties`:
   ```
   STRIPE_PUBLISHABLE_KEY=pk_test_...
   ```

---

## Etapas de Implementacao

### Etapa 1: Cloud Function `createPaymentIntent`

**Arquivo modificado:** `functions/src/index.ts`
**Arquivo modificado:** `functions/package.json` (adicionar `"stripe": "^17.0.0"`)

- Adicionar `import Stripe from "stripe"` e `defineSecret("STRIPE_SECRET_KEY")`
- Nova funcao `createPaymentIntent` (onCall, autenticada):
  - Recebe: `{ amount: number }` (em centavos, ex: R$50.00 = 5000)
  - Cria Customer no Stripe com metadata do Firebase UID
  - Cria EphemeralKey para o customer
  - Cria PaymentIntent com `automatic_payment_methods: { enabled: true }`, currency `brl`
  - Retorna: `{ paymentIntent: clientSecret, ephemeralKey: secret, customer: id }`

### Etapa 2: `PaymentRepository` no shared module

**Arquivos novos:**
- `shared/src/commonMain/.../repository/PaymentRepository.kt` — Interface + data class `PaymentIntentData`
- `shared/src/commonMain/.../repository/PaymentRepositoryFactory.kt` — `expect fun createPaymentRepository()`
- `shared/src/androidMain/.../repository/FirebasePaymentRepository.kt` — Chama Cloud Function via `Firebase.functions.httpsCallable("createPaymentIntent")`
- `shared/src/androidMain/.../repository/PaymentRepositoryFactory.android.kt` — actual
- `shared/src/iosMain/.../repository/FirebasePaymentRepository.kt` — Mesma implementacao (gitlive funciona no iOS)
- `shared/src/iosMain/.../repository/PaymentRepositoryFactory.ios.kt` — actual
- Stubs para `jvmMain`, `jsMain`, `wasmJsMain` — retornam `Result.failure(UnsupportedOperationException())`

Segue o padrao existente de `CartRepositoryFactory.kt` e `OrderRepositoryFactory.kt`.

### Etapa 3: `PaymentIntentResult` no StoreResult

**Arquivo modificado:** `shared/src/commonMain/.../store/StoreResult.kt`

Adicionar:
```kotlin
sealed class PaymentIntentResult {
    data object Idle : PaymentIntentResult()
    data object Loading : PaymentIntentResult()
    data class Success(val data: PaymentIntentData) : PaymentIntentResult()
    data class Error(val message: String) : PaymentIntentResult()
}
```

### Etapa 4: Novos metodos no SharedStoreViewModel

**Arquivo modificado:** `shared/src/commonMain/.../viewmodel/SharedStoreViewModel.kt`

- Adicionar `PaymentRepository` como dependencia
- Novo StateFlow: `paymentIntentState: StateFlow<PaymentIntentResult>`
- Novo metodo: `createPaymentIntent()` — calcula centavos do cart, chama `paymentRepository.createPaymentIntent()`
- Novo metodo: `checkoutAfterPayment(address: Address, stripePaymentIntentId: String)` — cria o pedido no Firestore apos pagamento confirmado pelo Stripe
- Novo metodo: `resetPaymentIntentState()`
- Novo metodo observer para iOS: `observePaymentIntentState()`

### Etapa 5: Novo metodo `checkoutWithStripe` no CartRepository

**Arquivo modificado:** `shared/src/commonMain/.../repository/CartRepository.kt` — adicionar na interface
**Arquivo modificado:** `shared/src/androidMain/.../repository/FirebaseCartRepository.kt` — implementar
**Arquivo modificado:** `shared/src/iosMain/.../repository/FirebaseCartRepository.kt` — implementar

Semelhante ao `checkoutWithInfo`, mas salva `payment: { method: "stripe", paymentIntentId: "..." }` em vez dos dados do cartao.

### Etapa 6: SharedStoreViewModelWrapper (iOS)

**Arquivo modificado:** `shared/src/iosMain/.../viewmodel/SharedStoreViewModelHelper.kt`

- Adicionar observer `observePaymentIntentState()` e jobs correspondentes
- Expor novos metodos: `createPaymentIntent()`, `checkoutAfterPayment()`, `resetPaymentIntentState()`

### Etapa 7: Dependencia Stripe SDK no Android

**Arquivo modificado:** `gradle/libs.versions.toml`
```toml
stripe = "21.5.0"  # Em [versions]
stripe-android = { module = "com.stripe:stripe-android", version.ref = "stripe" }  # Em [libraries]
```

**Arquivo modificado:** `composeApp/build.gradle.kts`
- Em `androidMain.dependencies`: `implementation(libs.stripe.android)`
- Em `defaultConfig`: `buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", ...)`
- Habilitar `buildFeatures { buildConfig = true }` no bloco `android`

### Etapa 8: Inicializar Stripe na MainActivity

**Arquivo modificado:** `composeApp/src/androidMain/.../MainActivity.kt`
- Importar `com.stripe.android.PaymentConfiguration`
- No `onCreate`, antes do `setContent`: `PaymentConfiguration.init(applicationContext, BuildConfig.STRIPE_PUBLISHABLE_KEY)`

### Etapa 9: Componente expect/actual `PaymentSection`

**Arquivo novo (expect):** `composeApp/src/commonMain/.../components/PaymentSection.kt`
```kotlin
@Composable
expect fun PaymentSection(
    viewModel: SharedStoreViewModel,
    address: Address,
    totalPrice: Double,
    onPaymentSuccess: () -> Unit,
    onPaymentError: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
)
```

**Arquivo novo (actual Android):** `composeApp/src/androidMain/.../components/PaymentSection.android.kt`
- Usa `rememberPaymentSheet()` do Stripe SDK
- Observa `viewModel.paymentIntentState`
- Botao "Pagar com Cartao" → chama `viewModel.createPaymentIntent()`
- `LaunchedEffect(paymentIntentState)` → quando Success, apresenta o Payment Sheet
- No callback `PaymentSheetResult.Completed` → chama `viewModel.checkoutAfterPayment(address, paymentIntentId)` e `onPaymentSuccess()`
- Exibe mensagem "Pagamento seguro via Stripe" com icone de cadeado

**Arquivo novo (actual iOS):** `composeApp/src/iosMain/.../components/PaymentSection.ios.kt`
- Mantem os campos de cartao manuais atuais (cardNumber, cardHolder, expiryDate, cvv)
- Botao "Confirmar Pagamento" → chama `viewModel.checkoutWithInfo(address, payment)` e `onPaymentSuccess()`
- Preserva comportamento atual para iOS

**Arquivo novo (actual JVM):** `composeApp/src/jvmMain/.../components/PaymentSection.jvm.kt`
- Stub: Text("Pagamento nao disponivel")

**Arquivo novo (actual Web):** `composeApp/src/webMain/.../components/PaymentSection.web.kt`
- Stub: Text("Pagamento nao disponivel")

### Etapa 10: Refatorar CheckoutScreen

**Arquivo modificado:** `composeApp/src/commonMain/.../screens/event/store/CheckoutScreen.kt`

Mudancas:
1. **Remover** estados de cartao: `cardNumber`, `cardHolder`, `expiryDate`, `cvv`
2. **Remover** criacao do `PaymentInfo` e validacao `payment.isValid`
3. **Remover** o Card inteiro "Dados do Cartao" (linhas 292-373)
4. **Remover** funcoes `formatCardNumber` e `formatExpiryDate` (mantidas no actual iOS)
5. **Remover** o botao "Confirmar Pagamento" do bottomBar
6. `isFormValid` passa a ser apenas `address.isValid`
7. **Adicionar** `PaymentSection(...)` entre o card de endereco e o resumo do pedido
8. Mover a acao de `onConfirmOrder` para dentro do `PaymentSection` via callback `onPaymentSuccess`
9. BottomBar simplificada: apenas mostra o total, sem botao

---

## Arquivos Totais

### Novos (13):
| # | Arquivo |
|---|---------|
| 1 | `shared/src/commonMain/.../repository/PaymentRepository.kt` |
| 2 | `shared/src/commonMain/.../repository/PaymentRepositoryFactory.kt` |
| 3 | `shared/src/androidMain/.../repository/FirebasePaymentRepository.kt` |
| 4 | `shared/src/androidMain/.../repository/PaymentRepositoryFactory.android.kt` |
| 5 | `shared/src/iosMain/.../repository/FirebasePaymentRepository.kt` |
| 6 | `shared/src/iosMain/.../repository/PaymentRepositoryFactory.ios.kt` |
| 7 | `shared/src/jvmMain/.../repository/PaymentRepositoryFactory.jvm.kt` |
| 8 | `shared/src/jsMain/.../repository/PaymentRepositoryFactory.js.kt` |
| 9 | `shared/src/wasmJsMain/.../repository/PaymentRepositoryFactory.wasmJs.kt` |
| 10 | `composeApp/src/commonMain/.../components/PaymentSection.kt` |
| 11 | `composeApp/src/androidMain/.../components/PaymentSection.android.kt` |
| 12 | `composeApp/src/iosMain/.../components/PaymentSection.ios.kt` |
| 13 | `composeApp/src/jvmMain/.../components/PaymentSection.jvm.kt` + `webMain` |

### Modificados (10):
| # | Arquivo |
|---|---------|
| 1 | `functions/src/index.ts` |
| 2 | `functions/package.json` |
| 3 | `gradle/libs.versions.toml` |
| 4 | `composeApp/build.gradle.kts` |
| 5 | `composeApp/src/androidMain/.../MainActivity.kt` |
| 6 | `composeApp/src/commonMain/.../screens/event/store/CheckoutScreen.kt` |
| 7 | `shared/src/commonMain/.../store/StoreResult.kt` |
| 8 | `shared/src/commonMain/.../viewmodel/SharedStoreViewModel.kt` |
| 9 | `shared/src/commonMain/.../repository/CartRepository.kt` + implementacoes android/ios |
| 10 | `shared/src/iosMain/.../viewmodel/SharedStoreViewModelHelper.kt` |

---

## Fluxo Final (Android)
1. Usuario preenche endereco no CheckoutScreen
2. Clica "Pagar com Cartao" no PaymentSection
3. App chama Cloud Function → cria PaymentIntent
4. Stripe Payment Sheet abre automaticamente
5. Usuario insere cartao de teste no Payment Sheet
6. Stripe processa pagamento
7. App recebe confirmacao → cria pedido no Firestore com `paymentIntentId`
8. Carrinho limpo → redireciona para tela de confirmacao

## Fluxo Final (iOS)
- Sem mudancas visiveis. Campos de cartao manuais mantidos no actual do iOS.

---

# PARTE 2: Visualizacao de Compras pelo Cliente (App)

## Contexto
O app ja possui "Meus Pedidos" (MyOrdersScreen no Android/Compose e MyOrdersView no iOS/SwiftUI), acessivel via Perfil > Meus Pedidos. A tela lista pedidos do Firestore (`pedidos`) com status, itens e total. Com a integracao Stripe, precisamos:
1. Exibir o metodo de pagamento (Stripe ou manual)
2. Mostrar badge "Pago via Stripe" nos pedidos aprovados

## Etapa 11: Atualizar MyOrdersScreen (Android/Compose)

**Arquivo modificado:** `composeApp/src/commonMain/.../screens/event/store/MyOrdersScreen.kt`

No `OrderCard`, adicionar abaixo do status badge:
- Se o pedido tem `payment.method == "stripe"`: badge verde "Pago via Stripe" com icone de cadeado
- Se nao tem: badge cinza "Pagamento manual"
- Exibir endereco resumido (cidade, UF) se disponivel

## Etapa 12: Atualizar MyOrdersView (iOS/SwiftUI)

**Arquivo modificado:** `iosApp/iosApp/View/Store/MyOrdersView.swift`

Mesmas mudancas da Etapa 11 adaptadas para SwiftUI:
- Badge de metodo de pagamento no `OrderCardView`

## Etapa 13: Atualizar Order model e FirebaseOrderRepository

**Arquivo modificado:** `shared/src/commonMain/.../model/Order.kt`
- Adicionar campo: `val paymentMethod: String = ""` (valores: "stripe", "manual", "")
- Adicionar campo: `val paymentIntentId: String = ""` (ID do Stripe para referencia)

**Arquivo modificado:** `shared/src/androidMain/.../repository/FirebaseOrderRepository.kt`
**Arquivo modificado:** `shared/src/iosMain/.../repository/FirebaseOrderRepository.kt`
- No parsing do documento Firestore, ler `payment.method` e `payment.paymentIntentId`
- Mapear para os novos campos do Order

---

# PARTE 3: Painel Admin de Pedidos (Web)

## Contexto
O site admin em `/Users/cgalves/Desktop/backendprojects/mlb_api_webpage_github/MakeLifeBetterWebPage/` usa vanilla JavaScript + Firebase. Possui tabs para Eventos, Produtos, Banners, etc. mas **NAO tem secao de pedidos**. Precisamos criar uma aba "Pedidos" para o admin ver todas as compras, especialmente as aprovadas pelo Stripe, e poder atualizar o status (ex: marcar como "Enviado").

**Projeto web:**
- Framework: Vanilla JS (ES6 modules) + HTML/CSS
- Firebase: Firestore, Auth, Storage
- Admin auth: whitelist de emails
- Modulos admin em: `js/modules/admin/`
- Servico de pedidos ja existe: `js/services/order.service.js` (tem `getAllOrders()` e `updateOrderStatus()`)
- Constantes de status em: `js/config/constants.js`

## Etapa 14: Adicionar tab "Pedidos" no admin HTML

**Arquivo modificado:** `/Users/cgalves/Desktop/backendprojects/mlb_api_webpage_github/MakeLifeBetterWebPage/index.html`

- Adicionar nova tab "Pedidos" na navegacao (apos "Produtos")
- Adicionar nova section `<div id="admin-pedidos">` com:
  - Header com titulo + contadores (total, pendentes, pagos)
  - Barra de filtros: status (Todos, Pendente, Pago/Confirmado, Enviado, Entregue, Cancelado) + busca por ID/email
  - Tabela/cards de pedidos
  - Area de detalhes do pedido (expandivel)

## Etapa 15: Criar modulo `admin-pedidos.js`

**Arquivo novo:** `/Users/cgalves/Desktop/backendprojects/mlb_api_webpage_github/MakeLifeBetterWebPage/js/modules/admin/admin-pedidos.js`

Funcionalidades:
- `loadAllOrders()` — chama `getAllOrders()` do order.service.js
- `renderOrdersTable(orders)` — renderiza tabela/lista de pedidos
- `filterOrders(status)` — filtra por status
- `searchOrders(query)` — busca por ID ou email
- `showOrderDetail(orderId)` — expande detalhes do pedido:
  - Dados do cliente (userId, email)
  - Endereco de entrega completo
  - Metodo de pagamento (Stripe ou manual)
  - PaymentIntent ID do Stripe (se aplicavel)
  - Lista completa de itens com precos
  - Total
  - Linha do tempo de status
- `updateStatus(orderId, newStatus)` — altera status com dropdown:
  - pending → paid → shipped → delivered
  - Qualquer → cancelled
- `exportOrders()` — (opcional) exportar pedidos para CSV

**Cada linha/card de pedido mostra:**
| Coluna | Conteudo |
|--------|----------|
| ID | Primeiros 8 chars do orderId |
| Data | DD/MM/YYYY HH:mm |
| Cliente | Email do usuario |
| Itens | Quantidade total de itens |
| Total | R$ formatado |
| Pagamento | Badge: "Stripe" (verde) ou "Manual" (cinza) |
| Status | Badge colorido com dropdown para atualizar |
| Acoes | Botao "Ver detalhes" |

## Etapa 16: Registrar tab no admin-tabs.js

**Arquivo modificado:** `/Users/cgalves/Desktop/backendprojects/mlb_api_webpage_github/MakeLifeBetterWebPage/js/modules/admin/admin-tabs.js`

- Registrar nova tab "pedidos" com callback `loadAllOrders()`
- Seguir padrao existente das outras tabs

## Etapa 17: CSS para a secao de pedidos

**Arquivo novo ou modificado:** CSS correspondente (seguir padrao modular do projeto em `css/`)

- Estilos para tabela de pedidos
- Badges de status (cores por status)
- Badge de metodo de pagamento (Stripe verde, manual cinza)
- Dropdown de acao para alterar status
- Area de detalhe expandivel
- Responsivo (mobile-friendly)

## Etapa 18: Alinhar status entre App e Web

**Importante:** Os status sao diferentes entre KMP e Web:

| KMP App | Web Store | Significado |
|---------|-----------|-------------|
| PENDING | pending | Aguardando |
| CONFIRMED | paid | Pagamento confirmado |
| PROCESSING | — | Em preparo |
| SHIPPED | shipped | Enviado |
| DELIVERED | delivered | Entregue |
| CANCELLED | cancelled | Cancelado |

**Decisao:** Padronizar no Firestore usando os valores do Web (lowercase):
- `pending`, `paid`, `shipped`, `delivered`, `cancelled`
- Adicionar `processing` como status extra (entre paid e shipped)

**Arquivo modificado:** `js/config/constants.js` — adicionar `processing: { label: 'Em Preparo', class: 'processing' }`
**Arquivo modificado:** `shared/src/commonMain/.../model/Order.kt` — garantir mapeamento correto

---

## Arquivos Totais (Parte 2 + 3)

### Novos:
| # | Arquivo | Projeto |
|---|---------|---------|
| 1 | `js/modules/admin/admin-pedidos.js` | Web Admin |
| 2 | CSS para pedidos (novo ou existente) | Web Admin |

### Modificados:
| # | Arquivo | Projeto |
|---|---------|---------|
| 1 | `index.html` (nova tab) | Web Admin |
| 2 | `js/modules/admin/admin-tabs.js` | Web Admin |
| 3 | `js/config/constants.js` (status processing) | Web Admin |
| 4 | `MyOrdersScreen.kt` (badge pagamento) | KMP App |
| 5 | `MyOrdersView.swift` (badge pagamento) | KMP App |
| 6 | `Order.kt` (novos campos) | KMP App |
| 7 | `FirebaseOrderRepository.kt` Android (parsing) | KMP App |
| 8 | `FirebaseOrderRepository.kt` iOS (parsing) | KMP App |

---

## Fluxo Completo: Do Pagamento a Entrega

```
1. Cliente no app → preenche endereco → Stripe Payment Sheet
2. Stripe processa → pagamento aprovado
3. Pedido criado no Firestore: status "paid", payment.method: "stripe"
4. Cliente ve pedido em "Meus Pedidos" com badge "Pago via Stripe"
5. Admin abre painel web → tab "Pedidos"
6. Admin ve pedido com badge verde "Stripe" e status "Pago"
7. Admin prepara produto → muda status para "Em Preparo"
8. Admin envia produto → muda status para "Enviado"
9. Cliente ve atualizacao de status no app
10. Admin confirma entrega → muda status para "Entregue"
```

---

## Verificacao Completa

### Parte 1 (Stripe):
1. Deploy da Cloud Function: `cd functions && npm install && npm run build && firebase deploy --only functions`
2. Build Android e testar no emulador
3. Usar cartao de teste Stripe: `4242 4242 4242 4242`, validade futura, CVV qualquer
4. Verificar pedido criado no Firestore com `payment.method: "stripe"`
5. Testar cartao recusado: `4000 0000 0000 0002` → verificar mensagem de erro
6. Verificar que iOS continua funcionando normalmente com checkout manual

### Parte 2 (Meus Pedidos no App):
7. Apos compra, ir em Perfil > Meus Pedidos
8. Verificar que o pedido aparece com badge "Pago via Stripe"
9. Verificar que status e total estao corretos

### Parte 3 (Admin Web):
10. Abrir admin web (index.html) e logar como admin
11. Ir na aba "Pedidos" → verificar que o pedido do teste aparece
12. Verificar badge verde "Stripe" e status "Pago"
13. Clicar "Ver detalhes" → confirmar itens, endereco, paymentIntentId
14. Alterar status para "Enviado" → verificar no Firestore
15. Voltar ao app → verificar que status atualizou em "Meus Pedidos"

## Cartoes de Teste Stripe

| Cartao | Numero | Resultado |
|--------|--------|-----------|
| Visa (sucesso) | `4242 4242 4242 4242` | Pagamento aprovado |
| Visa (recusado) | `4000 0000 0000 0002` | Cartao recusado |
| 3D Secure | `4000 0027 6000 3184` | Requer autenticacao 3DS |
| Fundos insuficientes | `4000 0000 0000 9995` | Fundos insuficientes |

- **Data de validade:** Qualquer data futura (ex: `12/34`)
- **CVC:** Qualquer 3 digitos (ex: `123`)

---

# EXECUCAO: Registro do que foi feito (02/03/2026)

## Resumo

Todas as etapas da **Parte 1** (Stripe Payment Sheet no Android) foram implementadas e testadas com sucesso. Alem disso, foi adicionada uma protecao de login obrigatorio antes de acessar o carrinho.

---

## Passo a Passo Executado

### 1. Configuracao das API Keys do Stripe

**Stripe Dashboard** (https://dashboard.stripe.com) > Developers > API keys (Test mode ativado):
- Copiada a **Publishable key** (`pk_test_...`)
- Copiada a **Secret key** (`sk_test_...`)

**Publishable key no app (client-side):**
Adicionada em `local.properties`:
```properties
STRIPE_PUBLISHABLE_KEY=pk_test_...
```
Esta chave e lida pelo `composeApp/build.gradle.kts` e injetada no `BuildConfig` do Android em tempo de compilacao.

**Secret key no Firebase (server-side):**
```bash
firebase functions:secrets:set STRIPE_SECRET_KEY
# Colar: sk_test_...
```

**Comandos uteis para gerenciar secrets:**
```bash
# Ver o valor atual da secret
firebase functions:secrets:access STRIPE_SECRET_KEY

# Atualizar a secret (cria nova versao e pergunta se quer re-deployar)
firebase functions:secrets:set STRIPE_SECRET_KEY

# Deletar uma secret (ex: criada com typo)
firebase functions:secrets:destroy NOME_DA_SECRET
```

### 2. Build e Deploy da Cloud Function

**Problema encontrado:** O Firebase CLI usa um npm embutido (`firepit`) que tem um bug com `stdin`, causando erro no `predeploy`:
```
npm ERR! Cannot read properties of undefined (reading 'stdin')
Error: functions predeploy error: Command terminated with non-zero exit code 1
```

**Solucao (workaround):**
1. Compilar manualmente com o npm do sistema:
   ```bash
   cd functions/
   npm install
   npm run build
   ```
2. Temporariamente remover o `predeploy` do `firebase.json`:
   ```json
   "predeploy": []
   ```
3. Fazer o deploy:
   ```bash
   firebase deploy --only functions
   ```
4. Restaurar o `predeploy` no `firebase.json`:
   ```json
   "predeploy": [
     "npm --prefix \"$RESOURCE_DIR\" run build"
   ]
   ```

**Alternativa mais simples:** Quando se atualiza a secret com `firebase functions:secrets:set`, o Firebase pergunta se quer re-deployar a function automaticamente — nesse caso nao precisa rodar o deploy manual.

### 3. Erro Inicial: `INTERNAL` ao clicar "Pagar com Cartao"

**Sintoma:** Ao clicar no botao "Pagar com Cartao" no app, aparecia "Erro ao criar PaymentIntent: INTERNAL".

**Causa:** A `STRIPE_SECRET_KEY` havia sido configurada com a **publishable key** (`pk_test_...`) em vez da **secret key** (`sk_test_...`). O log do Firebase mostrou:
```
StripeAuthenticationError: Invalid API Key provided: pk_test_...smKR
statusCode: 401
```

**Diagnostico:**
```bash
firebase functions:log --only createPaymentIntent
```

**Correcao:** Reconfiguramos a secret com a chave correta (`sk_test_...`):
```bash
firebase functions:secrets:set STRIPE_SECRET_KEY
# Colar: sk_test_... (NAO pk_test_!)
```
O Firebase re-deployou a function automaticamente e o pagamento passou a funcionar.

**Licao:** `pk_test_` = Publishable (client/app). `sk_test_` = Secret (server/Cloud Function). Nunca confundir.

### 4. Teste de Pagamento com Sucesso

Apos corrigir a secret key, o fluxo completo funcionou:
1. App Android → Adicionar produto ao carrinho → Checkout
2. Preencher endereco → Clicar "Pagar com Cartao"
3. Stripe Payment Sheet abriu com sucesso
4. Cartao de teste `4242 4242 4242 4242` → Pagamento aprovado
5. Pedido criado no Firestore com `payment.method: "stripe"`

### 5. Protecao de Login Obrigatorio no Carrinho

**Problema:** Era possivel fazer compra sem estar logado (pedido ficava como "anonymous").

**Solucao:** Adicionada verificacao de login antes de acessar o carrinho, reutilizando o padrao existente do `GuestProfileScreen` (mesmo usado no Chat e Perfil).

**Arquivo modificado:** `composeApp/src/commonMain/.../screens/HomeScreen.kt`

No bloco `StoreScreenState.CART`, adicionado:
```kotlin
StoreScreenState.CART -> {
    if (currentUser == null && isLoginRequired) {
        GuestProfileScreen(
            onLoginClick = onLoginClick,
            onBackClick = {
                storeScreenState = StoreScreenState.LIST
            }
        )
    } else {
        CartScreen(...)
    }
}
```

Agora ao clicar no carrinho sem estar logado, aparece a tela "Voce esta como visitante" com botao "Entrar na conta".

---

## Status Atual das Etapas

| Etapa | Descricao | Status |
|-------|-----------|--------|
| 1 | Cloud Function `createPaymentIntent` | CONCLUIDO |
| 2 | `PaymentRepository` no shared module | CONCLUIDO |
| 3 | `PaymentIntentResult` no StoreResult | CONCLUIDO |
| 4 | Novos metodos no SharedStoreViewModel | CONCLUIDO |
| 5 | `checkoutWithStripe` no CartRepository | CONCLUIDO |
| 6 | SharedStoreViewModelWrapper (iOS) | CONCLUIDO |
| 7 | Dependencia Stripe SDK no Android | CONCLUIDO |
| 8 | Inicializar Stripe na MainActivity | CONCLUIDO |
| 9 | Componente expect/actual PaymentSection | CONCLUIDO |
| 10 | Refatorar CheckoutScreen | CONCLUIDO |
| 11 | Atualizar MyOrdersScreen (badge pagamento) | PENDENTE |
| 12 | Atualizar MyOrdersView iOS (badge pagamento) | PENDENTE |
| 13 | Atualizar Order model e FirebaseOrderRepository | PENDENTE |
| 14-18 | Painel Admin de Pedidos (Web) | PENDENTE |
| Extra | Login obrigatorio antes do carrinho | CONCLUIDO |

## Comandos de Referencia Rapida

```bash
# === SECRETS ===
firebase functions:secrets:set STRIPE_SECRET_KEY     # Configurar/atualizar
firebase functions:secrets:access STRIPE_SECRET_KEY   # Ver valor atual
firebase functions:secrets:destroy NOME              # Deletar secret

# === DEPLOY (workaround para bug do predeploy) ===
cd functions && npm install && npm run build && cd ..
# Temporariamente: "predeploy": [] no firebase.json
firebase deploy --only functions
# Restaurar: "predeploy": ["npm --prefix \"$RESOURCE_DIR\" run build"]

# === LOGS ===
firebase functions:log --only createPaymentIntent           # Logs recentes
firebase functions:log --only createPaymentIntent --follow  # Logs em tempo real

# === LISTAR FUNCTIONS DEPLOYADAS ===
firebase functions:list
```
