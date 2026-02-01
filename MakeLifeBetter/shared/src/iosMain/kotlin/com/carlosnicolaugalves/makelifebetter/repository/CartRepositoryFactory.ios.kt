package com.carlosnicolaugalves.makelifebetter.repository

actual fun createCartRepository(): CartRepository = FirebaseCartRepository()
