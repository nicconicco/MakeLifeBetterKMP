# Tutorial: Firebase Cloud Functions + Admin com Custom Claims (KMP)

Este tutorial documenta todo o processo de criacao de Cloud Functions no Firebase para gerenciar administradores com Custom Claims, integrado a um projeto Kotlin Multiplatform (KMP).

---

## Indice

1. [Por que migrar de isAdmin no Firestore para Custom Claims?](#1-por-que-migrar)
2. [Estrutura do projeto Functions](#2-estrutura-do-projeto-functions)
3. [Criando as Cloud Functions](#3-criando-as-cloud-functions)
4. [Configuracao do Firebase (Blaze, APIs, Secrets)](#4-configuracao-do-firebase)
5. [Deploy das Functions](#5-deploy-das-functions)
6. [Integrando no app KMP (Android + iOS)](#6-integrando-no-app-kmp)
7. [Problema: enforceAppCheck e o erro "Unauthenticated"](#7-problema-enforceappcheck)
8. [Como funciona o Custom Claims vs isAdmin no Firestore](#8-custom-claims-vs-firestore)
9. [Proximos passos](#9-proximos-passos)

---

## 1. Por que migrar? <a name="1-por-que-migrar"></a>

### Antes: campo `isAdmin` no Firestore

```
Firestore -> users -> nicco@gmail.com
  {
    name: "Nicco",
    isAdmin: true   <- campo no documento
  }
```

**Problemas:**
- Qualquer pessoa com acesso ao Firestore (ou usando a API diretamente) poderia editar `isAdmin: true`
- A validacao era **client-side** — o servidor nao sabia quem era admin
- Nao funcionava com Firestore Security Rules
- Precisava de uma query extra ao Firestore a cada login

### Depois: Custom Claims no Firebase Auth

```
Firebase Auth -> User Token (JWT)
  {
    uid: "abc123",
    email: "nicco@gmail.com",
    admin: true   <- custom claim dentro do token
  }
```

**Vantagens:**
- So o servidor (Cloud Functions) pode alterar claims — impossivel hackear pelo client
- Funciona com Security Rules (`request.auth.token.admin == true`)
- Ja vem no token JWT, sem query extra
- Padrao oficial do Firebase para controle de acesso

---

## 2. Estrutura do projeto Functions <a name="2-estrutura-do-projeto-functions"></a>

Na raiz do projeto KMP, criamos a pasta `functions/`:

```
functions/
  ├── src/
  │   └── index.ts          <- as 5 Cloud Functions
  ├── lib/                  <- JS compilado (gerado pelo build)
  ├── package.json
  ├── tsconfig.json
  └── .gitignore
```

### package.json

```json
{
  "name": "makelifebetter-functions",
  "scripts": {
    "build": "tsc",
    "serve": "npm run build && firebase emulators:start --only functions",
    "deploy": "firebase deploy --only functions"
  },
  "engines": {
    "node": "22"
  },
  "main": "lib/index.js",
  "dependencies": {
    "firebase-admin": "^13.0.0",
    "firebase-functions": "^6.3.0"
  },
  "devDependencies": {
    "typescript": "^5.7.0",
    "firebase-functions-test": "^3.4.0"
  },
  "private": true
}
```

### firebase.json (na raiz do projeto)

```json
{
  "functions": [
    {
      "source": "functions",
      "codebase": "default",
      "ignore": ["node_modules", ".git", "firebase-debug.log", "firebase-debug.*.log", "*.local"],
      "predeploy": ["npm --prefix \"$RESOURCE_DIR\" run build"]
    }
  ]
}
```

### .firebaserc

```json
{
  "projects": {
    "default": "makelifebetter-7f3c9"
  }
}
```

---

## 3. Criando as Cloud Functions <a name="3-criando-as-cloud-functions"></a>

Arquivo: `functions/src/index.ts`

### 3.1 Imports e inicializacao

```typescript
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { initializeApp } from "firebase-admin/app";
import { getAuth } from "firebase-admin/auth";
import { getFirestore } from "firebase-admin/firestore";

initializeApp();

const mapsApiKey = defineSecret("MAPS_API_KEY");
```

**Por que `defineSecret`?** Chaves sensiveis como API keys nao devem ficar hardcoded no codigo. O Firebase Secrets Manager armazena elas de forma segura e injeta no runtime da function.

### 3.2 getSecureConfig — Retorna chaves sensiveis

```typescript
export const getSecureConfig = onCall(
  { secrets: [mapsApiKey] },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Login required.");
    }
    return {
      mapsApiKey: mapsApiKey.value(),
    };
  }
);
```

**Motivo:** A Maps API Key nao deve ficar no codigo-fonte do app (pode ser extraida por decompilacao). Essa function retorna a chave apenas para usuarios autenticados.

### 3.3 bootstrapFirstAdmin — Cria o primeiro admin

```typescript
export const bootstrapFirstAdmin = onCall(
  {},
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Login required.");
    }

    const db = getFirestore();
    const bootstrapDoc = await db.doc("_system/bootstrap").get();

    if (bootstrapDoc.exists) {
      throw new HttpsError("already-exists", "Admin already bootstrapped.");
    }

    await getAuth().setCustomUserClaims(request.auth.uid, { admin: true });

    await db.doc("_system/bootstrap").set({
      adminUid: request.auth.uid,
      createdAt: new Date().toISOString(),
    });

    return { message: "You are now the first admin." };
  }
);
```

**Motivo:** Problema do "ovo e galinha" — precisamos de um admin para criar outros admins, mas nao existe nenhum admin no inicio. Essa function resolve isso permitindo que o primeiro usuario autenticado se torne admin. O documento `_system/bootstrap` garante que so funciona UMA vez.

### 3.4 setAdminClaim — Promove outros usuarios

```typescript
export const setAdminClaim = onCall(
  {},
  async (request) => {
    if (!request.auth?.token.admin) {
      throw new HttpsError("permission-denied", "Only admins can promote users.");
    }

    const { email } = request.data;
    if (!email || typeof email !== "string") {
      throw new HttpsError("invalid-argument", "Email is required.");
    }

    const user = await getAuth().getUserByEmail(email);
    await getAuth().setCustomUserClaims(user.uid, { admin: true });

    return { message: `${email} is now admin.` };
  }
);
```

**Motivo:** Depois que o primeiro admin existe, ele pode promover outros usuarios. A verificacao `request.auth?.token.admin` garante que so admins podem chamar essa function.

### 3.5 adminDeleteAllData — Limpa collections

```typescript
export const adminDeleteAllData = onCall(
  {},
  async (request) => {
    if (!request.auth?.token.admin) {
      throw new HttpsError("permission-denied", "Admin only.");
    }

    const db = getFirestore();
    const collections = [
      "eventos", "lista_geral", "produtos", "categorias",
      "carrinho", "pedidos", "duvidas", "event_location", "banners",
    ];

    let totalDeleted = 0;
    for (const collectionName of collections) {
      const snapshot = await db.collection(collectionName).get();
      const batch = db.batch();
      snapshot.docs.forEach((doc) => {
        batch.delete(doc.ref);
        totalDeleted++;
      });
      await batch.commit();
    }

    return { message: `Deleted ${totalDeleted} documents.` };
  }
);
```

**Motivo:** Operacoes destrutivas devem ser protegidas no servidor. Mesmo que alguem decompile o app, nao consegue deletar dados sem ter o claim `admin: true` no token.

### 3.6 checkAdminStatus — Verifica se e admin

```typescript
export const checkAdminStatus = onCall(
  {},
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "Login required.");
    }
    return {
      isAdmin: request.auth.token.admin === true,
      email: request.auth.token.email,
    };
  }
);
```

---

## 4. Configuracao do Firebase <a name="4-configuracao-do-firebase"></a>

### 4.1 Plano Blaze (obrigatorio)

Cloud Functions **nao funciona** no plano Spark (gratuito). E necessario ativar o plano Blaze (pay-as-you-go) no Firebase Console. Para uso baixo, o custo e praticamente zero.

### 4.2 APIs que precisaram ser habilitadas

Durante o primeiro deploy, varias APIs foram habilitadas automaticamente:
- `cloudfunctions.googleapis.com`
- `cloudbuild.googleapis.com`
- `artifactregistry.googleapis.com`
- `run.googleapis.com`
- `eventarc.googleapis.com`
- `pubsub.googleapis.com`
- `storage.googleapis.com`

Uma API precisou ser habilitada **manualmente**:
- **Secret Manager API** (`secretmanager.googleapis.com`) — necessaria porque usamos `defineSecret("MAPS_API_KEY")`

Para habilitar, acessar:
```
https://console.developers.google.com/apis/api/secretmanager.googleapis.com/overview?project=SEU_PROJECT_ID
```

### 4.3 Configurar o Secret da Maps API Key

```bash
firebase functions:secrets:set MAPS_API_KEY
```

Este comando e interativo — ele pede para digitar o valor da chave. O valor fica armazenado no Google Secret Manager, nunca no codigo.

---

## 5. Deploy das Functions <a name="5-deploy-das-functions"></a>

### Build

```bash
cd functions
npm run build
```

Isso compila o TypeScript para JavaScript na pasta `lib/`.

### Deploy

```bash
firebase deploy --only functions
```

**Problema encontrado:** O `predeploy` script no `firebase.json` executava `npm run build` automaticamente, mas falhava em modo non-interactive com o erro `Cannot read properties of undefined (reading 'stdin')`. A solucao foi:
1. Fazer o build manualmente (`npm run build`)
2. Temporariamente esvaziar o predeploy no `firebase.json`
3. Rodar o deploy
4. Restaurar o predeploy

### Cleanup policy

Apos o primeiro deploy, o Firebase sugeriu configurar uma politica de limpeza de imagens Docker:

```bash
firebase functions:artifacts:setpolicy --force
```

Isso evita acumulo de imagens antigas e custos desnecessarios de storage.

---

## 6. Integrando no app KMP <a name="6-integrando-no-app-kmp"></a>

### 6.1 Adicionar dependencia firebase-functions

**gradle/libs.versions.toml:**
```toml
firebase-functions = { module = "dev.gitlive:firebase-functions", version.ref = "firebase-gitlive" }
```

**shared/build.gradle.kts** (tanto em `androidMain` quanto `iosMain`):
```kotlin
implementation(libs.firebase.functions)
```

**Por que gitlive?** O projeto ja usava `dev.gitlive:firebase-auth` e `dev.gitlive:firebase-firestore` — o wrapper KMP do Firebase. O modulo `firebase-functions` segue o mesmo padrao e funciona em Android e iOS com codigo compartilhado.

### 6.2 Interface AdminRepository

```kotlin
// shared/src/commonMain/kotlin/.../repository/AdminRepository.kt
interface AdminRepository {
    // Firebase Functions
    suspend fun bootstrapFirstAdmin(): Result<String>

    // ... demais metodos
}
```

### 6.3 Implementacao (Android e iOS)

```kotlin
// shared/src/androidMain/kotlin/.../repository/FirebaseAdminRepository.kt
// (identico no iosMain)

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.functions

class FirebaseAdminRepository : AdminRepository {

    private val functions by lazy { Firebase.functions }

    override suspend fun bootstrapFirstAdmin(): Result<String> {
        return try {
            val result = functions.httpsCallable("bootstrapFirstAdmin").invoke()
            val message = result.data<Map<String, String>>()["message"] ?: "Success"
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // ...
}
```

**Atencao na API gitlive:** O metodo correto e `.invoke()`, nao `.call()`. A API do gitlive usa o operador `invoke` do Kotlin como suspend function (diferente do SDK Android nativo que retorna `Task`).

### 6.4 Implementacao stub (JVM/JS/WasmJS)

```kotlin
// shared/src/commonMain/kotlin/.../repository/LocalAdminRepository.kt
override suspend fun bootstrapFirstAdmin(): Result<String> {
    return Result.success("Not supported on this platform")
}
```

### 6.5 ViewModel

```kotlin
// shared/src/commonMain/kotlin/.../viewmodel/AdminViewModel.kt

sealed class BootstrapAdminState {
    object Idle : BootstrapAdminState()
    object Loading : BootstrapAdminState()
    data class Success(val message: String) : BootstrapAdminState()
    data class Error(val message: String) : BootstrapAdminState()
}

class AdminViewModel(...) {
    private val _bootstrapAdminState = MutableStateFlow<BootstrapAdminState>(BootstrapAdminState.Idle)
    val bootstrapAdminState: StateFlow<BootstrapAdminState> = _bootstrapAdminState.asStateFlow()

    fun bootstrapFirstAdmin() {
        viewModelScope.launch {
            _bootstrapAdminState.value = BootstrapAdminState.Loading
            adminRepository.bootstrapFirstAdmin()
                .onSuccess { message ->
                    _bootstrapAdminState.value = BootstrapAdminState.Success(message)
                }
                .onFailure { exception ->
                    _bootstrapAdminState.value = BootstrapAdminState.Error(
                        exception.message ?: "Erro ao configurar admin"
                    )
                }
        }
    }

    fun resetBootstrapState() {
        _bootstrapAdminState.value = BootstrapAdminState.Idle
    }
}
```

### 6.6 SecretScreen (UI)

Na SecretScreen, adicionamos:
- Um **Card** com botao "Tornar-me Admin"
- Um **AlertDialog de confirmacao** antes de executar
- Um **AlertDialog de sucesso** com a mensagem retornada pela function
- **CircularProgressIndicator** durante o loading
- Observacao do estado via `LaunchedEffect(bootstrapAdminState)`

---

## 7. Problema: enforceAppCheck <a name="7-problema-enforceappcheck"></a>

### O que aconteceu

Inicialmente, todas as functions tinham `enforceAppCheck: true`:

```typescript
export const bootstrapFirstAdmin = onCall(
  { enforceAppCheck: true },  // <- causava o erro
  async (request) => { ... }
);
```

Ao chamar a function pelo app, retornava **"Unauthenticated"**.

### Por que

O `enforceAppCheck: true` exige que o app envie um **token do App Check** junto com cada request. O App Check e um servico do Firebase que verifica se a request vem de um app legitimo (nao de um bot ou script).

Como o app **nao tinha App Check configurado**, a request era rejeitada antes mesmo de verificar a autenticacao — resultando no erro "Unauthenticated".

### Solucao

Removemos `enforceAppCheck: true` de todas as functions:

```typescript
export const bootstrapFirstAdmin = onCall(
  {},  // <- sem enforceAppCheck
  async (request) => { ... }
);
```

E fizemos redeploy. A autenticacao continua protegida pelo `request.auth` check dentro de cada function.

### Quando usar enforceAppCheck?

Quando voce configurar o Firebase App Check no seu app (tanto Android quanto iOS), pode reativar o `enforceAppCheck: true` para uma camada extra de seguranca. Isso impede que scripts externos chamem suas functions mesmo com tokens de autenticacao validos.

---

## 8. Custom Claims vs isAdmin no Firestore <a name="8-custom-claims-vs-firestore"></a>

| | Antes (Firestore) | Agora (Custom Claims) |
|---|---|---|
| **Onde fica** | Documento no Firestore | Token JWT do Firebase Auth |
| **Quem valida** | O app (client-side) | O servidor (Cloud Functions) |
| **Pode ser hackeado?** | Sim, editando o Firestore | Nao, so o servidor pode alterar claims |
| **Funciona em Security Rules?** | Nao | Sim (`request.auth.token.admin == true`) |
| **Performance** | Precisa ler Firestore | Ja vem no token, sem query extra |
| **Quem altera** | Qualquer um com acesso ao Firestore | Apenas `firebase-admin` no servidor |

### Exemplo de Security Rules com Custom Claims

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /eventos/{doc} {
      allow read: if request.auth != null;
      allow write: if request.auth.token.admin == true;
    }

    match /produtos/{doc} {
      allow read: if request.auth != null;
      allow write: if request.auth.token.admin == true;
    }

    // Bloqueia acesso ao documento de bootstrap
    match /_system/{doc} {
      allow read, write: if false;
    }
  }
}
```

---

## 9. Proximos passos <a name="9-proximos-passos"></a>

1. **Configurar App Check** no Android e iOS para reativar `enforceAppCheck: true`
2. **Migrar operacoes do SecretScreen** para usar as Cloud Functions (`adminDeleteAllData`) em vez de deletar diretamente do client
3. **Aplicar Security Rules** no Firestore usando `request.auth.token.admin == true`
4. **Chamar `checkAdminStatus`** no app para verificar se o usuario e admin antes de mostrar a SecretScreen (em vez de usar senha hardcoded "0000")
5. **Usar `getSecureConfig`** para obter a Maps API Key em vez de deixa-la no codigo do app

---

## Resumo dos arquivos alterados

| Arquivo | O que mudou |
|---|---|
| `functions/src/index.ts` | 5 Cloud Functions criadas |
| `functions/package.json` | Dependencias do projeto Functions |
| `firebase.json` | Configuracao do deploy |
| `.firebaserc` | Projeto Firebase vinculado |
| `gradle/libs.versions.toml` | Adicionado `firebase-functions` (gitlive) |
| `shared/build.gradle.kts` | Dependencia `firebase-functions` no Android e iOS |
| `AdminRepository.kt` (interface) | Novo metodo `bootstrapFirstAdmin()` |
| `FirebaseAdminRepository.kt` (Android) | Implementacao com `Firebase.functions` |
| `FirebaseAdminRepository.kt` (iOS) | Mesma implementacao |
| `LocalAdminRepository.kt` | Stub para JVM/JS/WasmJS |
| `AdminViewModel.kt` | Novo state `BootstrapAdminState` + funcao |
| `SecretScreen.kt` | Botao "Tornar-me Admin" com dialogs |
