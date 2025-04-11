package com.qzwx.qzwxapp.notification

/**通知接受逻辑   */
import android.content.*
import androidx.core.app.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context,intent : Intent) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            val notification = NotificationCompat.Builder(
                context,
                NotificationChannels.REMINDER_CHANNEL_ID
            ).setSmallIcon(com.qzwx.core.R.drawable.qzxt_qdxt).setContentTitle("打卡提醒")
                .setContentText("快来APP打卡签到啦！")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true).build()
            NotificationManagerCompat.from(context).notify(
                1,
                notification
            )
        }
    }
}