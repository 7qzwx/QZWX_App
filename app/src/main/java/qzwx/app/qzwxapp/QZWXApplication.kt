package qzwx.app.qzwxapp

import android.app.*
import android.content.*
import android.util.Log
import qzwx.app.qzwxapp.notification.NotificationChannels
import qzwx.app.qzwxapp.notification.NotificationHelper

class QZWXApplication : Application() {
    companion object {
        private lateinit var context: Context

        fun getContext(): Context {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 首先设置context
        context = applicationContext

        // 然后初始化通知渠道
        NotificationChannels.createNotificationChannels(this)

        // 接着启用每日提醒
        NotificationHelper.enableDailyRemindersOnStartup(this)

    }
}
