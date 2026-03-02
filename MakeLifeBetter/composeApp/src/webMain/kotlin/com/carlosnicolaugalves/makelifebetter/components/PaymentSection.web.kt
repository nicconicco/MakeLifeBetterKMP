package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carlosnicolaugalves.makelifebetter.model.Address
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
    Box(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pagamento nao disponivel nesta plataforma",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
