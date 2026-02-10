package com.carlosnicolaugalves.makelifebetter.data.repository

import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository

actual fun createEventRepository(): EventRepository = FirebaseEventRepository()
