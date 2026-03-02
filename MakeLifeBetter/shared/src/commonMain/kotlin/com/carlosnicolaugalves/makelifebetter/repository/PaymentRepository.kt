package com.carlosnicolaugalves.makelifebetter.repository

data class PaymentIntentData(
    val paymentIntentClientSecret: String,
    val ephemeralKey: String,
    val customerId: String
)

interface PaymentRepository {
    suspend fun createPaymentIntent(amountInCentavos: Int): Result<PaymentIntentData>
}
