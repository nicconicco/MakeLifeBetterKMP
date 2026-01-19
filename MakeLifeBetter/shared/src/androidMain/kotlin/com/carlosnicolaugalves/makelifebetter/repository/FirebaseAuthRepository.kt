package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock

class FirebaseAuthRepository : AuthRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val usersCollection = firestore.collection("users")

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            // Primeiro, busca o email pelo username no Firestore
            val querySnapshot = usersCollection
                .where { "username" equalTo username }
                .get()

            val userDoc = querySnapshot.documents.firstOrNull()

            if (userDoc == null) {
                return Result.failure(Exception("Usuario nao encontrado"))
            }

            val email = userDoc.get<String>("email")

            // Faz login com email e senha no Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Erro ao fazer login"))

            val user = User(
                id = firebaseUser.uid,
                username = userDoc.get<String>("username"),
                email = email,
                passwordHash = ""
            )

            Result.success(user)
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("password", ignoreCase = true) == true -> "Senha incorreta"
                e.message?.contains("user", ignoreCase = true) == true -> "Usuario nao encontrado"
                e.message?.contains("network", ignoreCase = true) == true -> "Erro de conexao"
                else -> e.message ?: "Erro ao fazer login"
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            // Verifica se username ja existe
//            val existingUserQuery = usersCollection
//                .where { "username" equalTo username }
//                .get()
//
//            if (existingUserQuery.documents.isNotEmpty()) {
//                return Result.failure(Exception("Nome de usuario ja existe"))
//            }

            // Cria usuario no Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user
                ?:
                return Result.failure(Exception("Erro ao criar usuario"))

            val user = User(
                id = firebaseUser.uid,
                username = username,
                email = email,
                passwordHash = ""
            )

            // Salva dados adicionais no Firestore
            usersCollection.document(firebaseUser.uid).set(
                mapOf(
                    "id" to user.id,
                    "username" to user.username,
                    "email" to user.email,
                    "createdAt" to Clock.System.now().toEpochMilliseconds()
                )
            )

            Result.success(user)
        } catch (e: Exception) {
            val message = when {
                e.message?.contains("email", ignoreCase = true) == true -> "Email ja cadastrado"
                e.message?.contains("password", ignoreCase = true) == true -> "Senha deve ter pelo menos 6 caracteres"
                e.message?.contains("network", ignoreCase = true) == true -> "Erro de conexao"
                else -> e.message ?: "Erro ao registrar"
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun recoverPassword(email: String): Result<String> {
        return try {
            // Verifica se o email existe
            val userQuery = usersCollection
                .where { "email" equalTo email }
                .get()

            if (userQuery.documents.isEmpty()) {
                return Result.failure(Exception("Email nao encontrado"))
            }

            // Envia email de recuperacao de senha
            auth.sendPasswordResetEmail(email)

            Result.success("Email de recuperacao enviado para $email")
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Erro ao recuperar senha"))
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            val querySnapshot = usersCollection
                .where { "email" equalTo email }
                .get()

            val userDoc = querySnapshot.documents.firstOrNull()

            userDoc?.let {
                User(
                    id = it.get<String>("id"),
                    username = it.get<String>("username"),
                    email = it.get<String>("email"),
                    passwordHash = ""
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return try {
            val querySnapshot = usersCollection
                .where { "username" equalTo username }
                .get()

            val userDoc = querySnapshot.documents.firstOrNull()

            userDoc?.let {
                User(
                    id = it.get<String>("id"),
                    username = it.get<String>("username"),
                    email = it.get<String>("email"),
                    passwordHash = ""
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
