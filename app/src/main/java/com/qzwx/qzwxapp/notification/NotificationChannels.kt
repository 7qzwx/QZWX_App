package com.qzwx.qzwxapp.notification

import android.app.*
import android.content.*
import android.os.*
import android.media.AudioAttributes
import android.provider.Settings

/**
 * 通知渠道的创建
 */
object NotificationChannels {
    const val REMINDER_CHANNEL_ID = "reminder_channel"
    const val OTHER_CHANNEL_ID = "other_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // 删除旧的渠道以确保设置更新
            try {
                notificationManager.deleteNotificationChannel(REMINDER_CHANNEL_ID)
                notificationManager.deleteNotificationChannel(OTHER_CHANNEL_ID)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // 创建打卡提醒渠道
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "打卡提醒",
                NotificationManager.IMPORTANCE_HIGH  // 提高重要性
            ).apply {
                description = "提醒用户打卡的通道"
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                
                // 设置通知声音
                val soundUri = Settings.System.DEFAULT_NOTIFICATION_URI
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(reminderChannel)
            
            // 创建其他提醒渠道
            val otherChannel = NotificationChannel(
                OTHER_CHANNEL_ID,
                "其他提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "其他类型的提醒"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(otherChannel)
        }
    }
} 