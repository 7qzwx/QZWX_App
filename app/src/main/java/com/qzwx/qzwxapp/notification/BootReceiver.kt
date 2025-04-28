package com.qzwx.qzwxapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 开机启动接收器，用于设备重启后重新设置通知
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 设备重启后，重新设置所有通知
            NotificationHelper.enableDailyRemindersOnStartup(context)
        }
    }
}