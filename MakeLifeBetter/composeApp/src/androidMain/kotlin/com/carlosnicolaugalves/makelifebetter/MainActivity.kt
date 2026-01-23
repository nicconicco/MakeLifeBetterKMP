package com.carlosnicolaugalves.makelifebetter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.carlosnicolaugalves.makelifebetter.notification.AndroidNotificationScheduler
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel

class MainActivity : ComponentActivity() {
    val viewModel = SharedLoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize notification scheduler
        AndroidNotificationScheduler.init(this)

        setContent {
            AppView(viewModel)
        }
    }
}

@Preview
@Composable
fun AppViewAndroidPreview() {
    val viewModel = SharedLoginViewModel()

    AppView(viewModel)
}