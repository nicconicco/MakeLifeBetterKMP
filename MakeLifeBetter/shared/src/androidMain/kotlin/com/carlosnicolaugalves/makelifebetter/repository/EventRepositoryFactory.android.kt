package com.carlosnicolaugalves.makelifebetter.repository

actual fun createEventRepository(): EventRepository = FirebaseEventRepository()
