package com.qzwx.qzwxapp.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import java.util.Calendar

/** 定时任务逻辑   */
object NotificationHelper {
    fun setDailyReminder(context: Context, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val morningIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("notification_type", "morning")
        }
        val pendingMorningIntent = PendingIntent.getBroadcast(
            context,
            0,
            morningIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val eveningIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("notification_type", "evening")
        }
        val pendingEveningIntent = PendingIntent.getBroadcast(
            context,
            1,
            eveningIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 检查通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+需要请求POST_NOTIFICATIONS权限
            checkNotificationPermission(context)
        }

        // 检查精确闹钟权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(context, "需要授予精确闹钟权限才能设置提醒", Toast.LENGTH_LONG).show()
                try {
                    val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(settingsIntent)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 如果上面的Intent失败，尝试打开应用详情页
                    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(appSettingsIntent)
                    return
                }
            }
        }

        if (enabled) {
            // 设置早上 6 点提醒
            val morningCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 6)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            morningCalendar.timeInMillis,
                            pendingMorningIntent
                        )
                    } else {
                        // 对于Android 12+如果无法使用精确闹钟，回退到非精确
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            morningCalendar.timeInMillis,
                            pendingMorningIntent
                        )
                    }
                } else
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        morningCalendar.timeInMillis,
                        pendingMorningIntent
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "设置早上提醒失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            // 设置晚上 6 点提醒
            val eveningCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            eveningCalendar.timeInMillis,
                            pendingEveningIntent
                        )
                    } else {
                        // 对于Android 12+如果无法使用精确闹钟，回退到非精确
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            eveningCalendar.timeInMillis,
                            pendingEveningIntent
                        )
                    }
                } else
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        eveningCalendar.timeInMillis,
                        pendingEveningIntent
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "设置晚上提醒失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            
            // 保存通知启用状态
            val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
            prefs.edit() {
                putBoolean(
                    "notifications_enabled",
                    true
                )
            }
            
        } else {
            // 取消提醒
            alarmManager.cancel(pendingMorningIntent)
            alarmManager.cancel(pendingEveningIntent)
            
            // 保存通知禁用状态
            val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
            prefs.edit() {
                putBoolean(
                    "notifications_enabled",
                    false
                )
            }
        }
    }
    
    // 检查通知权限（适用于Android 13+）
    private fun checkNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                // 在实际使用中，需要在Activity中请求权限
                // 这里仅提供提示
                Toast.makeText(context, "请在设置中允许应用发送通知", Toast.LENGTH_LONG).show()
                try {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 如果上面的Intent失败，尝试打开应用详情页
                    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(appSettingsIntent)
                }
            }
        }
    }
    
    // 应用启动时自动启用每日提醒
    fun enableDailyRemindersOnStartup(context: Context) {
        // 检查通知是否已启用
        val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true) // 默认启用
        
        if (notificationsEnabled) {
            setDailyReminder(context, true)
        }
    }
    
    // 创建测试通知的方法，用于快速验证通知功能
    fun sendTestNotification(context: Context, notificationType: String = "morning") {
        // 检查通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission(context)
        }
        
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Toast.makeText(context, "通知权限未开启，请在设置中开启", Toast.LENGTH_LONG).show()
            return
        }
        
        val title = "测试通知"
        val content = if (notificationType == "morning") {
            "这是早上通知测试"
        } else {
            "这是晚上通知测试"
        }
        
        // 创建启动应用的意图
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(
            context,
            NotificationChannels.REMINDER_CHANNEL_ID
        ).setSmallIcon(com.qzwx.core.R.drawable.qzxt_qdxt)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)  // 添加点击操作
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 提高优先级
            .setCategory(NotificationCompat.CATEGORY_REMINDER) // 设置为提醒类别
            .setAutoCancel(true)
            .build()
            
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(
                if (notificationType == "morning") 100 else 101, // 使用不同ID避免与定时通知冲突
                notification
            )
            Toast.makeText(context, "已发送测试通知", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "发送通知失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}