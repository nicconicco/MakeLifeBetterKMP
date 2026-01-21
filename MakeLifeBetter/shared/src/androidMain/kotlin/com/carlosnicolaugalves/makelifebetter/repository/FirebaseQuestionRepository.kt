package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Question
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
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
}
