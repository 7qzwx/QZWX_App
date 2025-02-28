package com.qzwx.myapplication.notification

/**通知接受逻辑   */

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            val notification = NotificationCompat.Builder(context, NotificationChannels.REMINDER_CHANNEL_ID)
                .setSmallIcon(com.qzwx.core.R.drawable.qzxt_qdxt)
                .setContentTitle("打卡提醒")
                .setContentText("快来APP打卡签到啦！")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context).notify(1, notification)
        }
    }
}