package com.example.demodatadog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.demodatadog.ui.navigation.AppNav
import com.example.demodatadog.ui.theme.DemoDataDogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoDataDogTheme {
                AppNav()
            }
        }
    }
}
