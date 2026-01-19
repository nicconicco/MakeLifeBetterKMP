# Configuracao do Firebase

Este projeto usa o Firebase para autenticacao e armazenamento de usuarios.
Siga os passos abaixo para configurar o Firebase.

## 1. Criar projeto no Firebase Console

1. Acesse [Firebase Console](https://console.firebase.google.com/)
2. Clique em "Adicionar projeto"
3. De um nome ao projeto (ex: "MakeLifeBetter")
4. Siga as instrucoes para criar o projeto

## 2. Ativar Authentication

1. No menu lateral, clique em "Authentication"
2. Clique em "Comecar"
3. Na aba "Sign-in method", ative "Email/Senha"

## 3. Criar Firestore Database

1. No menu lateral, clique em "Firestore Database"
2. Clique em "Criar banco de dados"
3. Escolha o modo (producao ou teste)
4. Selecione a regiao mais proxima

### Regras do Firestore

Configure as regras de seguranca em "Regras":

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      allow read: if request.auth != null &&
        resource.data.username == request.resource.data.username;
    }
  }
}
```

## 4. Configurar Android

1. No Firebase Console, clique no icone do Android
2. Preencha:
   - Nome do pacote: `com.carlosnicolaugalves.makelifebetter`
   - Apelido do app: MakeLifeBetter (opcional)
   - SHA-1: (opcional, necessario para Google Sign-In)
3. Baixe o arquivo `google-services.json`
4. Coloque o arquivo em: `composeApp/`

```
MakeLifeBetter/
└── composeApp/
    └── google-services.json  <-- AQUI
```

## 5. Configurar iOS

1. No Firebase Console, clique no icone do iOS
2. Preencha:
   - ID do pacote: `com.carlosnicolaugalves.makelifebetter.MakeLifeBetter`
3. Baixe o arquivo `GoogleService-Info.plist`
4. Coloque o arquivo em: `iosApp/iosApp/`

```
MakeLifeBetter/
└── iosApp/
    └── iosApp/
        └── GoogleService-Info.plist  <-- AQUI
```

5. Adicione o arquivo ao projeto Xcode:
   - Abra o projeto no Xcode
   - Arraste `GoogleService-Info.plist` para o grupo `iosApp`
   - Marque "Copy items if needed"
   - Certifique-se de que o target `iosApp` esta selecionado

## 6. Verificar Configuracao

### Android
O arquivo `google-services.json` deve estar em `composeApp/` e conter:
- `project_id`
- `client_id`
- `api_key`

### iOS
O arquivo `GoogleService-Info.plist` deve conter:
- `GOOGLE_APP_ID`
- `API_KEY`
- `PROJECT_ID`

## Estrutura de Dados no Firestore

Colecao `users`:
```
users/
  {userId}/
    id: string
    username: string
    email: string
    createdAt: number (timestamp)
```

## Testando

1. Execute o app no Android ou iOS
2. Crie uma conta com:
   - Username
   - Email
   - Senha (minimo 6 caracteres)
3. Faca login com o username e senha
4. Verifique no Firebase Console:
   - Authentication > Users (usuario criado)
   - Firestore > users (documento criado)
