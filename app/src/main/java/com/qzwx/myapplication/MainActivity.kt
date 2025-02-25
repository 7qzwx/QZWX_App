package com.qzwx.myapplication

import BottomNavItem
import CustomBottomNavigationBar
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.feature_qiandaosystem.broadcast.setReminder
import com.qzwx.myapplication.navigation.NavGraph

// CoreActivity.kt（主应用模块）
class MainActivity : ComponentActivity() {
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 权限已授予，设置提醒
            setReminder(this, enabled = true)
        } else {
            // 权限被拒绝，无法设置提醒
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QZWX_AppTheme {
                Surface(modifier = Modifier.systemBarsPadding()) {
                    MyApp()
                }
            }
        }
        // 检查并请求通知权限（适用于 Android 13 及以上版本）
        checkAndRequestNotificationPermission()
    }

    private fun createNotificationChannel() {
        val channelId = "reminder_channel"
        val channelName = "打卡提醒"
        val channelDescription = "提醒用户打卡的通道"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
        )
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // 请求通知权限
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // 权限已开启，设置提醒
                setReminder(this, enabled = true)
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
    // 系统状态栏修改
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color(0xFF310952) // 设置为透明，或你需要的颜色
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
