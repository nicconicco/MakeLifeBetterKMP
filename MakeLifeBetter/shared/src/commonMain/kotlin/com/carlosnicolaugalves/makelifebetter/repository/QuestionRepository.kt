package com.carlosnicolaugalves.makelifebetter.repository

import com.carlosnicolaugalves.makelifebetter.model.Question

interface QuestionRepository {
    suspend fun getQuestions(): Result<List<Question>>
    suspend fun addQuestion(author: String, title: String, description: String): Result<Question>
    suspend fun deleteQuestion(questionId: String): Result<Unit>
}
