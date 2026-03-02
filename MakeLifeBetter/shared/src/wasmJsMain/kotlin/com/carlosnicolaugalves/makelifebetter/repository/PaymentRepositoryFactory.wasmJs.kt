package com.carlosnicolaugalves.makelifebetter.repository

actual fun createPaymentRepository(): PaymentRepository = object : PaymentRepository {
    override suspend fun createPaymentIntent(amountInCentavos: Int): Result<PaymentIntentData> {
        return Result.failure(UnsupportedOperationException("Payment not available on this platform"))
    }
}
