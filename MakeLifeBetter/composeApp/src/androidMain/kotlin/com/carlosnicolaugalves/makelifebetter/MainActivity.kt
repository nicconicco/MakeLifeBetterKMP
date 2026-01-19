package com.carlosnicolaugalves.makelifebetter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.carlosnicolaugalves.makelifebetter.viewmodel.LoginViewModel
import kotlin.getValue

class MainActivity : ComponentActivity() {
//    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    val loginViewModel = LoginViewModel()
    App()
}