package com.qzwx.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
/**通知渠道的创建   */
object NotificationChannels {
    const val REMINDER_CHANNEL_ID = "reminder_channel"
    const val OTHER_CHANNEL_ID = "other_channel"

    fun createNotificationChannels(context : Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            // 创建打卡提醒渠道
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "打卡提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "提醒用户打卡的通道"
            }
            notificationManager.createNotificationChannel(reminderChannel)
            // 创建其他提醒渠道
            val otherChannel = NotificationChannel(
                OTHER_CHANNEL_ID,
                "其他提醒",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "其他类型的提醒"
            }
            notificationManager.createNotificationChannel(otherChannel)
        }
    }
}