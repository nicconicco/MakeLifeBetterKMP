package com.carlosnicolaugalves.makelifebetter.di

import com.carlosnicolaugalves.makelifebetter.data.repository.createAuthRepository
import com.carlosnicolaugalves.makelifebetter.data.repository.createEventRepository
import com.carlosnicolaugalves.makelifebetter.domain.auth.AuthUseCases
import com.carlosnicolaugalves.makelifebetter.domain.auth.AuthValidator
import com.carlosnicolaugalves.makelifebetter.domain.event.EventUseCases
import com.carlosnicolaugalves.makelifebetter.domain.repository.AuthRepository
import com.carlosnicolaugalves.makelifebetter.domain.repository.EventRepository

fun provideAuthUseCases(
    repository: AuthRepository = createAuthRepository(),
    validator: AuthValidator = AuthValidator()
): AuthUseCases {
    return AuthUseCases(repository, validator)
}

fun provideEventUseCases(
    repository: EventRepository = createEventRepository()
): EventUseCases {
    return EventUseCases(repository)
}
