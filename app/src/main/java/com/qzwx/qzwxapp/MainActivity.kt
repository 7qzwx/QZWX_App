package com.qzwx.qzwxapp

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.annotation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.navigation.compose.*
import com.qzwx.core.theme.*
import com.qzwx.qzwxapp.navigation.*
import com.qzwx.qzwxapp.notification.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationChannels.createNotificationChannels(this)
        enableEdgeToEdge()
        setContent {
            QZWX_AppTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier
                        .systemBarsPadding()
                        .fillMaxSize()
                ) {
                    NavGraph(navController)
                }
            }
        }
    }
}

