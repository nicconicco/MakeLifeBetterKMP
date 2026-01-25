package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Question
import com.carlosnicolaugalves.makelifebetter.model.QuestionReply
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock

class FirebaseQuestionRepository : QuestionRepository {

    private val firestore by lazy { Firebase.firestore }
    private val questionsCollection by lazy { firestore.collection("duvidas") }

    override suspend fun getQuestions(): Result<List<Question>> {
        return try {
            val querySnapshot = questionsCollection
                .orderBy("timestamp", Direction.DESCENDING)
                .get()

            val questions = querySnapshot.documents.mapNotNull { doc ->
                try {
                    Question(
                        id = doc.id,
                        title = doc.get<String>("title"),
                        description = doc.get<String>("description"),
                        author = doc.get<String>("author"),
                        replies = doc.get<Int>("replies"),
                        timestamp = doc.get<Long>("timestamp")
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addQuestion(author: String, title: String, description: String): Result<Question> {
        return try {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val docRef = questionsCollection.add(
                mapOf(
                    "title" to title,
                    "description" to description,
                    "author" to author,
                    "replies" to 0,
                    "timestamp" to timestamp
                )
            )

            val question = Question(
                id = docRef.id,
                title = title,
                description = description,
                author = author,
                replies = 0,
                timestamp = timestamp
            )

            Result.success(question)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteQuestion(questionId: String): Result<Unit> {
        return try {
            questionsCollection.document(questionId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReplies(questionId: String): Result<List<QuestionReply>> {
        return try {
            val querySnapshot = questionsCollection
                .document(questionId)
                .collection("respostas")
                .orderBy("timestamp", Direction.ASCENDING)
                .get()

            val replies = querySnapshot.documents.mapNotNull { doc ->
                try {
                    QuestionReply(
                        id = doc.id,
                        questionId = questionId,
                        author = doc.get<String>("author"),
                        content = doc.get<String>("content"),
                        timestamp = doc.get<Long>("timestamp")
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(replies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addReply(questionId: String, author: String, content: String): Result<QuestionReply> {
        return try {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val docRef = questionsCollection
                .document(questionId)
                .collection("respostas")
                .add(
                    mapOf(
                        "author" to author,
                        "content" to content,
                        "timestamp" to timestamp
                    )
                )

            // Incrementar contador de respostas na pergunta
            questionsCollection.document(questionId).update(
                mapOf("replies" to FieldValue.increment(1))
            )

            val reply = QuestionReply(
                id = docRef.id,
                questionId = questionId,
                author = author,
                content = content,
                timestamp = timestamp
            )

            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReply(questionId: String, replyId: String): Result<Unit> {
        return try {
            questionsCollection
                .document(questionId)
                .collection("respostas")
                .document(replyId)
                .delete()

            // Decrementar contador de respostas na pergunta
            questionsCollection.document(questionId).update(
                mapOf("replies" to FieldValue.increment(-1))
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
