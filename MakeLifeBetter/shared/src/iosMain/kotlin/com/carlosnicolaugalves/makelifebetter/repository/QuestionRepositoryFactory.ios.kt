package com.carlosnicolaugalves.makelifebetter.repository

actual fun createQuestionRepository(): QuestionRepository = FirebaseQuestionRepository()
