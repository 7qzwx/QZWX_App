package com.qzwx.qzwxapp.notification

/**通知接受逻辑   */
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast",
                  "ScheduleExactAlarm",
                  "MissingPermission"
    )
    override fun onReceive(context : Context, intent : Intent) {
        // 判断通知类型，早晚显示不同内容
        val notificationType = intent.getStringExtra("notification_type") ?: "morning"
        val title = "打卡提醒"
        val content = if (notificationType == "morning") {
            "早上好！开始新的一天，请记得打卡签到！"
        } else {
            " 下午了哦，别忘了完成今天的打卡签到！"
        }
         
        // 创建启动应用的意图
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            if (notificationType == "morning") 0 else 1,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notificationManager = NotificationManagerCompat.from(context)
        
        if (notificationManager.areNotificationsEnabled()) {
            val notification = NotificationCompat.Builder(
                context,
                NotificationChannels.REMINDER_CHANNEL_ID
            ).setSmallIcon(com.qzwx.qzwxapp.R.drawable.tp1)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent) // 添加点击操作
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 提高优先级
                .setCategory(NotificationCompat.CATEGORY_REMINDER) // 设置为提醒类别
                .setAutoCancel(true)
                .build()
                
            try {
                notificationManager.notify(
                    if (notificationType == "morning") 0 else 1,
                    notification
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // 在BroadcastReceiver中使用Toast需要特别小心，因为Toast需要UI线程
                try {
                    Toast.makeText(context, "发送通知失败: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
            }
        }
        
        // 设置下一次通知，确保即使应用关闭也能继续接收通知
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("notification_type", notificationType)
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            context,
            if (notificationType == "morning") 0 else 1,
            nextIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 正确设置24小时后的同一时间点
        val nextCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1) // 添加一天
            
            // 根据通知类型设置正确的小时
            if (notificationType == "morning") {
                set(Calendar.HOUR_OF_DAY, 6)
            } else {
                set(Calendar.HOUR_OF_DAY, 18)
            }
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextCalendar.timeInMillis,
                        nextPendingIntent
                    )
                } else {
                    // 对于Android 12+如果无法使用精确闹钟，回退到非精确
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextCalendar.timeInMillis,
                        nextPendingIntent
                    )
                }
            } else alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextCalendar.timeInMillis,
                nextPendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                Toast.makeText(context, "设置下一次通知失败: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
}