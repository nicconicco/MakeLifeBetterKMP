package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.model.Address
import com.carlosnicolaugalves.makelifebetter.store.PaymentIntentResult
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedStoreViewModel
import com.carlosnicolaugalves.makelifebetter.BuildConfig
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

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
    val hasPublishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY.isNotBlank()
    val paymentIntentState by viewModel.paymentIntentState.collectAsState()
    var pendingPaymentIntentId by remember { mutableStateOf<String?>(null) }

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                val intentId = pendingPaymentIntentId
                if (intentId != null) {
                    viewModel.checkoutAfterPayment(address, intentId)
                    onPaymentSuccess()
                }
                viewModel.resetPaymentIntentState()
                pendingPaymentIntentId = null
            }
            is PaymentSheetResult.Canceled -> {
                viewModel.resetPaymentIntentState()
                pendingPaymentIntentId = null
            }
            is PaymentSheetResult.Failed -> {
                onPaymentError(result.error.localizedMessage ?: "Erro no pagamento")
                viewModel.resetPaymentIntentState()
                pendingPaymentIntentId = null
            }
        }
    }

    LaunchedEffect(paymentIntentState) {
        val state = paymentIntentState
        if (state is PaymentIntentResult.Success) {
            val data = state.data
            pendingPaymentIntentId = data.paymentIntentClientSecret
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = data.paymentIntentClientSecret,
                configuration = PaymentSheet.Configuration(
                    merchantDisplayName = "Make Life Better",
                    customer = PaymentSheet.CustomerConfiguration(
                        id = data.customerId,
                        ephemeralKeySecret = data.ephemeralKey
                    ),
                    allowsDelayedPaymentMethods = false
                )
            )
        }
    }

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
                    text = "Pagamento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createPaymentIntent() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = enabled && hasPublishableKey && paymentIntentState !is PaymentIntentResult.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (paymentIntentState is PaymentIntentResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Processando...",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Pagar com Cartao",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            if (!hasPublishableKey) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Stripe desabilitado: configure STRIPE_PUBLISHABLE_KEY no build.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (paymentIntentState is PaymentIntentResult.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (paymentIntentState as PaymentIntentResult.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Pagamento seguro via Stripe",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
