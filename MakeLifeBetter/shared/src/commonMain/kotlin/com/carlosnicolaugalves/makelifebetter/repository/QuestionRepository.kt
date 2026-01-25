package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Question
import com.carlosnicolaugalves.makelifebetter.model.QuestionReply

interface QuestionRepository {
    suspend fun getQuestions(): Result<List<Question>>
    suspend fun addQuestion(author: String, title: String, description: String): Result<Question>
    suspend fun deleteQuestion(questionId: String): Result<Unit>

    suspend fun getReplies(questionId: String): Result<List<QuestionReply>>
    suspend fun addReply(questionId: String, author: String, content: String): Result<QuestionReply>
    suspend fun deleteReply(questionId: String, replyId: String): Result<Unit>
}
