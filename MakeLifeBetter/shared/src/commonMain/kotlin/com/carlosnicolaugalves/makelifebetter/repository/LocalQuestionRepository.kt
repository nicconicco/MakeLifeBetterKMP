package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Question
import com.carlosnicolaugalves.makelifebetter.model.QuestionReply
import kotlinx.datetime.Clock

class LocalQuestionRepository : QuestionRepository {

    private val questions = mutableListOf<Question>()
    private val replies = mutableMapOf<String, MutableList<QuestionReply>>()

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
        replies.remove(questionId)
        return Result.success(Unit)
    }

    override suspend fun getReplies(questionId: String): Result<List<QuestionReply>> {
        val questionReplies = replies[questionId] ?: emptyList()
        return Result.success(questionReplies.sortedBy { it.timestamp })
    }

    override suspend fun addReply(questionId: String, author: String, content: String): Result<QuestionReply> {
        val reply = QuestionReply(
            id = Clock.System.now().toEpochMilliseconds().toString(),
            questionId = questionId,
            author = author,
            content = content,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )

        replies.getOrPut(questionId) { mutableListOf() }.add(reply)

        // Atualizar contador de respostas na pergunta
        val questionIndex = questions.indexOfFirst { it.id == questionId }
        if (questionIndex >= 0) {
            val question = questions[questionIndex]
            questions[questionIndex] = question.copy(replies = question.replies + 1)
        }

        return Result.success(reply)
    }

    override suspend fun deleteReply(questionId: String, replyId: String): Result<Unit> {
        replies[questionId]?.removeAll { it.id == replyId }

        // Decrementar contador de respostas na pergunta
        val questionIndex = questions.indexOfFirst { it.id == questionId }
        if (questionIndex >= 0) {
            val question = questions[questionIndex]
            questions[questionIndex] = question.copy(replies = maxOf(0, question.replies - 1))
        }

        return Result.success(Unit)
    }
}
