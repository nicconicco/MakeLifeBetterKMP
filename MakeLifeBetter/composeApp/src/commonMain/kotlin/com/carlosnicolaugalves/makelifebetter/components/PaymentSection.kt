package com.carlosnicolaugalves.makelifebetter.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.carlosnicolaugalves.makelifebetter.model.Address
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedStoreViewModel

@Composable
expect fun PaymentSection(
    viewModel: SharedStoreViewModel,
    address: Address,
    totalPrice: Double,
    onPaymentSuccess: () -> Unit,
    onPaymentError: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
)
