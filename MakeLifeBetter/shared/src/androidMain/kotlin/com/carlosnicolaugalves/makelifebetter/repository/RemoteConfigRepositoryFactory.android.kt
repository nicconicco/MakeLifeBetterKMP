package com.carlosnicolaugalves.makelifebetter.repository

actual fun createRemoteConfigRepository(): RemoteConfigRepository = FirebaseRemoteConfigRepository()
