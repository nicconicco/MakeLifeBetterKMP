package com.carlosnicolaugalves.makelifebetter.repository

actual fun createPaymentRepository(): PaymentRepository = FirebasePaymentRepository()
