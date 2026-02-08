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
import com.carlosnicolaugalves.makelifebetter.theme.rememberRemoteThemePalettes
import com.carlosnicolaugalves.makelifebetter.theme.toColorScheme
import com.carlosnicolaugalves.makelifebetter.viewmodel.SharedLoginViewModel

class MainActivity : ComponentActivity() {
    val viewModel = SharedLoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize notification scheduler
        AndroidNotificationScheduler.init(this)

        setContent {
            val palettes by rememberRemoteThemePalettes()
            val isDark = isSystemInDarkTheme()
//            val isDark = true
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
