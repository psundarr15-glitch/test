package com.jeevifood.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jeevifood.customer.ui.nav.AppNavHost
import com.jeevifood.customer.ui.theme.JeeviFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as JeeviFoodApplication

        setContent {
            JeeviFoodTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(app)
                }
            }
        }
    }
}
