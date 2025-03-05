package com.qzwx.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.myapplication.components.BottomNavItem
import com.qzwx.myapplication.components.CustomBottomNavigationBar
import com.qzwx.myapplication.navigation.NavGraph
import com.qzwx.myapplication.notification.NotificationChannels
import com.qzwx.myapplication.notification.NotificationHelper

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState) // 创建通知渠道
        NotificationChannels.createNotificationChannels(this)
        enableEdgeToEdge()
        // 设置定时提醒
        NotificationHelper.setDailyReminder(this, enabled = true)
        setContent {
            QZWX_AppTheme {
                Surface(modifier = Modifier.systemBarsPadding()) {
                    MyApp()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    // 定义底部导航栏的选项
    val items = listOf(
        BottomNavItem("主页", R.drawable.svg_all, "home"),
        BottomNavItem("音乐", R.drawable.svg_music1, "music"),
        BottomNavItem("我的", R.drawable.svg_my, "profile")
    )
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomNavigationBar(
                items = items,
                navController = navController
            )
        }
    ) {
        NavGraph(
            navController = navController
        )
    }
}