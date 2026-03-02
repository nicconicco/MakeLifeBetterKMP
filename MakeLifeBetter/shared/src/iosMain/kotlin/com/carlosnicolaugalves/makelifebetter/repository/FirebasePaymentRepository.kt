package com.carlosnicolaugalves.makelifebetter.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.functions

class FirebasePaymentRepository : PaymentRepository {

    private val functions by lazy { Firebase.functions }

    override suspend fun createPaymentIntent(amountInCentavos: Int): Result<PaymentIntentData> {
        return try {
            val result = functions
                .httpsCallable("createPaymentIntent")
                .invoke(mapOf("amount" to amountInCentavos))

            val data = result.data<Map<String, String>>()

            val paymentIntentClientSecret = data["paymentIntent"]
            val ephemeralKey = data["ephemeralKey"]
            val customerId = data["customer"]

            if (paymentIntentClientSecret == null || ephemeralKey == null || customerId == null) {
                return Result.failure(
                    IllegalStateException("Resposta inválida da função createPaymentIntent")
                )
            }

            Result.success(
                PaymentIntentData(
                    paymentIntentClientSecret = paymentIntentClientSecret,
                    ephemeralKey = ephemeralKey,
                    customerId = customerId
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
