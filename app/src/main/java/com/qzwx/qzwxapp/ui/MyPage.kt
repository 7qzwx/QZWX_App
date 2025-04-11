package com.qzwx.qzwxapp.ui

import android.*
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.provider.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.annotation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.core.content.*
import androidx.navigation.*
import com.qzwx.qzwxapp.navigation.*
import com.qzwx.qzwxapp.notification.*

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyPage(navController : NavController) {
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
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
        , bottomBar = { AnimatedNavigationBarExample(navController = navController)}
    ) { PaddingValues ->
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues)
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