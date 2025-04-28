package com.qzwx.qzwxapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.qzwx.qzwxapp.notification.NotificationHelper
import com.qzwx.qzwxapp.notification.NotificationPermissionHelper
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyPage(navController: NavController) {
    val context = LocalContext.current
    
    // 需要申请的权限列表
    val permissions = mutableListOf(
        Manifest.permission.READ_MEDIA_IMAGES, // 存储权限（图片）
        Manifest.permission.READ_MEDIA_VIDEO, // 存储权限（视频）
        Manifest.permission.READ_MEDIA_AUDIO, // 存储权限（音频）
        Manifest.permission.MODIFY_AUDIO_SETTINGS // 铃声权限
    )
    
    // 添加Android 13+需要的通知权限
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS) // 通知权限
    }
    
    // 用于刷新权限状态的触发器
    var refreshTrigger by remember { mutableStateOf(0) }
    
    // 检查是否已经授予了常规权限
    val allPermissionsGranted = remember(refreshTrigger) {
        mutableStateOf(
            permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
        )
    }
    
    // 检查闹钟权限状态
    val hasExactAlarmPermission = remember(refreshTrigger) {
        mutableStateOf(checkExactAlarmPermission(context))
    }
    
    // 检查通知权限状态
    val hasNotificationPermission = remember(refreshTrigger) {
        mutableStateOf(NotificationPermissionHelper.checkNotificationPermission(context, false))
    }
    
    // 动态权限申请
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // 更新权限状态
        refreshTrigger += 1
        
        if (allPermissionsGranted.value) {
            // 检查是否需要申请精确闹钟权限
            if (!hasExactAlarmPermission.value) {
                // 申请精确闹钟权限
                NotificationPermissionHelper.checkExactAlarmPermission(context, true)
                
                // 延迟更新权限状态（给用户时间进行操作）
                refreshTrigger += 1
            }
            
            if (hasExactAlarmPermission.value) {
                // 所有权限都已获取，设置每日提醒
                NotificationHelper.setDailyReminder(context, enabled = true)
                Toast.makeText(context, "已设置每日打卡提醒", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // 申请闹钟权限
    val alarmPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // 检查权限是否授予，更新状态
        refreshTrigger += 1
        
        if (hasExactAlarmPermission.value && allPermissionsGranted.value) {
            // 所有权限都已获取，设置每日提醒
            NotificationHelper.setDailyReminder(context, enabled = true)
            Toast.makeText(context, "已设置每日打卡提醒", Toast.LENGTH_SHORT).show()
        }
    }
    
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) { PaddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues)
        ) {
            // 申请权限按钮
            Button(
                onClick = {
                    // 动态申请常规权限
                    permissionLauncher.launch(permissions.toTypedArray())
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text("申请所需权限")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 测试通知按钮
            Button(
                onClick = {
                    // 刷新权限状态
                    refreshTrigger += 1
                    
                    if (!hasNotificationPermission.value) {
                        // 如果没有通知权限，先申请权限
                        Toast.makeText(context, "请先获取通知权限", Toast.LENGTH_SHORT).show()
                        NotificationPermissionHelper.checkNotificationPermission(context, true)
                        
                        // 延迟更新权限状态
                        refreshTrigger += 1
                        return@Button
                    }
                    
                    if (!hasExactAlarmPermission.value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // 如果没有精确闹钟权限，先申请权限
                        Toast.makeText(context, "请先获取精确闹钟权限", Toast.LENGTH_SHORT).show()
                        NotificationPermissionHelper.checkExactAlarmPermission(context, true)
                        
                        // 延迟更新权限状态
                        refreshTrigger += 1
                        return@Button
                    }
                    
                    // 发送测试通知
                    NotificationHelper.sendTestNotification(context, "morning")
                    NotificationHelper.sendTestNotification(context, "evening")
                    Toast.makeText(context, "测试通知已发送", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text("测试通知发送")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 刷新权限状态按钮
            Button(
                onClick = {
                    refreshTrigger += 1
                    Toast.makeText(context, "已刷新权限状态", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text("刷新权限状态")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 状态提示
            Text(
                text = "权限状态: " + if (allPermissionsGranted.value) "已获取" else "未获取",
                modifier = Modifier.padding(8.dp)
            )
            
            Text(
                text = "精确闹钟权限: " + if (hasExactAlarmPermission.value) "已获取" else "未获取",
                modifier = Modifier.padding(8.dp)
            )
            
            Text(
                text = "通知权限: " + if (hasNotificationPermission.value) "已获取" else "未获取",
                modifier = Modifier.padding(8.dp)
            )
        }
        
        // 每次显示页面时检查权限状态
        LaunchedEffect(Unit) {
            refreshTrigger += 1
        }
        
        // 响应权限刷新触发器
        LaunchedEffect(refreshTrigger) {
            // 略微延迟，以确保权限更改已应用
            if (refreshTrigger > 0) {
                delay(500) // 延迟500毫秒再刷新状态
            }
            
            allPermissionsGranted.value = permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
            hasExactAlarmPermission.value = checkExactAlarmPermission(context)
            hasNotificationPermission.value = NotificationPermissionHelper.checkNotificationPermission(context, false)
        }
    }
}

// 检查是否拥有精确闹钟权限
private fun checkExactAlarmPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true // Android 12 以下版本默认拥有权限
    }
}