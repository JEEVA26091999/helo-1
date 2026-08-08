package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.HeloApp
import com.example.ui.theme.HeloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.utils.NotificationHelper.createNotificationChannels(this)
        setContent {
            HeloTheme {
                HeloApp()
            }
        }
    }
}
