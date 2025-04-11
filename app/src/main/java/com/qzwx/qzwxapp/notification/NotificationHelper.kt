package com.qzwx.qzwxapp.notification

import android.app.*
import android.content.*
import android.os.*
import android.provider.*
import android.widget.*
import java.util.*

/** 定时任务逻辑   */
object NotificationHelper {
    fun setDailyReminder(context: Context, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(context, "需要授予精确闹钟权限才能设置提醒", Toast.LENGTH_LONG).show()
                val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(settingsIntent)
                return
            }
        }

        if (enabled) {
            // 设置早上 8 点提醒
            val morningCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                morningCalendar.timeInMillis,
                pendingIntent
            )

            // 设置晚上 8 点提醒
            val eveningCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 20)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                eveningCalendar.timeInMillis,
                pendingIntent
            )
        } else {
            // 取消提醒
            alarmManager.cancel(pendingIntent)
        }
    }
}