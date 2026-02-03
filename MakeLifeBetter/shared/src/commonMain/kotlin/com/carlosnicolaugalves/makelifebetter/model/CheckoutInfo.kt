package com.carlosnicolaugalves.makelifebetter.model

data class CheckoutInfo(
    val address: Address,
    val payment: PaymentInfo
)

data class Address(
    val street: String = "",
    val number: String = "",
    val complement: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = ""
) {
    val isValid: Boolean
        get() = street.isNotBlank() &&
                number.isNotBlank() &&
                neighborhood.isNotBlank() &&
                city.isNotBlank() &&
                state.isNotBlank() &&
                zipCode.length >= 8

    val formatted: String
        get() = buildString {
            append("$street, $number")
            if (complement.isNotBlank()) append(" - $complement")
            append("\n$neighborhood")
            append("\n$city - $state")
            append("\nCEP: $zipCode")
        }
}

data class PaymentInfo(
    val cardNumber: String = "",
    val cardHolder: String = "",
    val expiryDate: String = "",
    val cvv: String = ""
) {
    val isValid: Boolean
        get() = cardNumber.replace(" ", "").length == 16 &&
                cardHolder.isNotBlank() &&
                expiryDate.length == 5 &&
                cvv.length >= 3

    val maskedCardNumber: String
        get() = if (cardNumber.length >= 4) {
            "**** **** **** ${cardNumber.takeLast(4)}"
        } else {
            cardNumber
        }
}
