package com.qzwx.qzwxapp.notification

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * 通知权限处理帮助类
 */
object NotificationPermissionHelper {

    /**
     * 检查通知权限，针对不同Android版本提供不同的处理方式
     * @param context 上下文
     * @param requestPermission 是否请求权限（如果缺少）
     * @return 是否拥有通知权限
     */
    fun checkNotificationPermission(context: Context, requestPermission: Boolean = false): Boolean {
        // 对于Android 13+，需要专门检查POST_NOTIFICATIONS权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 检查是否拥有POST_NOTIFICATIONS权限
            val hasPostNotificationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            
            // 如果没有权限且需要请求，则发起请求
            if (!hasPostNotificationPermission && requestPermission) {
                if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        100
                    )
                } else {
                    openNotificationSettings(context)
                }
            }
            
            return hasPostNotificationPermission
        } 
        // Android 13以下，检查系统通知开关是否已开启
        else {
            val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
            if (!notificationsEnabled && requestPermission) {
                openNotificationSettings(context)
            }
            return notificationsEnabled
        }
    }
    
    /**
     * 打开应用通知设置页面
     */
    fun openNotificationSettings(context: Context) {
        try {
            Toast.makeText(context, "请开启通知权限以接收提醒", Toast.LENGTH_LONG).show()
            
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0及以上，跳转到应用通知设置页面
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                // Android 8.0以下，跳转到应用详情页面
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果上面的Intent失败，尝试打开应用详情页
            try {
                val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(appSettingsIntent)
            } catch (e2: Exception) {
                e2.printStackTrace()
                Toast.makeText(context, "无法打开设置页面，请手动前往设置开启通知权限", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    /**
     * 检查精确闹钟权限
     * @param context 上下文
     * @param requestPermission 是否请求权限（如果缺少）
     * @return 是否拥有精确闹钟权限
     */
    fun checkExactAlarmPermission(context: Context, requestPermission: Boolean = true): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                if (requestPermission) {
                    try {
                        Toast.makeText(context, "需要精确闹钟权限才能设置提醒", Toast.LENGTH_LONG).show()
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // 如果上面的Intent失败，尝试打开应用详情页
                        try {
                            val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:${context.packageName}")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(appSettingsIntent)
                        } catch (e2: Exception) {
                            e2.printStackTrace()
                            Toast.makeText(context, "无法打开设置页面，请手动前往设置开启精确闹钟权限", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                return false
            }
            return true
        }
        return true // Android 12 以下版本默认拥有权限
    }
} 