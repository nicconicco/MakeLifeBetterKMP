package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Question
import kotlinx.datetime.Clock

class LocalQuestionRepository : QuestionRepository {

    private val questions = mutableListOf<Question>()

    override suspend fun getQuestions(): Result<List<Question>> {
        return Result.success(questions.sortedByDescending { it.timestamp })
    }

    override suspend fun addQuestion(author: String, title: String, description: String): Result<Question> {
        val question = Question(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            title = title,
            description = description,
            author = author,
            replies = 0,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        questions.add(question)
        return Result.success(question)
    }

    override suspend fun deleteQuestion(questionId: String): Result<Unit> {
        questions.removeAll { it.id == questionId }
        return Result.success(Unit)
    }
}
