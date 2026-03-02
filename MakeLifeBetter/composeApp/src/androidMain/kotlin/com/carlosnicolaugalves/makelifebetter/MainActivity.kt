package com.carlosnicolaugalves.makelifebetter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.carlosnicolaugalves.makelifebetter.notification.AndroidNotificationScheduler
import com.carlosnicolaugalves.makelifebetter.theme.ThemeDefaults
import com.stripe.android.PaymentConfiguration
import com.carlosnicolaugalves.makelifebetter.theme.rememberRemoteThemePalettes
import com.carlosnicolaugalves.makelifebetter.theme.toColorScheme
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel
import android.util.Log
import android.widget.Toast

class MainActivity : ComponentActivity() {
    val viewModel = SharedLoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize notification scheduler
        AndroidNotificationScheduler.init(this)

        // Initialize Stripe only when key is present to avoid runtime crash
        val publishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
        if (publishableKey.isNullOrBlank()) {
            Log.e("MainActivity", "Stripe publishable key is missing. Add STRIPE_PUBLISHABLE_KEY to local.properties or env var.")
            Toast.makeText(
                this,
                "Stripe desabilitado: configure STRIPE_PUBLISHABLE_KEY",
                Toast.LENGTH_LONG
            ).show()
        } else {
            PaymentConfiguration.init(applicationContext, publishableKey)
        }

        setContent {
            val palettes by rememberRemoteThemePalettes()
            val isDark = isSystemInDarkTheme()
            val colorScheme = if (isDark) {
                palettes.dark.toColorScheme(isDark)
            } else {
                palettes.light.toColorScheme(isDark)
            }
            AppView(viewModel, colorScheme = colorScheme)
        }
    }
}

@Preview
@Composable
fun AppViewAndroidPreview() {
    val viewModel = SharedLoginViewModel()

    val isDark = true
    val palettes = ThemeDefaults.palettes
    val colorScheme = if (isDark) {
        palettes.dark.toColorScheme(isDark)
    } else {
        palettes.light.toColorScheme(isDark)
    }
    AppView(viewModel, colorScheme = colorScheme)
}
