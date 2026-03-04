# Plano: GitHub Actions para Build e Distribuicao de APK

## Objetivo
Criar um pipeline de CI/CD com GitHub Actions que, a cada push, compile o app Android e disponibilize uma APK para download e teste no celular.

---

## Analise do Projeto

| Item | Valor |
|------|-------|
| Modulo Android | `composeApp` |
| Build command | `./gradlew :composeApp:assembleDebug` |
| Compile SDK | 36 |
| Min SDK | 26 |
| Target SDK | 36 |
| JDK necessario | 17 |
| Gradle | 8.14.3 (wrapper) |
| AGP | 8.11.2 |

### Arquivos sensíveis necessarios para o build
- `google-services.json` (Firebase - obrigatorio para compilar)
- `local.properties` com:
  - `STRIPE_PUBLISHABLE_KEY` (usada como BuildConfig field)
  - `MAPS_API_KEY` (usada como manifest placeholder)
  - Chaves de signing (apenas para release)

---

## Plano de Implementacao

### Fase 1: APK Debug (implementar agora)

**Arquivo:** `.github/workflows/build-android.yml`

**O que faz:**
1. Roda a cada push em qualquer branch
2. Configura JDK 17, cache do Gradle
3. Reconstroi `google-services.json` a partir de um GitHub Secret
4. Cria `local.properties` com as chaves necessarias (via Secrets)
5. Executa `./gradlew :composeApp:assembleDebug`
6. Faz upload da APK debug como **GitHub Actions Artifact**
7. A APK fica disponivel para download por 14 dias na aba "Actions" do repositorio

**Secrets necessarios no GitHub (Settings > Secrets and variables > Actions):**

| Secret | Conteudo |
|--------|----------|
| `GOOGLE_SERVICES_JSON` | Conteudo completo do `google-services.json` (copiar e colar) |
| `STRIPE_PUBLISHABLE_KEY` | Chave publica do Stripe |
| `MAPS_API_KEY` | Chave da API do Google Maps |

**Como baixar a APK:**
- Va em **Actions** no repositorio GitHub
- Clique no workflow run do commit desejado
- Na secao **Artifacts**, baixe o arquivo `app-debug.apk`
- Instale no celular (precisa habilitar "fontes desconhecidas")

### Fase 2: APK Release Assinada (futuro, opcional)

Para gerar uma APK assinada para release, seria necessario:
1. Converter o keystore `.jks` para Base64 e salvar como Secret
2. Adicionar secrets: `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`
3. Decodificar o keystore no workflow e configurar `local.properties` completo
4. Executar `./gradlew :composeApp:assembleRelease`

### Fase 3: iOS (futuro)

Para iOS, o cenario e mais complexo:
- GitHub Actions oferece macOS runners (necessarios para Xcode)
- Precisa configurar certificados de signing e provisioning profiles
- Pode gerar um `.ipa` para TestFlight ou ad-hoc
- Custo: macOS runners sao **10x mais caros** que Linux runners
- Alternativa: usar Fastlane + match para gerenciar certificados

---

## Workflow Proposto (Fase 1)

```yaml
name: Build Android APK

on:
  push:
    branches: ['**']  # Toda branch
  workflow_dispatch:    # Permite rodar manualmente

jobs:
  build-android:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Create google-services.json
        run: echo '${{ secrets.GOOGLE_SERVICES_JSON }}' > composeApp/google-services.json

      - name: Create local.properties
        run: |
          echo "sdk.dir=/usr/local/lib/android/sdk" > local.properties
          echo "STRIPE_PUBLISHABLE_KEY=${{ secrets.STRIPE_PUBLISHABLE_KEY }}" >> local.properties
          echo "MAPS_API_KEY=${{ secrets.MAPS_API_KEY }}" >> local.properties

      - name: Build Debug APK
        run: ./gradlew :composeApp:assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: composeApp/build/outputs/apk/debug/*.apk
          retention-days: 14
```

---

## Passos para Ativar

1. **Eu crio** o arquivo `.github/workflows/build-android.yml`
2. **Voce configura** os 3 secrets no GitHub:
   - Va em: Repositorio > Settings > Secrets and variables > Actions > New repository secret
   - Adicione `GOOGLE_SERVICES_JSON`, `STRIPE_PUBLISHABLE_KEY`, `MAPS_API_KEY`
3. **Faca push** e o workflow roda automaticamente
4. **Baixe a APK** na aba Actions do GitHub

---

## Observacoes

- A APK **debug** nao precisa de keystore/signing (o Android usa uma chave debug automatica)
- O download do artifact so funciona logado no GitHub (nao gera link publico)
- Se quiser link publico, podemos integrar com Firebase App Distribution no futuro
- O workflow leva aproximadamente 5-10 minutos para completar
