package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.model.Address
import com.carlosnicolaugalves.makelifebetter.model.PaymentInfo
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedStoreViewModel

@Composable
actual fun PaymentSection(
    viewModel: SharedStoreViewModel,
    address: Address,
    totalPrice: Double,
    onPaymentSuccess: () -> Unit,
    onPaymentError: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val payment = PaymentInfo(
        cardNumber = cardNumber,
        cardHolder = cardHolder,
        expiryDate = expiryDate,
        cvv = cvv
    )

    val isPaymentValid = payment.isValid

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dados do Cartao",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.replace(" ", "").length <= 16) cardNumber = formatCardNumber(it) },
                label = { Text("Numero do Cartao") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("0000 0000 0000 0000") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cardHolder,
                onValueChange = { cardHolder = it.uppercase() },
                label = { Text("Nome no Cartao") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("NOME COMO ESTA NO CARTAO") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { if (it.length <= 5) expiryDate = formatExpiryDate(it) },
                    label = { Text("Validade") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("MM/AA") }
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 4) cvv = it.filter { c -> c.isDigit() } },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation(),
                    placeholder = { Text("***") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.checkoutWithInfo(address, payment)
                    onPaymentSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = enabled && isPaymentValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Confirmar Pagamento",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

private fun formatCardNumber(input: String): String {
    val digitsOnly = input.filter { it.isDigit() }
    return digitsOnly.chunked(4).joinToString(" ")
}

private fun formatExpiryDate(input: String): String {
    val digitsOnly = input.filter { it.isDigit() }
    return when {
        digitsOnly.length <= 2 -> digitsOnly
        else -> "${digitsOnly.take(2)}/${digitsOnly.drop(2).take(2)}"
    }
}
