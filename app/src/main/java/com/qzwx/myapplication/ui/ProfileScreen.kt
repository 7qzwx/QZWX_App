package com.qzwx.myapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.qzwx.myapplication.notification.NotificationHelper

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    // 需要申请的权限列表
    val permissions = mutableListOf(
        Manifest.permission.READ_MEDIA_IMAGES, // 存储权限（图片）
        Manifest.permission.READ_MEDIA_VIDEO, // 存储权限（视频）
        Manifest.permission.READ_MEDIA_AUDIO, // 存储权限（音频）
        Manifest.permission.POST_NOTIFICATIONS, // 通知权限
        Manifest.permission.SCHEDULE_EXACT_ALARM, // 闹钟权限
        Manifest.permission.MODIFY_AUDIO_SETTINGS // 铃声权限
    )
    // 检查是否已经授予了所有权限
    val allPermissionsGranted = remember {
        mutableStateOf(
            permissions.all { permission ->
                ContextCompat.checkSelfPermission(context,
                    permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
        )
    }
    // 动态权限申请
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        allPermissionsGranted.value = results.all { it.value }
        if (allPermissionsGranted.value) {
            // 权限申请成功后，设置每日提醒
            NotificationHelper.setDailyReminder(context, enabled = true)
        }
    }
    // 检查闹钟权限状态
    val hasExactAlarmPermission = remember {
        mutableStateOf(checkExactAlarmPermission(context))
    }
    // 申请闹钟权限
    val alarmPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // 检查权限是否授予
        hasExactAlarmPermission.value = checkExactAlarmPermission(context)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = {
            if (allPermissionsGranted.value && hasExactAlarmPermission.value) {
                // 如果已经授予了所有权限，直接设置每日提醒
                NotificationHelper.setDailyReminder(context, enabled = true)
            } else {
                // 动态申请权限
                permissionLauncher.launch(permissions.toTypedArray())
                // 如果闹钟权限未授予，跳转到设置页面
                if (!hasExactAlarmPermission.value) {
                    alarmPermissionLauncher.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                }
            }
        }) {
            Text("申请所需权限")
        }
    }
    // 检查权限状态
    LaunchedEffect(Unit) {
        allPermissionsGranted.value = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context,
                permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        hasExactAlarmPermission.value = checkExactAlarmPermission(context)
    }
}

// 检查是否拥有精确闹钟权限
private fun checkExactAlarmPermission(context : Context) : Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true // Android 12 以下版本默认拥有权限
    }
}