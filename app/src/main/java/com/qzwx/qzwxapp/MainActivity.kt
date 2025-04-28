package com.qzwx.qzwxapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.qzwx.core.theme.QZWX_AppTheme
import com.qzwx.qzwxapp.navigation.NavGraph
import com.qzwx.qzwxapp.notification.NotificationChannels
import com.qzwx.qzwxapp.notification.NotificationHelper
import com.qzwx.qzwxapp.notification.NotificationPermissionHelper

class MainActivity : ComponentActivity() {
    
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 只创建通知渠道，不自动申请权限
        NotificationChannels.createNotificationChannels(this)
        
        enableEdgeToEdge()
        setContent {
            QZWX_AppTheme {
                val navController = rememberNavController()
                    NavGraph(navController)
            }
        }
    }
    
    // 权限请求回调处理
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == 100) { // 通知权限
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了通知权限，初始化通知系统
                NotificationHelper.enableDailyRemindersOnStartup(this)
            } else {
                // 用户拒绝了权限，尝试引导用户去设置页面开启权限
                NotificationPermissionHelper.openNotificationSettings(this)
            }
        }
    }
}

